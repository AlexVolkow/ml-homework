package ru.ctdev.ifmo.ml.hw1.classificator

class KFoldDataSetSeparator(
    private val dataSet: Dataset,
    private val nSplits: Int = 3
) : DataSetSeparator {

    override suspend fun separate(): Sequence<Pair<Dataset, Dataset>> = sequence {
        repeat(nSplits) {
            val shuffled = dataSet.shuffle()
            yield(shuffled.filterByIndex(dataSet.size - nSplits))
        }
    }
}