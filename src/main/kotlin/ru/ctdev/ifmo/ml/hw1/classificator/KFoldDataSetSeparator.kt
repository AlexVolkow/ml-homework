package ru.ctdev.ifmo.ml.hw1.classificator

class KFoldDataSetSeparator(
    private val dataSet: DataSet,
    private val nSplits: Int = 5
) : DataSetSeparator {

    override suspend fun separate(): Sequence<Pair<DataSet, DataSet>> = sequence {
        repeat(nSplits) {
            val shuffled = dataSet.shuffle()
            yield(shuffled.filterByIndex(dataSet.vectors.size - nSplits))
        }
    }
}