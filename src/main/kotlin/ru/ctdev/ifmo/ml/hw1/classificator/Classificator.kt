package ru.ctdev.ifmo.ml.hw1.classificator

interface Classificator<X, Y> {
    suspend fun fit(dataSet: Dataset)

   suspend fun predict(x: X): Y
}