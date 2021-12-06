fun main() {
    fun part1(input: List<String>): Long {
        val initialState = input.flatMap { it.split(',') }.map { it.toInt() }
        val simulator = LanternFishSimulator(initialState)
        simulator.advance(80)
        return simulator.totalFish
    }

    fun part2(input: List<String>): Long {
        val initialState = input.flatMap { it.split(',') }.map { it.toInt() }
        val simulator = LanternFishSimulator(initialState)
        simulator.advance(256)
        return simulator.totalFish
    }

    val testInput = readInput("day_6.example")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539L)

    val input = readInput("day_6")
    println(part1(input))
    println(part2(input))
}


class LanternFishSimulator(initialState: List<Int>) {

    val fish = mapOf(*((0..8).map { it to LanternFishCounter() }.toTypedArray()))
    val formatted: String
        get() = fish.entries.joinToString(" | ") { "${it.key}: ${it.value.count}" }
    val totalFish: Long
        get() = fish.values.sumOf { it.count }

    init {
        initialState.forEach {
            fish[it]!!.count++
        }
    }

    fun advance(count: Int) {
        repeat(count) {
            advance()
        }
    }

    fun advance() {
        val newFish = fish[0]!!.count
        (1..8).forEach {
            fish[it - 1]!!.count = fish[it]!!.count
        }
        fish[6]!!.count += newFish
        fish[8]!!.count = newFish
    }

    inner class LanternFishCounter(var count: Long = 0)
}