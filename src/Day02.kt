import java.text.ParseException

fun main() {
    fun part1(input: List<Action>): Int {
        var depth = 0
        var forward = 0
        for (action in input) {
            when (action) {
                is ActionDown -> depth += action.amount
                is ActionForward -> forward += action.amount
                is ActionUp -> depth -= action.amount
            }
        }
        return depth*forward
    }

    fun part2(input: List<Action>): Int {
        var depth = 0
        var forward = 0
        var aim = 0
        for (action in input) {
            when (action) {
                is ActionDown -> aim += action.amount
                is ActionUp -> aim -= action.amount
                is ActionForward -> {
                    forward += action.amount
                    depth += aim * action.amount
                }
            }
        }
        return depth*forward
    }

    fun parseAction(input: String): Action {
        return when {
            input.startsWith("forward") -> ActionForward(input.substring(8).toInt())
            input.startsWith("down") -> ActionDown(input.substring(5).toInt())
            input.startsWith("up") -> ActionUp(input.substring(3).toInt())
            else -> throw ParseException("Could not parse: $input", 0)
        }
    }

    val input = readInput("day_2").map(::parseAction)
    println(part1(input))
    println(part2(input))
}

sealed class Action(val amount: Int)
class ActionForward(amount: Int) : Action(amount)
class ActionUp(amount: Int) : Action(amount)
class ActionDown(amount: Int) : Action(amount)