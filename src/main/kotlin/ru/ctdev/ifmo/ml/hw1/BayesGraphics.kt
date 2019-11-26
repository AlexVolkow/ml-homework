package ru.ctdev.ifmo.ml.hw1

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.knowm.xchart.*
import org.knowm.xchart.style.Styler
import ru.ctdev.ifmo.ml.hw1.classificator.BayesClassificator
import ru.ctdev.ifmo.ml.hw1.classificator.ClassificatorAnalyzer
import ru.ctdev.ifmo.ml.hw1.classificator.SplitDataSetSeparator
import ru.ctdev.ifmo.ml.hw1.math.Matrix

val precision =
    { matrix: Matrix ->
        0.5 * ((matrix[0][0] / (matrix[0][0] + matrix[0][1]))
                + (matrix[1][1] / (matrix[1][0] + matrix[1][1])))
    }

@ObsoleteCoroutinesApi
fun main()= runBlocking<Unit>(context = newFixedThreadPoolContext(7, "bayes")) {
    val fine = 100
    val smooth = 0.1

    val dt = readTexts()

    val x = mutableListOf<Double>()
    val y = mutableListOf<Double>()
    val classifier = BayesClassificator(smooth, listOf(fine.toDouble(), 0.0))
    val analyzer = ClassificatorAnalyzer(classifier)

    for (f in 0..fine step 3) {
        val separator = SplitDataSetSeparator(dt, 5)
        val confMatrix = analyzer.getConfMatrix(2, separator)
        val presicion = precision(confMatrix)
        if (confMatrix[0][1] != 0.0) {
            println("Strange $f $presicion")
        }
        println("Fine: $f, Presicion: $presicion")

        x.add(f.toDouble())
        y.add(presicion)
    }

    val chart = QuickChart.getChart("Spam filter", "Fine", "Precision", "Precision(fine)", x, y)
    BitmapEncoder.saveBitmap(chart, "./spam_filter", BitmapEncoder.BitmapFormat.PNG);
    SwingWrapper(chart).displayChart();
}