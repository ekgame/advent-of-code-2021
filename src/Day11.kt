fun main() {
    fun readInput(input: List<String>): Array<IntArray> {
        return input.map { line ->
            line.map { it.digitToInt() }.toIntArray()
        }.toTypedArray()
    }

    fun part1(input: List<String>): Int {
        val initialState = readInput(input)
        val simulator = Day11.OctopusFlashSimulator(initialState)
        simulator.advance(100)
        return simulator.flashes
    }

    fun part2(input: List<String>): Int {
        val initialState = readInput(input)
        val simulator = Day11.OctopusFlashSimulator(initialState)
        while (true) {
            simulator.advance()
            if (simulator.hasAllFlashed) {
                return simulator.steps
            }
        }
    }

    val testInput = readInput("day_11.example")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("day_11")
    println(part1(input))
    println(part2(input))
}

object Day11 {
    class OctopusFlashSimulator(initial: Array<IntArray>) {
        val state = initial.mapIndexed { y, line ->
            line.mapIndexed { x, energy -> Octopus(x, y, energy) }
        }

        val octopuses: List<Octopus> get() = state.flatten()
        val hasAllFlashed: Boolean get() = octopuses.all { it.flashed }

        var flashes = 0
        var steps = 0

        fun advance(steps: Int) {
            repeat(steps) {
                advance()
            }
        }

        fun advance() {
            octopuses.forEach {
                it.flashed = false
                it.energy++
            }

            while (true) {
                val unflashed = getUnflashed()
                if (unflashed.isEmpty()) {
                    break
                }
                unflashed.forEach(::flash)
            }

            getFlashed().forEach {
                it.energy = 0
            }

            steps ++
        }

        fun flash(octopus: Octopus) {
            val adjacent = getAdjacent(octopus.x, octopus.y)
            adjacent.forEach { it.energy++ }
            octopus.flashed = true
            flashes++
        }

        operator fun get(x: Int, y: Int) = state.getOrNull(y)?.getOrNull(x)

        fun getAdjacent(x: Int, y: Int) = listOfNotNull(
            this[x + 1, y + 1],
            this[x + 1, y - 1],
            this[x - 1, y + 1],
            this[x - 1, y - 1],
            this[x + 1, y],
            this[x - 1, y],
            this[x, y + 1],
            this[x, y - 1],
        )

        fun getUnflashed() = octopuses.filter { it.energy > 9 && !it.flashed }

        fun getFlashed() = octopuses.filter { it.flashed }

        data class Octopus(val x: Int, val y: Int, var energy: Int, var flashed: Boolean = false)
    }
}