package ru.ctdev.ifmo.ml.hw1.classificator

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.ctdev.ifmo.ml.hw1.math.Vector
import kotlin.math.exp
import kotlin.math.ln

class BayesClassificator(
    val smoothing: Double = 1.0,
    val fine: List<Double>
) : Classificator<Vector, Int> {

    private lateinit var dataset: Dataset
    private val texts: MutableList<Set<Double>> = mutableListOf()

    private val countWordPerClass = Array(2) { mutableSetOf<Double>() }
    private val countTextPerClass = Array(2) { 0 }
    private val textByClass = Array(2) { mutableListOf<Int>() }

    override suspend fun fit(dataSet: Dataset) {
        for (i in 0 until dataSet.size) {
            countTextPerClass[dataSet.getClass(i).toInt()]++
            countWordPerClass[dataSet.getClass(i).toInt()].addAll(dataSet[i])
            textByClass[dataSet.getClass(i).toInt()].add(i)
            texts.add(dataSet[i].toSet())
        }
        this.dataset = dataSet
    }

    override suspend fun predict(x: Vector): Int {
        val xs = x.toSet()

        val y = coroutineScope {
            IntRange(0, 1).map { classId ->
                async {
                    var y = fine[classId] + ln((countTextPerClass[classId].toDouble() / dataset.size))
                    for (word in xs) {
                        val p = (countText(word, classId) + smoothing) /
                                (countTextPerClass[classId] + smoothing * countWordPerClass[classId].size)
                        y += ln(p)
                    }
                    y
                }
            }.awaitAll()
        }

        val mean = y.filter { it != 0.0 }.average()
        val e = Array(2) {
            if (y[it] == 0.0) 0.0 else exp(y[it] - mean)
        }
        val sumE = e.sum()

        return indexOfMax(e.map { it / sumE })!!
    }

    private fun countText(word: Double, classId: Int): Int {
        var cnt = 0
        for (idx in textByClass[classId]) {
            if (word in texts[idx])
                cnt++
        }
        return cnt
    }

    private fun indexOfMax(a: List<Double>): Int? {
        return a.withIndex().maxBy { it.value }?.index
    }
}