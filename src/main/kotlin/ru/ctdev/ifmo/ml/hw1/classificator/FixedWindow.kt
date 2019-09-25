package ru.ctdev.ifmo.ml.hw1.classificator

class FixedWindow(private val width: Double): Window {
    override fun getWidth(distances: List<Double>): Double = width
}