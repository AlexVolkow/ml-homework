package ru.ctdev.ifmo.ml.hw1.train

interface TrainResultProvider {
    fun save(result: TrainResult)

    fun read(): List<TrainResult>
}