package ru.ctdev.ifmo.ml.hw1.classificator

interface Classificator<X, Y> {
    fun fit(dataSet: DataSet)

    fun predict(x: X): Y
}