package ru.ctdev.ifmo.ml.hw1.train

import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter

class FileTrainResultProvider(private val fileName: String) : TrainResultProvider, AutoCloseable {
    private val fout = PrintWriter(FileWriter(fileName, true))

    override fun close() {
        fout.close()
    }

    override fun save(result: TrainResult) {
        fout.println(result.serialize())
        fout.flush()
    }

    override fun read(): List<TrainResult> {
        val inp = FileReader(fileName)
        return inp.readLines().map { it.asTrainResult() }
    }

    private fun TrainResult.serialize(): String {
        return "$kernel, $distance, $window, $h, $fscore"
    }

    private fun String.asTrainResult(): TrainResult {
        val parts = split(",")
        return TrainResult(
            kernel = parts[0].trim(),
            distance = parts[1].trim(),
            window = parts[2].trim(),
            h = parts[3].toDouble(),
            fscore = parts[4].toDouble()
        )
    }
}