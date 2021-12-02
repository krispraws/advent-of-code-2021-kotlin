
private fun String.toPositionDelta() : Pair<Long, Long> {
    val (direction, distance) = split(" ", limit=2).let { it.component1().trim() to it.component2().trim().toLong() }
    return when (direction) {
        "forward" -> distance to 0
        "up" -> 0L to ((-1) * distance)
        "down" -> 0L to distance
        else -> 0L to 0L
    }
}

private fun List<Pair<Long, Long>>.toFinalPosition(
    startPosition: Pair<Long, Long> = Pair(0L, 0L)
) = fold(startPosition) { currResult, element -> Pair(currResult.first + element.first, currResult.second + element.second) }

private fun String.toPositionDeltaAlt() = split(" ", limit=2).let {
        val direction = it.first().trim()
        val distance = it.last().trim().toLong()
        when (direction) {
        "forward" -> "x" to distance
        "up" -> "y" to ((-1) * distance)
        "down" -> "y" to distance
        else -> {
            println("Unknown direction {0} in line {1}".format(direction, this))
            "?" to distance
        }
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        return input.map { it.toPositionDelta() }.toFinalPosition().let { it.first * it.second }
    }

    fun part1Alt(input: List<String>): Long {
        return input.map {
            it.toPositionDeltaAlt()
        }.groupBy({ it.first }, { it.second }).mapValues {
            it.value.sum()
        }.let { it.getValue("x") * it.getValue("y") }
    }

    fun part2(input: List<String>): Long {
        return input.map { line ->
            line.split(" ", limit = 2).let { it.first().trim() to it.last().trim().toLong() }
        }.fold(Triple(0L,0L,0L)) { // (x, y, aim)
            (currX, currY, currAim), (direction, delta) -> when (direction) {
                "forward" -> Triple(currX + delta, currY + (currAim * delta), currAim)
                "up" -> Triple(currX, currY, currAim + ((-1) * delta))
                "down" -> Triple(currX, currY, currAim + delta)
                else -> {
                    println("Unknown direction {0}. Skipping.".format(direction))
                    Triple(currX, currY, currAim)
                }
            }
        }.let { (x, y, _) -> x * y }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 20L)
    check(part1Alt(testInput) == 20L)
    check(part2(testInput) == 75L)

    val input = readInput("Day02")
    val part1Answer = part1(input)
    check(part1Alt(input) == part1Answer)
    println("Part 1: ${part1Answer}")
    println("Part 2: ${part2(input)}")

}
