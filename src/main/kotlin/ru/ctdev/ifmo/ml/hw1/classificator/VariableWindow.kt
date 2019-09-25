package ru.ctdev.ifmo.ml.hw1.classificator

class VariableWindow(private val neighbor: Int): Window {
    override fun getWidth(distances: List<Double>): Double = distances.sorted()[neighbor + 1]
}