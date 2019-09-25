package ru.ctdev.ifmo.ml.hw1.classificator

class LeaveOneOutDataSetSeparator(private val dataSet: DataSet) : DataSetSeparator {

    override suspend fun separate(): Sequence<Pair<DataSet, DataSet>> = sequence {
        for (row in 0 until dataSet.vectors.height) {
            val validating = dataSet.singleDataSet(row)
            val train = dataSet.removeRow(row)
            yield(train to validating)
        }
    }
}