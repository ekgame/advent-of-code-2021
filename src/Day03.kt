fun main() {
    fun calculateBitBuckets(lines: List<String>): BitBuckets {
        val buckets = BitBuckets(lines.first().length)
        lines.forEach { line ->
            buckets.incrementLines()
            line.forEachIndexed { index, char ->
                if (char == '1') {
                    buckets.increment(index)
                }
            }
        }
        return buckets
    }

    fun part1(input: List<String>): Int {
        val buckets = calculateBitBuckets(input)
        return buckets.calculateGamma() * buckets.calculateEpsilon()
    }

    fun calculateOxygenRating(lines: List<String>): Int {
        val size = lines.first().length
        var remainingLines = lines
        (0 until size).forEach { index ->
            if (remainingLines.size == 1) {
                return remainingLines.first().toInt(2)
            }
            val halfway = remainingLines.size/2f
            val buckets = calculateBitBuckets(remainingLines)
            val keep1 = buckets.buckets[index] >= halfway
            remainingLines = remainingLines.filter {
                if (keep1) it[index] == '1' else it[index] == '0'
            }
        }
        if (remainingLines.size != 1) {
            error("Something went wrong")
        }
        return remainingLines.first().toInt(2)
    }

    fun calculateCarbonRating(lines: List<String>): Int {
        val size = lines.first().length
        var remainingLines = lines
        (0 until size).forEach { index ->
            if (remainingLines.size == 1) {
                return remainingLines.first().toInt(2)
            }
            val halfway = remainingLines.size/2f
            val buckets = calculateBitBuckets(remainingLines)
            val keep0 = buckets.buckets[index] >= halfway
            remainingLines = remainingLines.filter {
                if (keep0) it[index] == '0' else it[index] == '1'
            }
        }
        if (remainingLines.size != 1) {
            error("Something went wrong")
        }
        return remainingLines.first().toInt(2)
    }

    fun part2(input: List<String>): Int {
        val oxygenRating = calculateOxygenRating(input)
        val carbonRating = calculateCarbonRating(input)
        return oxygenRating * carbonRating
    }

    val testInput = readInput("day_3.example")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("day_3")
    println(part1(input))
    println(part2(input))
}

class BitBuckets(val size: Int) {
    val buckets = IntArray(size)
    var lines = 0

    fun increment(index: Int) {
        buckets[index]++
    }

    fun incrementLines() {
        lines++
    }

    fun calculateGamma(): Int {
        var result = 0
        val halfway = lines/2
        (0 until size).forEach { index ->
            result = result shl 1
            if (buckets[index] > halfway) {
                result = result or 1
            }
        }
        return result
    }

    fun createBitMask(size: Int): Int {
        var mask = 0
        (0 until size).forEach { _ ->
            mask = mask shl 1 or 1
        }
        return mask
    }

    fun calculateEpsilon(): Int {
        return calculateGamma().inv() and createBitMask(size)
    }
}

