fun main() {
    fun parseInput(input: List<String>): Day14.Inputs {
        val initialState = input.first()
        val rules = input.drop(2).map {
            val (_, identifier, insert) = """(\w\w) -> (\w)""".toRegex().matchEntire(it)!!.groupValues
            Day14.Rule(identifier, insert)
        }
        return Day14.Inputs(
            initialState = initialState,
            rules = rules,
        )
    }

    fun part1(input: List<String>): Long {
        val inputs = parseInput(input)
        val simulator = Day14.PolimerizationSimulator(inputs.initialState, inputs.rules)
        repeat(10) {
            simulator.step()
        }
        val counts = simulator.getCounts().map {
            val number = it.second
            if (number.rem(2) == 0L) {
                it.second/2
            } else {
                it.second/2 + 1
            }
        }
        return counts.last() - counts.first()
    }

    fun part2(input: List<String>): Long {
        val inputs = parseInput(input)
        val simulator = Day14.PolimerizationSimulator(inputs.initialState, inputs.rules)
        repeat(40) {
            simulator.step()
        }
        val counts = simulator.getCounts().map {
            val number = it.second
            if (number.rem(2) == 0L) {
                it.second/2
            } else {
                it.second/2 + 1
            }
        }
        return counts.last() - counts.first()
    }

    val testInput = readInput("day_14.example")
    check(part1(testInput) == 1588L)
    check(part2(testInput) == 2188189693529L)

    val input = readInput("day_14")
    println(part1(input))
    println(part2(input))
}

object Day14 {
    data class Inputs(
        val initialState: String,
        val rules: List<Rule>,
    )

    data class Rule(val identifier: String, val insert: String)

    class PolimerizationSimulator(
        initialState: String,
        val rules: List<Rule>,
    ) {
        var counts = PairCounts()

        init {
            initialState.zipWithNext().forEach {
                counts.addPairAmount("${it.first}${it.second}", 1)
            }
        }

        fun step() {
            val newCounts = PairCounts()
            counts.pairCounts.values
                .filter { it.amount > 0 }
                .forEach { count ->
                    val rule = rules.firstOrNull { it.identifier == count.identifier }
                    if (rule !== null) {
                        val (first, second) = count
                        newCounts.addPairAmount("$first${rule.insert}", count.amount)
                        newCounts.addPairAmount("${rule.insert}$second", count.amount)
                    } else {
                        newCounts.addPairAmount(count.identifier, count.amount)
                    }
                }
            counts = newCounts
        }

        fun getCounts(): List<Pair<Char, Long>> {
            return counts.pairCounts.values
                .flatMap { count ->
                    val (first, second) = count
                    listOf(
                        first to count.amount,
                        second to count.amount,
                    )
                }
                .groupBy { it.first }
                .map {
                    it.key to it.value.sumOf { it.second }
                }
                .sortedBy { it.second }
        }
    }

    class PairCounts {
        var pairCounts = mutableMapOf<String, PairCount>()

        fun addPairAmount(identifier: String, amount: Long) {
            val count = pairCounts.getOrPut(identifier) { PairCount(identifier, 0) }
            count.amount += amount
        }

        class PairCount(val identifier: String, var amount: Long = 0) {
            operator fun component1() = identifier[0]
            operator fun component2() = identifier[1]

            override fun hashCode(): Int {
                return identifier.hashCode()
            }

            override fun equals(other: Any?): Boolean {
                return other is PairCount && other.identifier == identifier
            }
        }
    }
}