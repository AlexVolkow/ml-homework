package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Matrix
import ru.ctdev.ifmo.ml.hw1.math.Vector

data class BatchDataset(private val datasets: List<Dataset>): List<Vector> by datasets[0], Dataset {
    override val countClasses: Int
        get() = datasets[0].countClasses

    override val classes: Matrix
        get() = datasets[0].classes

    override fun getClass(idx: Int): Double {
        throw UnsupportedOperationException("Get class not suported for batch dataset")
    }

    override fun removeRow(row: Int): Dataset {
        val dt = datasets.toMutableList()
        dt.removeAt(row)
        return mergeDataset(dt)
    }

    override fun filterByIndex(int: Int): Pair<Dataset, Dataset> {
        val part1 = datasets.take(int)
        val part2 = datasets.takeLast(size - int)
        return mergeDataset(part1) to mergeDataset(part2)
    }

    override fun shuffle(): Dataset = BatchDataset(datasets.shuffled())

    override fun singleDataSet(row: Int): Dataset = datasets[row]

    override val size: Int
        get() = datasets.size

    fun unite() = mergeDataset(datasets)

    private fun mergeDataset(dts: List<Dataset>): Dataset {
        val vectors = dts.flatten()
        val classes = Vector(dts.flatMap { dt -> dt.mapIndexed { index, vector ->  dt.getClass(index)} }.toMutableList())
        return SimpleDataset(Matrix(vectors.toMutableList()), Matrix(mutableListOf(classes)).transpose())
    }
}