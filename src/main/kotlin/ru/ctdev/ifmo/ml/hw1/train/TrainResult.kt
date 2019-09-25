package ru.ctdev.ifmo.ml.hw1.train

data class TrainResult(
    val kernel: String,
    val distance: String,
    val window: String,
    val h: Double,
    val fscore: Double
)