package ru.ctdev.ifmo.ml.hw1.math

import kotlin.math.*

typealias Kernel = (Double) -> Double

fun uniform(x: Double) = xabs(x) * 0.5

fun triangular(x: Double) = xabs(x) * (1 - abs(x))

fun epanechnikov(x: Double) = xabs(x) * (0.75 * (1 - (x pow 2)))

fun quartic(x: Double) = xabs(x) * (15.0 / 16.0) * ((1 - (x pow 2)) pow 2)

fun cosine(x: Double) = xabs(x) * (PI / 4.0) * cos((PI / 2.0) * x)

fun gaussian(x: Double) = (1.0 / sqrt(2.0 * PI)) * exp(-0.5 * (x * x))

fun kernelByName(name: String): Kernel = when (name) {
    "uniform" -> { x -> uniform(x) }
    "triangular" -> { x -> triangular(x) }
    "epanechnikov" -> { x -> epanechnikov(x) }
    "quartic" -> { x -> quartic(x) }
    "cosine" -> { x -> cosine(x) }
    "gaussian" -> { x -> gaussian(x) }
    else -> throw IllegalArgumentException("Unknown function $name")
}
