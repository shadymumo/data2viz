package io.data2viz.viz

import io.data2viz.color.ColorOrGradient
import io.data2viz.core.CssClass
import io.data2viz.path.PathAdapter

/**
 * Common interface to bootstrap visualization into different platform contexts.
 */
//interface VizContext : Group


/**
 * Base class for holding both memory version of
 */
class Viz {

    val root = Group()

    lateinit var renderer: VizRenderer

    fun render() {
        renderer.render(this)
    }

}


interface VizElement


interface StateableElement {
    var stateManager: StateManager?
}


/**
 * Indicate an element on which we can apply a Transformation.
 * todo implement other transformation (rotate, ...)
 */
class Transform {
    var translate:Translation? = null
    fun translate(x: Double = 0.0, y: Double = 0.0) {
        translate = Translation(x,y)
    }
}

data class Translation(var x: Double = 0.0, var y: Double = 0.0)

interface StyledElement {
    fun addClass(cssClass: CssClass)
}

interface PathVizElement : VizElement, Shape, PathAdapter


interface Shape : HasFill, HasStroke


/**
 * All properties of stroke
 * Todo add remaining common properties
 */
interface HasStroke {
    var stroke: ColorOrGradient?
    var strokeWidth: Double?
}

interface HasFill {
    var fill: ColorOrGradient?
}


data class Margins(val top: Double, val right: Double = top, val bottom: Double = top, val left: Double = right) {
    val hMargins = right + left
    val vMargins = top + bottom
}

fun newGroup(): Group = Group()
fun newLine(): Line = Line()
fun newRect(): Rect = Rect()
fun newCircle(): Circle = Circle()
fun newText(): Text = Text()
fun newPath(): PathNode = PathNode()

