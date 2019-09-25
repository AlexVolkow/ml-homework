package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.emptyMatrix
import ru.ctdev.ifmo.ml.hw1.math.fscore

class ClassificatorAnalyzer(
    private val classificator: Classificator<Vector, Int>
) {

    suspend fun getScore(countClasses: Int, separator: DataSetSeparator, window: Window): Double {
        val matrix = emptyMatrix(countClasses, countClasses)
        for ((train, validating) in separator.separate()) {
            val actualClass = validating.getClass(0)
            val predictedClass = classificator.getClass(validating[0], train, window)
            matrix[(actualClass - 1).toInt()][predictedClass - 1] += 1.0
        }
        return fscore(matrix)
    }
}