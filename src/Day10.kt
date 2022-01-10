import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class Day10Impl {
    val CLOSE_DELIMITERS = hashMapOf<Char, Char>(
        ')' to '(',
        ']' to '[',
        '}' to '{',
        '>' to '<'
    )

    val OPEN_DELIMITERS = CLOSE_DELIMITERS.map { (k,v) -> v to k }.toMap()

    val ILLEGAL_DELIMITER_SCORES = hashMapOf<Char, Int>(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )

    val AUTO_COMPLETE_SCORES = hashMapOf<Char, Int>(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )

    fun part1(input: List<String>): Int {
        return input.map { line ->
            //print("\nprocessing line $line")
            val stack = ArrayDeque<Char>()
            //print("\nprocessing char ")
            for (c in line) {
                //print("$c ")
                when {
                    (c in CLOSE_DELIMITERS.values) -> stack.add(c) // is it an opening delimiter?
                    (c in CLOSE_DELIMITERS.keys) -> { // is it a closing delimiter
                        if (CLOSE_DELIMITERS[c] != stack.lastOrNull()) {
                            //print("illegal char $c, stack $stack")
                            return@map ILLEGAL_DELIMITER_SCORES[c]!!
                        } else {
                            stack.removeLastOrNull()
                        }
                    }
                }
            }
            0
        }.also { println("illegal char scores: $it") }.sum()
    }

    fun part2(input: List<String>): Long {
        return input.map { line ->
            //print("\nprocessing line $line")
            val stack = ArrayDeque<Char>()
            for (c in line) {
                //print("$c ")
                when {
                    (c in CLOSE_DELIMITERS.values) -> stack.add(c) // is it an opening delimiter?
                    (c in CLOSE_DELIMITERS.keys) -> { // is it a closing delimiter
                        if (CLOSE_DELIMITERS[c] != stack.lastOrNull()) {
                            //print("illegal char $c, stack $stack")
                            return@map null
                        } else {
                            stack.removeLastOrNull()
                        }
                    }
                }
            }
            stack
        }
            //.onEach { println("incomplete chars: ${it?.joinToString(prefix="", postfix = "", separator = "")}") }
            .filterNotNull().map { it.map { c -> OPEN_DELIMITERS[c] }.reversed() }
            //.onEach { println("autocomplete chars: ${it?.joinToString(prefix="", postfix = "", separator = "")}") }
            .map { it.fold(0L) { total, el -> 5*total + AUTO_COMPLETE_SCORES[el]!! } }
            .also { println("autocomplete scores: $it") }
            .sorted()
            .let {
                it[it.size/2]
            }
    }
}

class Day10Test {

    private val impl = Day10Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test part1 example`() {
        val input = listOf<String>(
            "[({(<(())[]>[[{[]{<()<>>",
            "[(()[<>])]({[<{<<[]>>(",
            "{([(<{}[<>[]}>{[]{[(<()>",
            "(((({<>}<{<{<>}{[]{[]{}",
            "[[<[([]))<([[{}[[()]]]",
            "[{[{({}]{}}([{[{{{}}([]",
            "{<[[]]>}<{[{[{[]{()[[[]",
            "[<(<(<(<{}))><([]([]()",
            "<{([([[(<>()){}]>(<<{{",
            "<{([{{}}[<[[[<>{}]]]>[]]"
        )
        Assertions.assertEquals(26397, impl.part1(input))
    }

    @Test
    fun `test part2 example`() {
        val input = listOf<String>(
            "[({(<(())[]>[[{[]{<()<>>",
            "[(()[<>])]({[<{<<[]>>(",
            "{([(<{}[<>[]}>{[]{[(<()>",
            "(((({<>}<{<{<>}{[]{[]{}",
            "[[<[([]))<([[{}[[()]]]",
            "[{[{({}]{}}([{[{{{}}([]",
            "{<[[]]>}<{[{[{[]{()[[[]",
            "[<(<(<(<{}))><([]([]()",
            "<{([([[(<>()){}]>(<<{{",
            "<{([{{}}[<[[[<>{}]]]>[]]"
        )
        Assertions.assertEquals(288957, impl.part2(input))
    }

}

fun main() {
    with(Day10Impl()) {
        val input = File("src/Day10.txt").readLines()
        println("Part 1: ${part1(input)}")
        println("Part 2: ${part2(input)}")
    }
}
