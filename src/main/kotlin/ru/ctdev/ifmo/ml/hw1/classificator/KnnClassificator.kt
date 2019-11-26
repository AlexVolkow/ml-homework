package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Distance
import ru.ctdev.ifmo.ml.hw1.math.Kernel
import ru.ctdev.ifmo.ml.hw1.math.Vector

class KnnClassificator(
    private val kernel: Kernel,
    private val distance: Distance,
    private val window: Window
) : Classificator<Vector, Int> {

    private lateinit var dataSet: Dataset

    override suspend fun fit(dataSet: Dataset) {
        this.dataSet = dataSet
    }

    override suspend fun predict(x: Vector): Int {
        val distances = dataSet.associateWith { vector -> distance(vector, x) }
        val width = window.getWidth(distances.values.sorted())
        val byClass = dataSet.groupByClass()
        return byClass.maxBy {
            it.value.sumByDouble { vector -> kernel(distances.getValue(vector) / width) }
        }!!.key.toInt()
    }

    private fun Dataset.groupByClass(): Map<Double, List<Vector>> {
        val result = mutableMapOf<Double, MutableList<Vector>>()
        for ((idx, vector) in this.withIndex()) {
            val c = getClass(idx)
            if (c !in result) {
                result[c] = mutableListOf()
            }
            result[c]!!.add(vector)
        }

        return result
    }
}

