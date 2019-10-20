package ru.ctdev.ifmo.ml.hw1.math

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun Double.isZero(eps: Double = 0.0000001): Boolean {
    return this < eps
}

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

infix fun Double.pow(power: Int) = this.pow(power.toDouble())

fun xabs(x: Double): Double = if (abs(x) < 1.0) 1.0 else 0.0

fun sign(v: Double): Int = when {
    v > 0.0 -> 1
    v < 0.0 -> -1
    else -> 0
}

fun harmonicMean(a: Double, b: Double): Double {
    return if ((a + b).isZero()) {
        0.0
    } else {
        2.0 * (a * b) / (a + b)
    }
}

fun Vector.avg(): Double {
    return sum() / size
}

fun Vector.std(): Double {
    val avg = avg()
    var std = 0.0
    for (j in 0 until size) {
        std += (this[j] - avg) pow 2
    }
    return sqrt(std / size)
}

fun Vector.norm(): Double {
    var res = 0.0
    for (j in 0 until size) {
        res += (this[j]) pow 2
    }
    return sqrt(res)
}

fun <T> List<T>.percentile(percentile: Double) = this[(percentile * size).toInt()]

fun normalize(matrix: Matrix): Matrix {
    val normalized = matrix.copy()
    for (i in 0 until matrix.width) {
        var avg = 0.0
        for (j in 0 until normalized.height) {
            avg += normalized[j][i]
        }
        avg /= normalized.height

        var std = 0.0
        for (j in 0 until normalized.height) {
            std += (normalized[j][i] - avg) pow 2
        }
        std = sqrt(std / normalized.height)

        for (j in 0 until normalized.height) {
            normalized[j][i] = (normalized[j][i] - avg) / std
        }
    }
    return normalized
}