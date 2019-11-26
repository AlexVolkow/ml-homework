package ru.ctdev.ifmo.ml.hw1.classificator

class LeaveOneOutDataSetSeparator(private val dataSet: Dataset) : DataSetSeparator {

    override suspend fun separate(): Sequence<Pair<Dataset, Dataset>> = sequence {
        for (row in 0 until dataSet.size) {
            val validating = dataSet.singleDataSet(row)
            val train = dataSet.removeRow(row)
            yield(train to validating)
        }
    }
}