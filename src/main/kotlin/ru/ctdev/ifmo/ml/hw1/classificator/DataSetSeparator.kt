package ru.ctdev.ifmo.ml.hw1.classificator

interface DataSetSeparator {
   suspend fun separate(): Sequence<Pair<Dataset, Dataset>>
}