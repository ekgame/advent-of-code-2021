fun main() {
    fun parsePoints(input: List<String>): List<Day13.Point> {
        return input.map {
            val (x, y) = it.split(",").map { it.toInt() }
            Day13.Point(x, y)
        }
    }

    fun parseFolds(input: List<String>): List<Day13.FoldInstruction> {
        return input.map {
            val (method, position) = it.split("=")
            val direction = when (method) {
                "fold along y" -> Day13.FoldDirection.HORIZONTAL
                "fold along x" -> Day13.FoldDirection.VERTICAL
                else -> error("Invalid fold instruction: $method")
            }
            Day13.FoldInstruction(direction, position.toInt())
        }
    }

    fun parseInput(input: List<String>): Day13.Input {
        val blankIndex = input.indexOfFirst { it.isBlank() }
        val pointInputs = parsePoints(input.subList(0, blankIndex))
        val foldInputs = parseFolds(input.subList(blankIndex + 1, input.size))
        return Day13.Input(pointInputs, foldInputs)
    }

    fun part1(input: List<String>): Int {
        val inputs = parseInput(input)
        val grid = Day13.Grid(inputs.points)
        grid.doFold(inputs.foldInstructions.first())
        return grid.countPoints()
    }

    fun part2(input: List<String>): Day13.Grid {
        val inputs = parseInput(input)
        val grid = Day13.Grid(inputs.points)
        inputs.foldInstructions.forEach { instruction ->
            grid.doFold(instruction)
        }
        return grid
    }

    val testInput = readInput("day_13.example")
    check(part1(testInput) == 17)

    val input = readInput("day_13")
    println(part1(input))
    part2(input).dump()
}

object Day13 {
    data class Input(val points: List<Point>, val foldInstructions: List<FoldInstruction>)

    data class Point(val x: Int, val y: Int)

    class Grid(points: List<Point>) {
        val grid: Array<BooleanArray>
        val sizeX: Int
        val sizeY: Int

        init {
            sizeX = points.maxOf { it.x } + 1
            sizeY = points.maxOf { it.y } + 1

            grid = Array(sizeY) {
                BooleanArray(sizeX)
            }

            points.forEach {
                this[it.x, it.y] = true
            }
        }

        operator fun get(x: Int, y: Int) = grid[y][x]

        operator fun set(x: Int, y: Int, value: Boolean) {
            grid[y][x] = value
        }

        fun doFold(instruction: FoldInstruction) {
            when (instruction.direction) {
                FoldDirection.HORIZONTAL -> {
                    (0 until sizeX).forEach { x ->
                        this[x, instruction.position] = false
                        ((instruction.position + 1) until sizeY).forEach { y ->
                            val targetY = instruction.position - (y - instruction.position)
                            if (this[x, y]) {
                                this[x, targetY] = true
                                this[x, y] = false
                            }
                        }
                    }
                }
                FoldDirection.VERTICAL -> {
                    (0 until sizeY).forEach { y ->
                        this[instruction.position, y] = false
                        ((instruction.position + 1) until sizeX).forEach { x ->
                            val targetX = instruction.position - (x - instruction.position)
                            if (this[x, y]) {
                                this[targetX, y] = true
                                this[x, y] = false
                            }
                        }
                    }
                }
            }
        }

        fun countPoints() = grid.sumOf { line ->
            line.count { it }
        }

        fun dump() {
            val sizeX = grid.maxOf {
                it.indexOfLast { it }
            }
            val sizeY = grid.indexOfLast { it.any { it } }
            (0..sizeY).forEach { y ->
                println(grid[y].copyOf(sizeX).map { if (it) '#' else '.' }.joinToString(""))
            }
        }
    }

    enum class FoldDirection {
        VERTICAL, HORIZONTAL
    }

    data class FoldInstruction(val direction: FoldDirection, val position: Int)
}