package io.data2viz.shape.curve

import io.data2viz.path.Path
import io.data2viz.path.PathAdapter
import io.data2viz.shape.Curve
import io.data2viz.shape.LineGenerator
import io.data2viz.shape.const
import io.data2viz.test.TestBase

data class Point(val x: Int, val y: Int)

open class CurveTest(val curve: (PathAdapter) -> Curve, val defined: (Point) -> Boolean = const(true)) : TestBase() {

    fun pt(x: Int, y: Int) = Point(x, y)

    fun line(vararg points: Point): String {
        val lineGenerator = LineGenerator<Point>().apply {
            curve = this@CurveTest.curve
            defined = this@CurveTest.defined
            x = { it.x.toDouble() }
            y = { it.y.toDouble() }
        }
        val context = Path()
        return lineGenerator.render(listOf(*points), context).svgPath.round()
    }

}
