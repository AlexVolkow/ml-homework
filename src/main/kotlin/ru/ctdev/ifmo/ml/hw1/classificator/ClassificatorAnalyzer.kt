package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.emptyMatrix
import ru.ctdev.ifmo.ml.hw1.math.fscore

class ClassificatorAnalyzer(
    private val classificator: Classificator<Vector, Int>
) {

    suspend fun getScore(countClasses: Int, separator: DataSetSeparator): Double {
        val matrix = emptyMatrix(countClasses, countClasses)
        for ((train, validating) in separator.separate()) {
            classificator.fit(train)
            for ((idx, vector) in validating.withIndex()) {
                val actualClass = if (validating.getClass(idx) == -1.0) 0 else 1
                val predictedClass = classificator.predict(vector)
                matrix[actualClass][predictedClass] += 1.0 / countClasses
            }
        }
        //println(matrix)
        return fscore(matrix)
    }
}