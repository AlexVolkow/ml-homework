package ru.ctdev.ifmo.ml.hw1.classificator

class SplitDataSetSeparator(
    private val dataSet: Dataset,
    private val nSplits: Int = 3
) : DataSetSeparator {

    override suspend fun separate(): Sequence<Pair<Dataset, Dataset>> = sequence {
        yield(dataSet.filterByIndex(dataSet.size - nSplits))
    }
}