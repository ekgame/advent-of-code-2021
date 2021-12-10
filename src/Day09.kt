fun main() {
    fun parseGrid(lines: List<String>): Day9.IntGrid {
        val values = lines.map { line ->
            line.map { it.digitToInt() }.toIntArray()
        }.toTypedArray()
        return Day9.IntGrid(values)
    }

    fun part1(input: List<String>): Int {
        val grid = parseGrid(input)
        return grid.findLowPoints().sumOf { (x, y) ->  grid[x, y]!! + 1 }
    }

    fun part2(input: List<String>): Int {
        val grid = parseGrid(input)
        val lowPoints = grid.findLowPoints()
        return lowPoints.map { grid.getBasin(it).size }
            .sortedDescending()
            .take(3)
            .fold(1) { acc, current -> acc*current }
    }

    val testInput = readInput("day_9.example")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("day_9")
    println(part1(input))
    println(part2(input))
}

object Day9 {
    data class Point(val x: Int, val y: Int) {
        val component1: Int get() = x
        val component2: Int get() = y
    }

    class IntGrid(private val grid: Array<IntArray>) {
        val width = grid[0].size
        val height = grid.size

        operator fun get(x: Int, y: Int): Int? {
            if (y < 0 || y >= height || x < 0 || x >= width) {
                return null
            }
            return grid[y][x]
        }

        fun getValidCoordinate(x: Int, y: Int): Point? {
            if (y < 0 || y >= height || x < 0 || x >= width) {
                return null
            }
            return Point(x, y)
        }

        fun getAdjacentCoordinates(x: Int, y: Int) = listOfNotNull(
            getValidCoordinate(x + 1, y),
            getValidCoordinate(x - 1, y),
            getValidCoordinate(x, y + 1),
            getValidCoordinate(x, y - 1),
        )

        fun getAdjacentValues(x: Int, y: Int) = getAdjacentCoordinates(x, y).map { this[it.x, it.y]!! }

        fun findLowPoints(): List<Point> {
            return (0 until height).flatMap { y ->
                (0 until width).mapNotNull { x ->
                    val value = this[x, y]!!
                    if (getAdjacentValues(x, y).all { it > value }) {
                        Point(x, y)
                    } else {
                        null
                    }
                }
            }
        }

        fun getBasin(origin: Point): Set<Point> {
            val queued = ArrayDeque(listOf(origin))
            val handled = mutableSetOf<Point>()

            fun queuePoint(point: Point) {
                if (!handled.contains(point)) {
                    queued.add(point)
                }
            }

            while (queued.isNotEmpty()) {
                val point = queued.removeFirst()
                handled.add(point)
                val adjacent = getAdjacentCoordinates(point.x, point.y)
                val valid = adjacent.filter { this[it.x, it.y]!! < 9 }
                valid.forEach(::queuePoint)
            }

            return handled
        }
    }
}