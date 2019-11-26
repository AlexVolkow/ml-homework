package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Matrix
import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.asMatrix

data class SimpleDataset(val vectors: Matrix, override val classes: Matrix) : List<Vector> by vectors, Dataset {
    override val countClasses: Int
        get() = classes.distinct().size

    override fun getClass(idx: Int): Double {
        return classes[idx][0]
    }

    override fun removeRow(row: Int): SimpleDataset {
        return SimpleDataset(vectors.removeRow(row), classes.removeRow(row))
    }

    override fun filterByIndex(int: Int): Pair<SimpleDataset, SimpleDataset> {
        val X_train = vectors.take(int)
        val Y_train = classes.take(int)

        val X_test = vectors.takeLast(vectors.size - int)
        val Y_test = classes.takeLast(vectors.size - int)
        return SimpleDataset(
            Matrix(X_train.toMutableList()),
            Matrix(Y_train.toMutableList())
        ) to SimpleDataset(Matrix(X_test.toMutableList()), Matrix(Y_test.toMutableList()))
    }

    override fun shuffle(): SimpleDataset {
        val idxs = (0 until vectors.size).toList().shuffled()
        val X = mutableListOf<Vector>()
        val Y = mutableListOf<Vector>()
        for (i in idxs) {
            X.add(vectors[i])
            Y.add(classes[i])
        }
        return SimpleDataset(Matrix(X), Matrix(Y))
    }

    override fun singleDataSet(row: Int): SimpleDataset {
        return SimpleDataset(vectors[row].asMatrix(), classes[row].asMatrix())
    }

    companion object {

        fun fromMatrix(matrix: Matrix, classColumn: Int): SimpleDataset {
            val (vectors, classes) = matrix.splitAtColumn(classColumn)
            return SimpleDataset(vectors, classes)
        }
    }
}