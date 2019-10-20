package ru.ctdev.ifmo.ml.hw1

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.style.Styler.LegendPosition
import ru.ctdev.ifmo.ml.hw1.classificator.*
import ru.ctdev.ifmo.ml.hw1.math.Distance
import ru.ctdev.ifmo.ml.hw1.math.Kernel
import ru.ctdev.ifmo.ml.hw1.math.distanceByName
import ru.ctdev.ifmo.ml.hw1.math.kernelByName
import ru.ctdev.ifmo.ml.hw1.train.FileTrainResultProvider
import ru.ctdev.ifmo.ml.hw1.train.TrainResult

fun printGraphic(name: String, h: List<Double>, f: List<Double>) {
    val chart = XYChartBuilder()
        .width(800)
        .height(700)
        .xAxisTitle("H")
        .yAxisTitle("F")
        .build()

    chart.styler.defaultSeriesRenderStyle = XYSeriesRenderStyle.Scatter
    chart.styler.isChartTitleVisible = false
    chart.styler.legendPosition = LegendPosition.InsideSW
    chart.styler.markerSize = 16

    chart.addSeries(name, h, f)

    SwingWrapper(chart).displayChart()
}

suspend fun getFixedWindowResults(dataset: DataSet, kernel: Kernel, distance: Distance): List<Pair<Double, Double>> {
    val resultProvider = FileTrainResultProvider("fixed_graphic")
    val trainResults = resultProvider.read()
    if (trainResults.isNotEmpty()) {
        return trainResults.map { it.h to it.fscore }
    }

    val allDistances = allDistances(dataset.vectors, distance)
    val minDistance = allDistances.min()!!
    val maxDistance = allDistances.max()!!

    println("Min: $minDistance, Max: $maxDistance")

    val results = mutableListOf<Pair<Double, Double>>()
    var width = minDistance
    while (width < maxDistance) {
        val window = FixedWindow(width)
        val classificator = KnnClassificator(kernel, distance, window)
        val analyzer = ClassificatorAnalyzer(classificator)
        val separator = LeaveOneOutDataSetSeparator(dataset)

        val fscore = analyzer.getScore(dataset.countClasses, separator)
        resultProvider.save(TrainResult("k", "d", "w", width, fscore))
        println(width to fscore)
        results.add(width to fscore)

        width += 0.45
    }
    resultProvider.close()

    return results
}

suspend fun getVariableWindowResults(dataset: DataSet, kernel: Kernel, distance: Distance): List<Pair<Double, Double>> {
    val resultProvider = FileTrainResultProvider("variable_graphic")
    val trainResults = resultProvider.read()
    if (trainResults.isNotEmpty()) {
        return trainResults.map { it.h to it.fscore }
    }

    val results = mutableListOf<Pair<Double, Double>>()
    for (h in 1 until (dataset.vectors.height / 5) step (10)){
        val window = VariableWindow(h)
        val classificator = KnnClassificator(kernel, distance, window)
        val analyzer = ClassificatorAnalyzer(classificator)
        val separator = LeaveOneOutDataSetSeparator(dataset)

        val width = h.toDouble()
        val fscore = analyzer.getScore(dataset.countClasses, separator)
        resultProvider.save(TrainResult("k", "d", "w", width, fscore))
        println(width to fscore)
        results.add(width to fscore)
    }

    resultProvider.close()

    return results
}

fun main() = runBlocking<Unit> {
    val resultProvider = FileTrainResultProvider("report.txt")
    val results = resultProvider.read()

    val bestResult = results.maxBy { it.fscore }!!

    println("Best: " + bestResult)

    val kernal = kernelByName(bestResult.kernel)
    val distance = distanceByName(bestResult.distance)

    val dataSet = readDataSet()

    val (fixedWindowResults, variableWindowResult) = coroutineScope {
        val fixed = async { getFixedWindowResults(dataSet, kernal, distance) }
        val variable = async { getVariableWindowResults(dataSet, kernal, distance) }
        fixed.await() to variable.await()
    }

    printGraphic("Fixed window", fixedWindowResults.map { it.first }, fixedWindowResults.map { it.second })
    printGraphic("Variable window", variableWindowResult.map { it.first }, variableWindowResult.map { it.second })
}