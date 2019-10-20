package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Matrix
import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.asMatrix

data class DataSet(val vectors: Matrix, val classes: Matrix) : Iterable<Vector> by vectors {
    val countClasses: Int
        get() = classes.distinct().size

    fun getClass(idx: Int): Double {
        return classes[idx][0]
    }

    operator fun get(idx: Int): Vector {
        return vectors[idx]
    }

    fun removeRow(row: Int): DataSet {
        return DataSet(vectors.removeRow(row), classes.removeRow(row))
    }

    fun filterByIndex(int: Int): Pair<DataSet, DataSet> {
        val X_train = vectors.take(int)
        val Y_train = classes.take(int)

        val X_test = vectors.takeLast(vectors.size - int)
        val Y_test = classes.take(vectors.size - int)
        return DataSet(
            Matrix(X_train.toMutableList()),
            Matrix(Y_train.toMutableList())
        ) to DataSet(Matrix(X_test.toMutableList()), Matrix(Y_test.toMutableList()))
    }

    fun shuffle(): DataSet {
        val idxs = (0 until vectors.size).toList().shuffled()//.sortedBy { getClass(it) }

        val X = mutableListOf<Vector>()
        val Y = mutableListOf<Vector>()
        for (i in idxs) {
            X.add(vectors[i])
            Y.add(classes[i])
        }
        return DataSet(Matrix(X), Matrix(Y))
    }

    fun singleDataSet(row: Int): DataSet {
        return DataSet(vectors[row].asMatrix(), classes[row].asMatrix())
    }

    fun groupByClass(): Map<Double, List<Vector>> {
        val result = mutableMapOf<Double, MutableList<Vector>>()
        for ((idx, vector) in vectors.withIndex()) {
            val c = getClass(idx)
            if (c !in result) {
                result[c] = mutableListOf()
            }
            result[c]!!.add(vector)
        }

        return result
    }

    companion object {

        fun fromMatrix(matrix: Matrix, classColumn: Int): DataSet {
            val (vectors, classes) = matrix.splitAtColumn(classColumn)
            return DataSet(vectors, classes)
        }
    }
}