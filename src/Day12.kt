import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

typealias Path = List<String>
class Graph(input: List<String>) {
    val START = "start"
    val END = "end"

    private val adjacencyList = input.flatMap {
        val (start, end) = it.split("-")
        listOf(start to end, end to start)
    }.groupBy({ it.first }, { it.second })

    fun findPaths(maxSmallCaveVisits: Int) : Set<Path> {
        val validPaths = mutableSetOf<Path>()
        var currPaths = listOf<Path>(listOf(START))
        while (currPaths.isNotEmpty()) {
            val newPaths = currPaths.flatMap { traverse(it) }
            validPaths.addAll(newPaths.filter { it.last() == "end" })
            currPaths = newPaths.filterNot {
                it.last() == "end" || it.last() == "start" ||
                it.count { p -> p == p.lowercase() } > it.filter { p -> p == p.lowercase() }.toSet().size + maxSmallCaveVisits - 1
            }
        }
        //println("Paths: $validPaths")
        return validPaths
    }

    fun traverse(pathTillHere: Path) : List<Path> {
        val nextCaves = adjacencyList.getOrDefault(pathTillHere.last(), emptyList())
        return nextCaves.map { pathTillHere + it }
    }
}

class Day12Impl {

    fun part1(input: List<String> ): Int {
        val graph = Graph(input)
        return graph.findPaths(1).size
    }

    fun part2(input: List<String>): Int {
        val graph = Graph(input)
        return graph.findPaths(2).size
    }
}

class Day12Test {

    private val impl = Day12Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test part1 example1`() {
        val input = listOf("start-A", "start-b", "A-c", "A-b", "b-d", "A-end", "b-end")
        Assertions.assertEquals(10, impl.part1(input))
    }

    @Test
    fun `test part1 example2`() {
        val input = listOf("dc-end", "HN-start", "start-kj", "dc-start", "dc-HN", "LN-dc", "HN-end", "kj-sa", "kj-HN", "kj-dc")
        Assertions.assertEquals(19, impl.part1(input))
    }

    @Test
    fun `test part1 example3`() {
        val input = listOf("fs-end", "he-DX", "fs-he",
            "start-DX", "pj-DX", "end-zg", "zg-sl", "zg-pj",
            "pj-he", "RW-he", "fs-DX", "pj-RW", "zg-RW",
            "start-pj", "he-WI", "zg-he", "pj-fs", "start-RW")
        Assertions.assertEquals(226, impl.part1(input))
    }

    @Test
    fun `test part2 example1`() {
        val input = listOf("start-A", "start-b", "A-c", "A-b", "b-d", "A-end", "b-end")
        Assertions.assertEquals(36, impl.part2(input))
    }

    @Test
    fun `test part2 example2`() {
        val input = listOf("dc-end", "HN-start", "start-kj", "dc-start", "dc-HN", "LN-dc", "HN-end", "kj-sa", "kj-HN", "kj-dc")
        Assertions.assertEquals(103, impl.part2(input))
    }

    @Test
    fun `test part2 example3`() {
        val input = listOf("fs-end", "he-DX", "fs-he",
            "start-DX", "pj-DX", "end-zg", "zg-sl", "zg-pj",
            "pj-he", "RW-he", "fs-DX", "pj-RW", "zg-RW",
            "start-pj", "he-WI", "zg-he", "pj-fs", "start-RW")
        Assertions.assertEquals(3509, impl.part2(input))
    }

}

fun main() {
    with(Day12Impl()) {
        val input = File("src/Day12.txt").readLines()
        println("Part 1: ${part1(input)}")
        println("Part 2: ${part2(input)}")
    }
}
