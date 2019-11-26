package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Matrix
import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.sign
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class SVMClassificator(
    val kernel: ParameterizedKernel,
    val C: Double
) : Classificator<Vector, Int> {

    val TOL = 0.00001

    private lateinit var w: MutableList<Double>
    private var b = 0.0
    private lateinit var X: Matrix
    private lateinit var Y: Vector

    override suspend fun fit(dataSet: Dataset) {
        X = Matrix(dataSet.toMutableList())
        Y = dataSet.classes.transpose()[0]
        val n = X.size

        val K = MutableList(n) { MutableList(n) { 0.0 } }
        for (i in 0 until n) {
            for (j in 0 until n) {
                K[i][j] = kernel.evaluate(X[i], X[j])
            }
        }

        w = MutableList(n) { 0.0 }
        var count = 0
        var allIteration = 0
        while (count < 450 && allIteration < 10_000) {
            var changed = false
            for (i in 0 until n) {
                allIteration++
                val w_i = w[i]

                var u_i = b
                for (t in 0 until n) {
                    u_i += w[t] * K[i][t] * Y[t]
                }
                val e_i = u_i - Y[i]
                val r_i = e_i * Y[i]
                if ((r_i < -TOL && w[i] < C) || (r_i > TOL && w[i] > 0)) {
                    val j = getRandInt(n, i)
                    val w_j = w[j]

                    var u_j = b
                    for (t in 0 until n) {
                        u_j += w[t] * K[j][t] * Y[t]
                    }
                    val e_j = u_j - Y[j]

                    val L: Double
                    val H: Double
                    if (Y[j] != Y[i]) {
                        L = max(0.0, w_j - w_i);
                        H = min(C, C + w_j - w_i);
                    } else {
                        L = max(0.0, w_i + w_j - C);
                        H = min(C, w_j + w_i);
                    }
                    if (L == H) continue;


                    val k = 2.0 * K[i][j] - K[j][j] - K[i][i]
                    if (k >= 0) continue

                    w[j] = w_j + (Y[j] * (e_j - e_i)) / k
                    w[j] = max(w[j], L)
                    w[j] = min(w[j], H)

                    if (abs(w[j] - w_j) < 0.001) {
                        continue
                    }

                    w[i] = w_i + Y[j] * Y[i] * (w_j - w[j])

                    val b1 =
                        b - e_i - Y[i] * (w[i] - w_i) * K[i][i] - Y[j] * (w[j] - w_j) * K[i][j]
                    val b2 =
                        b - e_j - Y[i] * (w[i] - w_i) * K[i][j] - Y[j] * (w[j] - w_j) * K[j][j]

                    b = if (0 < w[i] && w[i] < C) b1 else if (0 < w[j] && w[j] < C) b2 else (b1 + b2) * 0.5

                    changed = true
                }
                if (!changed) count++
            }
        }
    }

    override suspend fun predict(x: Vector): Int {
        var res = b
        for (i in 0 until w.size) {
            if (w[i] != 0.0) {
                res += w[i] * Y[i] * kernel.evaluate(x, X[i])
            }
        }
        return if (sign(res) == 0 &&  rnd.nextInt(2) == 0) 0 else if (sign(res) == -1) 0 else 1
    }

    val rnd = Random(16)

    private fun getRandInt(n: Int, except: Int): Int {
        var r =rnd.nextInt(n)
        while (r == except)
            r =rnd.nextInt(n)
        return r
    }
}