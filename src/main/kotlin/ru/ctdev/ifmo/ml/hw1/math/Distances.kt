package ru.ctdev.ifmo.ml.hw1.math

import kotlin.math.abs
import kotlin.math.sqrt

typealias Distance = (Vector, Vector) -> Double

fun euclidean(x: Vector, y: Vector): Double = sqrt(x.zip(y) { a, b -> (a - b) pow 2 }.sum())

fun chebyshev(x: Vector, y: Vector): Double = x.zip(y) { a, b -> abs(a - b) }.max()!!

fun manhattan(x: Vector, y: Vector): Double = x.zip(y) { a, b -> abs(a - b) }.sum()

fun distanceByName(name: String): (Vector, Vector) -> Double =
    when (name) {
        "euclidean" -> { x, y -> euclidean(x, y) }
        "chebyshev" -> { x, y -> chebyshev(x, y) }
        "manhattan" -> { x, y -> manhattan(x, y) }
        else -> throw IllegalArgumentException("Unknown distance fun $name")
    }
