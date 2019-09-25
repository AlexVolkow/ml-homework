package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Distance
import ru.ctdev.ifmo.ml.hw1.math.Kernel
import ru.ctdev.ifmo.ml.hw1.math.Vector

class KnnClassificator(
    private val kernel: Kernel,
    private val distance: Distance
) : Classificator<Vector, Int> {

    override fun getClass(x: Vector, dataSet: DataSet, window: Window): Int {
        val distances = dataSet.associateWith { vector -> distance(vector, x) }
        val width = window.getWidth(distances.values.sorted())
        val byClass = dataSet.groupByClass()
        return byClass.maxBy {
            it.value.sumByDouble { vector -> kernel(distances.getValue(vector) / width) }
        }!!.key.toInt()
    }
}

