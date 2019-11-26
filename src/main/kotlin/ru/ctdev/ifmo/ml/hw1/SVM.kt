package ru.ctdev.ifmo.ml.hw1

import kotlinx.coroutines.runBlocking
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.Styler
import org.knowm.xchart.style.markers.Circle
import ru.ctdev.ifmo.ml.hw1.classificator.*
import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.fscore
import ru.ctdev.ifmo.ml.hw1.utils.CSVMatrixReader
import java.awt.Color

typealias KernelParams = Triple<String, ParameterizedKernel, Double>

fun readDataSet(name: String): SimpleDataset {
    val matrixReader = CSVMatrixReader()
    val table = matrixReader.read(name)
    return SimpleDataset.fromMatrix(table, 2)
}

data class TrainParams(
    val kenelName: String,
    val c: Double,
    val fscore: Double,
    val kernel: ParameterizedKernel,
    val classificator: Classificator<Vector, Int>
) {
    override fun toString(): String {
        return "TrainParams(kenelName='$kenelName', c=$c, fscore=$fscore)"
    }
}

suspend fun printAreas(dt: SimpleDataset, classificator: Classificator<Vector, Int>) {
    val chart = XYChartBuilder()
        .width(800)
        .height(700)
        .xAxisTitle("X")
        .yAxisTitle("Y")
        .build()

    chart.styler.defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Scatter
    chart.styler.isChartTitleVisible = false
    chart.styler.legendPosition = Styler.LegendPosition.InsideSW
    chart.styler.markerSize = 2

    val positives = mutableListOf<Pair<Double, Double>>()
    val negatives = mutableListOf<Pair<Double, Double>>()

    val xMin = dt.vectors.minBy { it[0] }!![0]
    val xMax = dt.vectors.maxBy { it[0] }!![0]

    val yMin = dt.vectors.minBy { it[1] }!![1]
    val yMax = dt.vectors.maxBy { it[1] }!![1]


    var x = xMin
    while (x < xMax) {
        var y = yMin
        while (y < yMax) {
            val vector = Vector(mutableListOf(x, y))
            val predictedClass = classificator.predict(vector)
            if (predictedClass == 0) {
                negatives.add(vector[0] to vector[1])
            } else {
                positives.add(vector[0] to vector[1])
            }
            y += 0.01
        }
        x += 0.01
    }


    var xPositives = positives.map { it.first }
    var yPositives = positives.map { it.second }

    if (xPositives.isNotEmpty() && yPositives.isNotEmpty()) {
        chart.addSeries("positives_area", xPositives, yPositives)
            .setMarkerColor(Color.getHSBColor(0.5F, 0.058823529411765F, 1F))
            .setMarker(Circle())
    }

    var xNegatives = negatives.map { it.first }
    var yNegatives = negatives.map { it.second }

    if (xNegatives.isNotEmpty() && yNegatives.isNotEmpty()) {
        chart.addSeries("negatives_area", xNegatives, yNegatives)
            .setMarkerColor(Color.getHSBColor(0.016666666666667F, 0.11764705882353F, 1F))
            .setMarker(Circle())
    }

    chart.styler.markerSize = 8

    negatives.clear()
    positives.clear()
    for ((idx, vector) in dt.withIndex()) {
        val predictedClass = if (dt.getClass(idx) == -1.0) 0 else 1
        if (predictedClass == 0) {
            negatives.add(vector[0] to vector[1])
        } else {
            positives.add(vector[0] to vector[1])
        }
    }

    xPositives = positives.map { it.first }
    yPositives = positives.map { it.second }

    if (xPositives.isNotEmpty() && yPositives.isNotEmpty()) {
        chart.addSeries("positives", xPositives, yPositives).setMarkerColor(Color.BLUE).setMarker(Circle())
    }

    xNegatives = negatives.map { it.first }
    yNegatives = negatives.map { it.second }

    if (xNegatives.isNotEmpty() && yNegatives.isNotEmpty()) {
        chart.addSeries("negatives", xNegatives, yNegatives).setMarkerColor(Color.RED).setMarker(Circle())
    }

    SwingWrapper(chart).displayChart()
}

suspend fun printBestForKernel(kernelName: String, dt: SimpleDataset, trains: List<TrainParams>) {
    val bestParams = trains.filter { it.kenelName == kernelName }.maxBy { it.fscore }!!
    println(bestParams)

    val classificator = SVMClassificator(bestParams.kernel, bestParams.c)

    classificator.fit(dt)

    printAreas(dt, classificator)
}

fun main() = runBlocking {
    val cs = listOf(1.0, 10.0, 25.0, 100.0)
    val params = listOf(
        cs.map { c ->
            Triple("Linear", LinearKernel(), c)
        },
        cs.map { c ->
            Triple("Rbf", RadialBaseKernel(), c)
        }
    ).flatten()

    testDataSet("chips.csv", params)
    testDataSet("geyser.csv", params)
}

private suspend fun testDataSet(filename: String, params: List<KernelParams>) {
    val dt = readDataSet(filename)
    val trainLog = mutableListOf<TrainParams>()

    for ((name, kernel, c) in params) {
        val classificator = SVMClassificator(kernel, c)

        val analyzer = ClassificatorAnalyzer(classificator)
        val separator = KFoldDataSetSeparator(dt)

        val score = fscore(analyzer.getConfMatrix(dt.countClasses, separator))
        trainLog.add(TrainParams(name, c, score, kernel, classificator))

        println("$name: $score")
    }

    printBestForKernel("Linear", dt, trainLog)
    printBestForKernel("Rbf", dt, trainLog)
}