import java.util.*

fun main() {

    fun parseInput(input: List<String>): InputDay4 {
        fun parseDraws(string: String) = string.split(',').map { it.toInt() }
        fun parseBoard(lines: List<String>): BingoBoard {
            require(lines.size == 5)
            val scanner = Scanner(lines.joinToString(" "))
            val values = Array(5) {
                IntArray(5) { scanner.nextInt() }
            }
            return BingoBoard(values)
        }

        val draws = parseDraws(input.first())
        var boardInputs = input.drop(2)
        val boards = mutableListOf<BingoBoard>()
        while (boardInputs.isNotEmpty()) {
            boards.add(parseBoard(boardInputs.take(5)))
            boardInputs = boardInputs.drop(6)
        }
        return InputDay4(draws, boards)
    }


    fun part1(input: List<String>): Int {
        val parsed = parseInput(input)
        parsed.draws.forEach { draw ->
            parsed.boards.forEach { board ->
                if (board.markNumber(draw) && board.isCompleted()) {
                    return board.getUnmarkedSum() * draw
                }
            }
        }
        error("No boards completed")
    }

    fun part2(input: List<String>): Int {
        val parsed = parseInput(input)
        val remainingBoards = parsed.boards.toMutableList()
        parsed.draws.forEach { draw ->
            val iterator = remainingBoards.listIterator()
            iterator.forEach { board ->
                if (board.markNumber(draw) && board.isCompleted()) {
                    iterator.remove()
                }

                if (remainingBoards.isEmpty()) {
                    return board.getUnmarkedSum() * draw
                }
            }
        }
        error("More than one board still remains")
    }

    val testInput = readInput("day_4.example")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("day_4")
    println(part1(input))
    println(part2(input))
}

data class InputDay4(val draws: List<Int>, val boards: List<BingoBoard>)

class BingoBoard(values: Array<IntArray>) {
    private val values = values.map { line -> line.map { Item(it) } }
    private val size = values.size

    fun markNumber(draw: Int): Boolean {
        values.forEach { line ->
            val item = line.find { it.number == draw }
            if (item != null) {
                item.mark()
                return true
            }
        }
        return false
    }

    fun isLineMarked(index: Int) = values[index].all { it.marked }

    fun isColumnMarked(index: Int) = values.all { it[index].marked }

    fun isCompleted() = (0 until size).any { isColumnMarked(it) || isLineMarked(it) }

    fun getUnmarkedSum() = values.map { line ->
        // Sum all unmarked numbers in the line
        line.filter { !it.marked }.sumOf { it.number }
    }.sum()

    inner class Item(val number: Int, var marked: Boolean = false) {
        fun mark() {
            marked = true
        }
    }
}