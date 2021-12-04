import java.io.File

private inline fun <T, R> Iterable<T>.zipSlidingWindow(windowLength: Int, transform: (window: List<T>) -> R): List<R> {
    val iterator = iterator()
    val result = mutableListOf<R>()
    val window = mutableListOf<T>()
    while (iterator.hasNext()) {
        for (i in 1..(windowLength - window.size)) {
            if (!iterator.hasNext()) return emptyList()
            window.add(iterator.next())
        }
        result.add(transform(window))
        window.removeFirst()
    }
    return result
}

fun main() {

    fun countIncreasingDeltas(input: List<Int>): Int {
        return input.zipWithNext { a, b -> b - a }.filter { n -> n > 0 }.size
    }

    fun part1(input: List<String>): Int {
        return countIncreasingDeltas(input.map { s -> s.toInt() })
    }

    fun part2(input: List<String>): Int {
        return countIncreasingDeltas(input.map { s -> s.toInt() }.zipSlidingWindow(3) { l -> l.sum() })
    }

    fun part2OneLine()
        = File("src/Day01.txt").useLines { lines ->
            lines.map { s ->
                s.toInt()
            }.windowed(3).map { w ->
                w.sum()
            }.zipWithNext().count {
                it.second > it.first
            }
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 0)

    val input = readInput("Day01")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
    println("Part 2 one line: ${part2OneLine()}")
}