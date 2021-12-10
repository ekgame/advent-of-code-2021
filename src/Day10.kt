fun main() {
    fun part1(input: List<String>): Int {
        return input.mapNotNull { Day10.SyntaxParser(it).parse() }.sum()
    }

    fun part2(input: List<String>): Long {
        val validLines = input.mapNotNull {
            Day10.SyntaxParser(it).takeIf { parser -> parser.parse() == null }
        }
        val completionScores = validLines.map { it.getCompletionScore() }.sorted()
        return completionScores[completionScores.size/2]
    }

    val testInput = readInput("day_10.example")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("day_10")
    println(part1(input))
    println(part2(input))
}

object Day10 {
    data class BracketDefinition(val opening: Char, val closing: Char, val errorScore: Int, val completionScore: Int) {
        fun isOpening(char: Char) = char == opening
        fun isClosing(char: Char) = char == closing
    }

    object Brackets {
        val brackets = listOf(
            BracketDefinition('(', ')', 3, 1),
            BracketDefinition('[', ']', 57, 2),
            BracketDefinition('{', '}', 1197, 3),
            BracketDefinition('<', '>', 25137, 4),
        )

        fun getDefinition(char: Char) = brackets.first { it.opening == char || it.closing == char }
    }

    class SyntaxParser(val line: String) {

        val runningBrackets = ArrayDeque<Char>()

        fun parse(): Int? {
            val queue = ArrayDeque(line.toCharArray().toList())
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                val definition = Brackets.getDefinition(current)
                when {
                    definition.isOpening(current) -> runningBrackets.addLast(current)
                    definition.isClosing(current) -> {
                        val last = runningBrackets.removeLast()
                        val lastDefinition = Brackets.getDefinition(last)
                        if (definition != lastDefinition) {
                            return definition.errorScore
                        }
                    }
                }
            }

            return null
        }

        fun getCompletionScore(): Long {
            return runningBrackets.reversed()
                .fold(0L) { acc, char ->
                    val definition = Brackets.getDefinition(char)
                    acc*5 + definition.completionScore
                }
        }
    }
}