import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

private fun Pair<Int, Int>.toRange() = if (first <= second) first .. second else first downTo second

data class Point (val x: Int, val y: Int): Comparable<Point> {
    override fun compareTo(other: Point) = compareValuesBy(this, other,
        { it.x },
        { it.y }
    )
}

class LineSegment(_start: Point, _end: Point) {
    var start: Point = Point(0,0)
        private set
    var end: Point = Point(0,0)
        private set

    // Swap start and end so that start is always less than end
    init {
        if (_end < _start) {
            start = _end
            end = _start
        } else {
            start = _start
            end = _end
        }
    }

    override fun equals(other: Any?): Boolean {
        val otherLs = other as LineSegment?
        return (start == otherLs?.start && end == otherLs.end)
                || (start == otherLs?.end && end == otherLs.start)
    }

    override fun toString(): String {
        return "(${start.x}, ${start.y}) -> (${end.x}, ${end.y})"
    }

    fun isHorizontal() = (start.y == end.y)
    fun isVertical() = (start.x == end.x)

    fun containsPoint(point: Point) = when {
            isHorizontal() -> (point.y == start.y) && (point.x >= start.x) && (point.x <= end.x)
            isVertical() -> (point.x == start.x) && (point.y >= start.y) && (point.y <= end.y)
            else -> false
        }

    private fun horizontalIntersect(ls: LineSegment): Set<Point> {
        if (start.y != ls.start.y) {
            // Parallel lines
            return emptySet()
        }
        return (start.x .. end.x).intersect(ls.start.x .. ls.end.x).map { Point(it, start.y) }.toSet()
    }

    private fun verticalIntersect(ls: LineSegment): Set<Point> {
        if (start.x != ls.start.x) {
            // Parallel lines
            return emptySet()
        }
        return (start.y .. end.y).intersect(ls.start.y .. ls.end.y).map { Point(start.x, it) }.toSet()
    }

    // horizontal and vertical line segment
    private fun perpendicularIntersect(ls: LineSegment): Set<Point> {
        check((isHorizontal() && ls.isVertical()) || (isVertical() && ls.isHorizontal())) { "Incompatible line types" }
        val point1 = Point(start.x, ls.start.y)
        val point2 = Point(ls.start.x, start.y)
        return when {
            containsPoint(point1) && ls.containsPoint(point1) -> setOf(point1)
            containsPoint(point2) && ls.containsPoint(point2) -> setOf(point2)
            else -> emptySet()
        }
    }

    /**
     * Calculate intersection points of line segments.
     * Returns
     * Empty list if the line segments don't intersect.
     * List with size one for single point intersection
     * List with more than one point if the line segments overlap
     */
    fun intersect(ls: LineSegment): Set<Point> = when {
        isHorizontal() && ls.isHorizontal() -> horizontalIntersect(ls)
        isVertical() && ls.isVertical() -> verticalIntersect(ls)
        isHorizontal() && ls.isVertical() -> perpendicularIntersect(ls)
        isVertical() && ls.isHorizontal() -> perpendicularIntersect(ls)
        else -> listPoints().intersect(ls.listPoints())
    }

    fun listPoints(): Set<Point> = when {
        isHorizontal() -> ((start.x to end.x).toRange()).map { Point(it, start.y) }
        isVertical() -> ((start.y to end.y).toRange() ).map { Point(start.x, it) }
        else -> ((start.x to end.x).toRange()).zip((start.y to end.y).toRange()).map { Point(it.first, it.second) }
    }.toSet()

}

private fun File.toLineSegments(): Sequence<LineSegment> = sequence {
    useLines { lines ->
        for (line in lines) {
            val ls = line.trim().split("->", ",").map { xy -> xy.trim().toInt()}
            check(ls.size == 4) { "Invalid input line: $ls" }
            yield(LineSegment(Point(ls[0], ls[1]), Point(ls[2], ls[3])))
        }
    }
}

