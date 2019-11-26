import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.exp
import kotlin.math.ln

fun main() {
    val inp = FastScanner()

    val k = inp.nextInt()
    val lam = Array(k) { inp.nextInt() }
    val smoothing = inp.nextInt()

    val n = inp.nextInt()
    val classes = Array(n) { 0 }
    val texts = ArrayList<Set<String>>()

    val countWordPerClass = Array(k) { mutableSetOf<String>() }
    val countTextPerClass = Array(k) { 0 }
    val textByClass = Array(k) { mutableListOf<Int>() }
    for (i in 0 until n) {
        classes[i] = inp.nextInt() - 1
        textByClass[classes[i]].add(i)
        val l = inp.nextInt()
        val text = MutableList(l) { inp.next()!! }.toSet()
        texts.add(text)
        countTextPerClass[classes[i]]++
        countWordPerClass[classes[i]].addAll(text)
    }

    fun countText(word: String, classId: Int): Int {
        var cnt = 0
        for (i in textByClass[classId]) {
            if (word in texts[i])
                cnt++
        }
        return cnt
    }

    val st = StringBuilder()

    val m = inp.nextInt()
    for (i in 0 until m) {
        val l = inp.nextInt()
        val text = MutableList(l) { inp.next()!! }.toSet()
        val y = Array(k) { 0.0 }

        for (classId in 0 until k) {
            if (countTextPerClass[classId] == 0) continue
            y[classId] = ln(lam[classId].toDouble() * (countTextPerClass[classId].toDouble() / n))
            for (word in text) {
                val p = (countText(word, classId) + smoothing).toDouble() /
                        (countTextPerClass[classId] + smoothing * countWordPerClass[classId].size)
                y[classId] += ln(p)
            }
        }
        val mean = y.filter { it != 0.0 }.average()
        val e = Array(k) {
            if (y[it] == 0.0) 0.0 else exp(y[it] - mean)
        }
        val sumE = e.sum()

        for (eI in e) {
            st.append((eI / sumE).toString() + " ")
        }
        st.append("\n")
    }

    print(st.toString())
}

internal class FastScanner {
    var br: BufferedReader? = null
    var st: StringTokenizer? = null

    operator fun next(): String? {
        while (st == null || !st!!.hasMoreTokens()) {
            try {
                st = StringTokenizer(br!!.readLine())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return st!!.nextToken()
    }

    fun nextInt(): Int {
        return Integer.parseInt(next())
    }

    init {
        try {
            br = BufferedReader(System.`in`.reader())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}