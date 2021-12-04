fun main() {
    fun part1(input: List<String>): Int {
        val depths = input.map { it.toInt() }
        return depths.zipWithNext().count { it.first < it.second }
    }

    fun part2(input: List<String>): Int {
        val depths = input.map { it.toInt() }
        val normalizedDepths = depths.windowed(3).map { it.sum() }
        return normalizedDepths.zipWithNext().count { it.first < it.second }
    }

    val input = readInput("day_1")
    println(part1(input))
    println(part2(input))
}
