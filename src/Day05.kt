import Day5.calculateGridSize
import kotlin.math.absoluteValue

fun main() {
    fun parseLines(input: List<String>): List<Day5.Line> {
        val regex = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()
        return input.map {
            val (x1, y1, x2, y2) = (regex.matchEntire(it) ?: error("Failed to match '$it'")).destructured
            Day5.Line(Day5.Point(x1.toInt(), y1.toInt()), Day5.Point(x2.toInt(), y2.toInt()))
        }
    }

    fun part1(input: List<String>): Int {
        val lines = parseLines(input)
        val size = lines.calculateGridSize()
        val grid = Day5.Grid(size.x, size.y)
        lines.forEach {
            grid.processLine(it, false)
        }
        return grid.countIntersections()
    }

    fun part2(input: List<String>): Int {
        val lines = parseLines(input)
        val size = lines.calculateGridSize()
        val grid = Day5.Grid(size.x, size.y)
        lines.forEach(grid::processLine)
        return grid.countIntersections()
    }

    val testInput = readInput("day_5.example")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("day_5")
    println(part1(input))
    println(part2(input))
}

object Day5 {
    data class Point(val x: Int, val y: Int) {
        val component1: Int get() = x
        val component2: Int get() = y
    }

    data class Line(val p1: Point, val p2: Point) {
        val isVertical = p1.x == p2.x
        val isHorizontal = p1.y == p2.y
        val isValidDiagonal by lazy {
            val dx = (p1.x - p2.x).absoluteValue
            val dy = (p1.y - p2.y).absoluteValue
            dx == dy
        }

        init {
            require(isVertical || isHorizontal || isValidDiagonal)
        }
    }

    fun List<Line>.calculateGridSize(): Point {
        val maxX = this.flatMap { listOf(it.p1.x, it.p2.x) }.maxOf { it } + 1
        val maxY = this.flatMap { listOf(it.p1.y, it.p2.y) }.maxOf { it } + 1
        return Point(maxX, maxY)
    }

    class Grid(width: Int, height: Int) {
        val grid = Array(height) {
            IntArray(width) { 0 }
        }

        fun increment(x: Int, y: Int) {
            grid[y][x]++
        }

        fun processLine(line: Line, handleDiagonal: Boolean = true) {
            val (x1, y1) = line.p1
            val (x2, y2) = line.p2
            when {
                line.isVertical -> (y1 toward y2).forEach { y -> increment(x1, y) }
                line.isHorizontal -> (x1 toward x2).forEach { x -> increment(x, y1) }
                line.isValidDiagonal && handleDiagonal -> {
                    val rangeX = x1 toward x2
                    val rangeY = y1 toward y2
                    rangeX.zip(rangeY).forEach { (x, y) -> increment(x, y) }
                }
            }
        }

        infix fun Int.toward(to: Int): IntProgression {
            val step = if (this > to) -1 else 1
            return IntProgression.fromClosedRange(this, to, step)
        }

        fun countIntersections(): Int = grid.sumOf { line ->
            line.count { it > 1 }
        }
    }
}
