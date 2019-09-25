package ru.ctdev.ifmo.ml.hw1.math

data class Vector(val vector: MutableList<Double>) : MutableList<Double> by vector {

    operator fun times(other: Vector): Double {
        var result = 0.0
        for ((idx, value) in other.withIndex()) {
            result += vector[idx] * value
        }
        return result
    }

    operator fun times(const: Double): Vector =
        Vector(map { it * const }.toMutableList())

    operator fun plus(other: Vector): Vector {
        val result = MutableList(vector.size) { 0.0 }
        for ((idx, value) in other.withIndex()) {
            result[idx] = vector[idx] + value
        }
        return Vector(result)
    }

    operator fun minus(other: Vector): Vector {
        val result = MutableList(vector.size) { 0.0 }
        for ((idx, value) in other.withIndex()) {
            result[idx] = vector[idx] - value
        }
        return Vector(result)
    }

    override fun toString(): String {
        return vector.joinToString(separator = ", ", prefix = "(", postfix = ")")
    }
}

fun Vector.asMatrix(): Matrix =
    Matrix(mutableListOf(this))