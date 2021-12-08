fun main() {
    fun parseInput(lines: List<String>): List<DigitPatternLine> {
        return lines.map {
            val parts = it.split('|')
            val patterns = parts[0].trim().split(' ')
            val digits = parts[1].trim().split(' ')
            DigitPatternLine(patterns, digits)
        }
    }

    fun part1(input: List<String>): Int {
        val patterns = parseInput(input)
        return patterns.sumOf { line ->
            line.digits.count { it.length == 7 || it.length == 4  || it.length == 3  || it.length == 2 }
        }
    }

    fun part2(input: List<String>): Int {
        val patterns = parseInput(input)
        return patterns.sumOf { DigitResolver(it).getFourDigitNumber() }
    }

    val testInput = readInput("day_8.example")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("day_8")
    println(part1(input))
    println(part2(input))
}

data class DigitPatternLine(val patterns: List<String>, val digits: List<String>)

class DigitResolver(val pattern: DigitPatternLine) {
    val oneSegments = pattern.patterns.first { it.length == 2 }.toSet()
    val fourSegments = pattern.patterns.first { it.length == 4 }.toSet()

    fun getDigit(input: String): Int {
        val chars = input.toSet()
        return when (input.length) {
            7 -> 8
            3 -> 7
            4 -> 4
            2 -> 1
            5 -> {
                when {
                    chars.containsAll(oneSegments) -> 3
                    chars.intersect(fourSegments).size == 2 -> 2
                    chars.intersect(fourSegments).size == 3 -> 5
                    else -> error("uh oh")
                }
            }
            6 -> {
                when {
                    chars.containsAll(fourSegments) -> 9
                    chars.containsAll(oneSegments) -> 0
                    else -> 6
                }
            }
            else -> error("uh oh")
        }
    }

    fun getFourDigitNumber() =
        getDigit(pattern.digits[0])*1000 +
        getDigit(pattern.digits[1])*100 +
        getDigit(pattern.digits[2])*10 +
        getDigit(pattern.digits[3])
}