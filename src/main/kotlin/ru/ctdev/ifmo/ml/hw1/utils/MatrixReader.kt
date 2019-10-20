package ru.ctdev.ifmo.ml.hw1.utils

import ru.ctdev.ifmo.ml.hw1.math.Matrix
import ru.ctdev.ifmo.ml.hw1.math.Vector
import java.io.FileReader

class CSVMatrixReader {
    fun read(fileName: String): Matrix {
        val reader = FileReader(fileName)
        val matrix = Matrix()
        val lines = reader.readLines()
        for (i in 1 until lines.size) {
            val vector = Vector(lines[i].split(",").map { it.toDouble() }.toMutableList())
            matrix.add(vector)
        }
        matrix.matrix.shuffle()
        return matrix
    }
}