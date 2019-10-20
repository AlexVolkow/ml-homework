package ru.ctdev.ifmo.ml.hw1.classificator

import ru.ctdev.ifmo.ml.hw1.math.Vector
import ru.ctdev.ifmo.ml.hw1.math.norm
import java.lang.Integer.min

interface ParameterizedKernel {
    fun evaluate(a: Vector, b: Vector): Double
}

class LinearKernel : ParameterizedKernel {

    override fun evaluate(a: Vector, b: Vector): Double {
        var res = 0.0
        for (i in 0 until min(a.size, b.size)) {
            res += a[i] * b[i]
        }
        return res
    }
}

class RadialBaseKernel(
    var gamma: Double = 0.5
) : ParameterizedKernel {

    override fun evaluate(a: Vector, b: Vector): Double {
        return Math.exp(-gamma * Math.pow((a - b).norm(), 2.0))
    }
}