import kotlin.math.absoluteValue

fun main() {
    fun calculateFuelAtLevel(levels: List<Int>, level: Int): Int {
        return levels.sumOf { (it - level).absoluteValue }
    }

    fun calculateFuelAtLevel2(levels: List<Int>, level: Int): Int {
        return levels.sumOf {
            val n = (it - level).absoluteValue
            (n*(n + 1))/2
        }
    }

    fun getBestLevel(levels: List<Int>, hiuristic: (levels: List<Int>, level: Int) -> Int): Int {
        val min = levels.minOf { it }
        val max = levels.maxOf { it }
        return (min..max).minByOrNull { hiuristic(levels, it) }!!
    }

    fun part1(input: List<String>): Int {
        val initialState = input.flatMap { it.split(',') }.map { it.toInt() }
        val bestLevel = getBestLevel(initialState, ::calculateFuelAtLevel)
        return calculateFuelAtLevel(initialState, bestLevel)
    }

    fun part2(input: List<String>): Int {
        val initialState = input.flatMap { it.split(',') }.map { it.toInt() }
        val bestLevel = getBestLevel(initialState, ::calculateFuelAtLevel2)
        return calculateFuelAtLevel2(initialState, bestLevel)
    }

    val testInput = readInput("day_7.example")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("day_7")
    println(part1(input))
    println(part2(input))
}
