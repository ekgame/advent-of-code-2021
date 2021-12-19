fun main() {
    fun parseCaveSystem(input: List<String>): Day12.CaveSystem {
        fun createNode(name: String) = when {
            name == "start" -> Day12.Start
            name == "end" -> Day12.End
            name.all { it.isLowerCase() } -> Day12.SmallCave(name)
            name.all { it.isUpperCase() } -> Day12.LargeCave(name)
            else -> error("Failed to create node for name: $name")
        }
        val namedNodes = mutableMapOf<String, Day12.Node>()
        fun getNode(name: String) = namedNodes.getOrPut(name) { createNode(name) }

        val relations = mutableMapOf<Day12.Node, MutableSet<Day12.Node>>()
        fun addRelation(from: Day12.Node, to: Day12.Node) {
            relations.computeIfPresent(from) { _, value -> value.apply { add(to) } }
            relations.computeIfAbsent(from) { mutableSetOf(to) }
        }

        input.forEach {
            val (from, to) = it.split("-")
            val nodeFrom = getNode(from)
            val nodeTo = getNode(to)
            addRelation(nodeFrom, nodeTo)
            addRelation(nodeTo, nodeFrom)
        }

        return Day12.CaveSystem(relations)
    }

    fun part1(input: List<String>): Int {
        val caveSystem = parseCaveSystem(input)
        val simulator = Day12.NavigationSimulator(caveSystem) { submarine, possibleRoutes ->
            possibleRoutes.filter { availablePath ->
                when (availablePath) {
                    is Day12.SmallCave -> !submarine.path.contains(availablePath)
                    is Day12.Start -> false
                    else -> true
                }
            }.toSet()
        }
        simulator.simulate()
        return simulator.submarines.count()
    }

    fun part2(input: List<String>): Int {
        val caveSystem = parseCaveSystem(input)
        val simulator = Day12.NavigationSimulator(caveSystem) { submarine, possibleRoutes ->
            possibleRoutes.filter { availablePath ->
                when (availablePath) {
                    is Day12.SmallCave ->
                        !submarine.path.contains(availablePath)
                        || submarine.path.filterIsInstance<Day12.SmallCave>().groupBy { it }.entries.all { it.value.count() < 2 }
                    is Day12.Start -> false
                    else -> true
                }
            }.toSet()
        }
        simulator.simulate()
        return simulator.submarines.count()
    }

    val testInput = readInput("day_12.example")
    check(part1(testInput) == 10)
    check(part2(testInput) == 36)

    val testInput2 = readInput("day_12.example2")
    check(part1(testInput2) == 19)
    check(part2(testInput2) == 103)

    val testInput3 = readInput("day_12.example3")
    check(part1(testInput3) == 226)
    check(part2(testInput3) == 3509)

    val input = readInput("day_12")
    println(part1(input))
    println(part2(input))
}

object Day12 {
    class CaveSystem(val relations: Map<Node, Set<Node>>) {
        fun getAvailableRoutes(from: Node): Set<Node> = relations[from]!!
    }

    sealed class Node
    object Start : Node()
    object End : Node()
    data class SmallCave(val name: String) : Node()
    data class LargeCave(val name: String) : Node()

    class Submarine(currentPath: List<Node>) {
        val path = currentPath.toMutableList()
        val current: Node get() = path.last()
        val isFinished get() = current == End
    }

    class NavigationSimulator(
        val caveSystem: CaveSystem,
        val availableRoutePredicate: (Submarine, Set<Node>) -> Set<Node>
    ) {
        var submarines = listOf(Submarine(listOf(Start)))
        val isFinished get() = submarines.all { it.isFinished}

        fun simulate() {
            while (!isFinished) {
                step()
            }
        }

        fun step() {
            submarines = submarines.flatMap { submarine ->
                if (submarine.isFinished) {
                    return@flatMap listOf(submarine)
                }

                val availablePaths = availableRoutePredicate(submarine, caveSystem.getAvailableRoutes(submarine.current))
                availablePaths.map {
                    Submarine(submarine.path + it)
                }
            }
        }
    }
}