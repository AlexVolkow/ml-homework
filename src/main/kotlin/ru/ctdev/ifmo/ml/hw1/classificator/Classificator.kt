package ru.ctdev.ifmo.ml.hw1.classificator

interface Classificator<X, Y> {
    fun getClass(x: X, dataSet: DataSet, window: Window): Y
}