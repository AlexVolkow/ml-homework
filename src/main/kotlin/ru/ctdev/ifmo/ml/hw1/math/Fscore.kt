package ru.ctdev.ifmo.ml.hw1.math

fun fscore(matrix: Matrix): Double {
    require(matrix.height == matrix.width) { "Matrix must be square" }

    val scores = MutableList(matrix.height) { Score() }
    val all = matrix.sumByDouble { it.sum() }

    for (i in 0 until matrix.height) {
        scores[i].tp = matrix[i][i]
        scores[i].all = 0.0
        for (j in 0 until matrix.width) {
            scores[i].all += matrix[i][j]
            if (i != j) {
                scores[i].fp += matrix[i][j]
                scores[i].fn += matrix[j][i]
            }
        }
        scores[i].tn = all - scores[i].tp - scores[i].fp - scores[i].fn
    }
    var precSum = 0.0
    var recallSum = 0.0
    for (i in 0 until matrix.height) {
        if (!(scores[i].tp + scores[i].fn).isZero()) {
            precSum += scores[i].tp * (scores[i].tp + scores[i].fp) / (scores[i].tp + scores[i].fn)
        }
        recallSum += scores[i].tp
    }
    val prec = precSum / all
    val recall = recallSum / all

    return harmonicMean(prec, recall)
}

private class Score(
    var tp: Double = 0.0,
    var fp: Double = 0.0,
    var fn: Double = 0.0,
    var tn: Double = 0.0,
    var all: Double = 0.0
)