class Day05Impl {
    fun part1(input: Sequence<LineSegment>): Int {
        val horVerLines = input.filter {
            it.isHorizontal() || it.isVertical()
        }.toList()
        val intersectionPoints = mutableSetOf<Point>()
        for (i in 0 until horVerLines.size - 1) {
            for (j in i+1 until horVerLines.size) {
                horVerLines[i].intersect(horVerLines[j]).also {
                    // println("Line ${horVerLines[i]} x Line ${horVerLines[j]} = ${it}")
                    intersectionPoints.addAll(it)
                }
            }
        }
        // println(intersectionPoints)
        return intersectionPoints.size
    }

    fun part2(input: Sequence<LineSegment>): Int {
        val allLinePoints = input.toList().flatMap { line ->
            line.listPoints().also {
                // println("$line = $it")
            }
        }
        return allLinePoints.groupBy { it }.filterValues { points -> points.size > 1 }.keys.size
    }
}

class Day05Test {

    private val impl = Day05Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test read input file`() {
        val expectedLineSegments = listOf<LineSegment>(
            LineSegment(Point(0,9), Point(5,9)),
            LineSegment(Point(0,8), Point(8,0)),
            LineSegment(Point(3,4), Point(9,4)),
            LineSegment(Point(2,1), Point(2,2)),
            LineSegment(Point(7,0), Point(7,4)),
            LineSegment(Point(2,0), Point(6,4)),
            LineSegment(Point(0,9), Point(2,9)),
            LineSegment(Point(1,4), Point(3,4)),
            LineSegment(Point(0,0), Point(8,8)),
            LineSegment(Point(5,5), Point(8,2))
        )
        val actualLineSegments = File("src/Day05_test.txt").toLineSegments().toList()
        Assertions.assertEquals(expectedLineSegments, actualLineSegments)
    }

    @Test
    fun `test intersection of two parallel horizontal lines`() {
        val ls1 = LineSegment(Point(1,3), Point(5,3))
        val ls2 = LineSegment(Point(2, 6), Point(8,6))
        Assertions.assertEquals(ls1.intersect(ls2), emptySet<Point>())
    }

    @Test
    fun `test part1 example`() {
        val lineSegments = listOf<LineSegment>(
            LineSegment(Point(0,9), Point(5,9)),
            LineSegment(Point(8,0), Point(0,8)),
            LineSegment(Point(9,4), Point(3,4)),
            LineSegment(Point(2,2), Point(2,1)),
            LineSegment(Point(7,0), Point(7,4)),
            LineSegment(Point(6,4), Point(2,0)),
            LineSegment(Point(0,9), Point(2,9)),
            LineSegment(Point(3,4), Point(1,4)),
            LineSegment(Point(0,0), Point(8,8)),
            LineSegment(Point(5,5), Point(8,2))
        )
        Assertions.assertEquals(5, impl.part1(lineSegments.asSequence()))
    }

    @Test
    fun `test part2 example`() {
        val lineSegments = listOf<LineSegment>(
            LineSegment(Point(0,9), Point(5,9)),
            LineSegment(Point(8,0), Point(0,8)),
            LineSegment(Point(9,4), Point(3,4)),
            LineSegment(Point(2,2), Point(2,1)),
            LineSegment(Point(7,0), Point(7,4)),
            LineSegment(Point(6,4), Point(2,0)),
            LineSegment(Point(0,9), Point(2,9)),
            LineSegment(Point(3,4), Point(1,4)),
            LineSegment(Point(0,0), Point(8,8)),
            LineSegment(Point(5,5), Point(8,2))
        )
        Assertions.assertEquals(12, impl.part2(lineSegments.asSequence()))
    }

}

fun main() {
    with(Day05Impl()) {
        val lineSegments = File("src/Day05.txt").toLineSegments()
        println("Part 1: ${part1(lineSegments)}")
        println("Part 2: ${part2(lineSegments)}")
    }
}
