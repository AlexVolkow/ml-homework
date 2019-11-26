package ru.ctdev.ifmo.ml.hw1

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import ru.ctdev.ifmo.ml.hw1.classificator.*
import ru.ctdev.ifmo.ml.hw1.math.Matrix
import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.emptyMatrix
import ru.ctdev.ifmo.ml.hw1.math.toMatrix
import java.io.File
import java.io.PrintWriter

fun arrange(from: Int, to: Int, step: Int): List<Int> {
    return IntProgression.fromClosedRange(from, to, step).toList()
}

@ObsoleteCoroutinesApi
fun main() = runBlocking<Unit>(context = newFixedThreadPoolContext(7, "bayes")) {
    val dt = readTexts()

    val fines = arrange(0, 500, 25)
    val smooths = listOf(0.1, 0.5, 1.0, 3.0, 5.0, 7.0, 10.0, 20.0, 100.0)

    val params = smooths.flatMap { f -> fines.map { s -> f to s } }
    val precision =
        { matrix: Matrix ->
            0.5 * ((matrix[0][0] / (matrix[0][0] + matrix[0][1]))
                    + (matrix[1][1] / (matrix[1][0] + matrix[1][1])))
        }

    val results = mutableListOf<Triple<Double, Int, Double>>()
    for ((smooth, fine) in params) {
        val separator = SplitDataSetSeparator(dt)
        val classifier = BayesClassificator(smooth, listOf(fine.toDouble(), 0.0))
        val analyzer = ClassificatorAnalyzer(classifier)

        val confMatrix = analyzer.getConfMatrix(2, separator)
        val presicion = precision(confMatrix)

        if (confMatrix[0][1] != 0.0) {
            val msg = "No all spam detected: Fine: $fine, Smooth: $smooth, Precision: $presicion"
            println(msg)
        } else {
            println("Fine: $fine, Smooth: $smooth, Precision: $presicion")
            results.add(Triple(presicion, fine, smooth))
        }

    }
    PrintWriter("bayes.txt").use { log ->
        for ((presicion, fine, smooth) in results) {
            log.println("Fine: $fine, Smooth: $smooth, Precision: $presicion")
        }
    }

    val bestParams = results.maxBy { it.first }!!
    println("Best: Fine: ${bestParams.second}, Smooth: ${bestParams.third}, Precision: ${bestParams.first}")
}


fun readTexts(): BatchDataset {
    val dts = mutableListOf<Dataset>()
    File("bayes").walkBottomUp().forEach { part ->
        if (part.isDirectory && part.name.contains("part")) {
            val matrix = emptyMatrix()
            val classes = mutableListOf<Double>()
            println("Read $part")

            part.walkBottomUp().forEach { file ->
                if (file.isFile) {
                    val isSpam = if (file.name.contains("spmsg")) 1.0 else 0.0
                    classes.add(isSpam)

                    val lines = file.readLines()

                    val header = lines[0].drop(9).split(" ").filter { it.isNotBlank() }.map { it.toDouble() }
                    val text = lines[2].split(" ").filter { it.isNotBlank() }.map { it.toDouble() }

                    matrix.add(Vector((header + text).toMutableList()))

                }
            }
            dts.add(SimpleDataset(matrix, Vector(classes).toMatrix().transpose()))
        }
    }
    return BatchDataset(dts)
}

