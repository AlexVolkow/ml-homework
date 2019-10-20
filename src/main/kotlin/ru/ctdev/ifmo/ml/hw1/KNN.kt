package ru.ctdev.ifmo.ml.hw1

import kotlinx.coroutines.runBlocking
import ru.ctdev.ifmo.ml.hw1.classificator.*
import ru.ctdev.ifmo.ml.hw1.math.*
import ru.ctdev.ifmo.ml.hw1.train.FileTrainResultProvider
import ru.ctdev.ifmo.ml.hw1.train.TrainResult
import ru.ctdev.ifmo.ml.hw1.utils.CSVMatrixReader

val KERNELS = listOf("uniform", "triangular", "epanechnikov", "cosine", "quartic")
val DISTANCES = listOf("euclidean", "chebyshev", "manhattan")

suspend fun fixedWindow(dataset: DataSet, resultProvider: FileTrainResultProvider) {
    println("Fixed")
    for (distanceName in DISTANCES) {
        val distance = distanceByName(distanceName)

        val allDistances = allDistances(dataset.vectors, distance)

        val min = allDistances.percentile(0.1)
        val max = allDistances.percentile(0.9)

        val minWindow = FixedWindow(min)
        val maxWindow = FixedWindow(max)

        for (kernelName in KERNELS) {
            val kernel = kernelByName(kernelName)
            val classificator = KnnClassificator(kernel, distance, minWindow)
            val analyzer = ClassificatorAnalyzer(classificator)

            val separatorMin = LeaveOneOutDataSetSeparator(dataset)

            val resultMin = analyzer.getScore(dataset.countClasses, separatorMin)
            val trainResultMin = TrainResult(kernelName, distanceName, "fixed", min, resultMin)
            resultProvider.save(trainResultMin)
            println(trainResultMin)

            val separatorMax = LeaveOneOutDataSetSeparator(dataset)
            val resultMax = analyzer.getScore(dataset.countClasses, separatorMax)
            val trainResultMax = TrainResult(kernelName, distanceName, "fixed", max, resultMax)
            resultProvider.save(trainResultMax)

            println(trainResultMax)
        }
    }
}

suspend fun variableWindow(dataset: DataSet, resultProvider: FileTrainResultProvider) {
    println("Variable")
    for (kernelName in KERNELS) {
        val kernel = kernelByName(kernelName)
        for (distanceName in DISTANCES) {
            val distance = distanceByName(distanceName)

            for (h in 1 until (dataset.vectors.height / 10) step (20)) {
                println("H: $h")
                val window = VariableWindow(h)
                val classificator = KnnClassificator(kernel, distance, window)
                val analyzer = ClassificatorAnalyzer(classificator)

                val separator = LeaveOneOutDataSetSeparator(dataset)

                val result = analyzer.getScore(dataset.countClasses, separator)
                val trainResult = TrainResult(kernelName, distanceName, "variable", h.toDouble(), result)
                resultProvider.save(trainResult)
                println(trainResult)
            }
        }
    }
}

fun main() = runBlocking<Unit> {
    val normalizedDataSet = readDataSet()

    val resultProvider = FileTrainResultProvider("report_1.txt")

    fixedWindow(normalizedDataSet, resultProvider)
    variableWindow(normalizedDataSet, resultProvider)
    resultProvider.close()

    println("------RESULT-------")
}

fun readDataSet(): DataSet {
    val matrixReader = CSVMatrixReader()
    val table = matrixReader.read("dataset.csv")
    val dataset = DataSet.fromMatrix(table, 4)
    return dataset.copy(vectors = normalize(dataset.vectors))
}

fun allDistances(matrix: Matrix, distance: Distance): List<Double> {
    val distances = mutableListOf<Double>()
    for (i in 0 until matrix.height) {
        for (j in i + 1 until matrix.height) {
            distances.add(distance(matrix[i], matrix[j]))
        }
    }
    return distances.sorted().dropWhile { it.isZero() }
}
