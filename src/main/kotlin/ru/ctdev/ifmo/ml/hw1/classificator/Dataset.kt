package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Matrix
import ru.ctdev.ifmo.ml.hw1.math.Vector

interface Dataset : List<Vector> {
    val countClasses: Int

    val classes: Matrix

    fun getClass(idx: Int): Double

    fun removeRow(row: Int): Dataset

    fun filterByIndex(int: Int): Pair<Dataset, Dataset>

    fun shuffle(): Dataset

    fun singleDataSet(row: Int): Dataset
}