package ru.ctdev.ifmo.ml.hw1.math

data class Matrix(val matrix: MutableList<Vector> = mutableListOf()): MutableList<Vector> by matrix {
    val height: Int
        get() = matrix.size

    val width: Int
        get() = matrix[0].size

    fun transpose(): Matrix {
        val result = emptyMatrix(width, height)
        for (i in 0 until height) {
            for (j in 0 until width) {
                result[j][i] = this[i][j]
            }
        }
        return result
    }

    fun swapRows(i1: Int, i2: Int) {
        val tmp = matrix[i1]
        matrix[i1] = matrix[i2]
        matrix[i2] = tmp
    }

    infix fun concat(other: Matrix): Matrix {
        val result =
            Matrix(ArrayList(matrix.map { Vector(ArrayList(it.vector)) }))
        for (i in 0 until height) {
            result[i].vector.addAll(other.matrix[i].vector)
        }
        return result
    }

    operator fun plus(matrix: Matrix): Matrix {
        val result = emptyMatrix(height, matrix.width)
        for (i in 0 until height) {
            for (j in 0 until width) {
                result[i][j] = this[i][j] + matrix[i][j]
            }
        }
        return result
    }

    operator fun minus(matrix: Matrix): Matrix {
        val result = emptyMatrix(height, matrix.width)
        for (i in 0 until height) {
            for (j in 0 until width) {
                result[i][j] = this[i][j] - matrix[i][j]
            }
        }
        return result
    }

    operator fun times(matrix: Matrix): Matrix {
        val result = emptyMatrix(height, matrix.width)
        for (i in 0 until height) {
            for (j in 0 until matrix.width) {
                for (k in 0 until width) {
                    result[i][j] += this[i][k] * matrix[k][j]
                }
            }
        }
        return result
    }

    operator fun times(num: Double): Matrix {
        val y = emptyMatrix(height, width)
        for ((j, r) in matrix.withIndex()) {
            for ((i, t) in r.vector.withIndex()) {
                y[j][i] = t * num
            }
        }
        return y
    }

    operator fun times(vector: Vector): Vector {
        val result = MutableList(height) { 0.0 }
        for (i in 0 until height) {
            result[i] = this[i] * vector
        }
        return Vector(result)
    }

    fun removeRow(row: Int): Matrix {
        val result = emptyMatrix(height - 1, width)
        for (i in 0 until height) {
            if (i == row) continue
            val idx = if (i > row) i - 1 else i
            for (j in 0 until width) {
                result[idx][j] = this[i][j]
            }
        }
        return result
    }

    fun splitAtColumn(idx: Int): Pair<Matrix, Matrix> {
        val result = emptyMatrix(height, idx) to emptyMatrix(
            height,
            width - idx
        )
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (j < idx) {
                    result.first[i][j] = this[i][j]
                } else {
                    result.second[i][j - idx] = this[i][j]
                }
            }
        }
        return result
    }

    override fun toString(): String {
        return matrix.joinToString(prefix = "[", postfix = "]", separator = ",\n") { it.toString() }
    }
}

fun emptyMatrix(height: Int = 0, width: Int = 0) =
    Matrix(MutableList(height) { Vector(MutableList(width) { 0.0 }) })

fun unitaryMatrix(height: Int, width: Int) =
    Matrix(MutableList(height) { i -> Vector(MutableList(width) { j -> if (i == j) 1.0 else 0.0 }) })