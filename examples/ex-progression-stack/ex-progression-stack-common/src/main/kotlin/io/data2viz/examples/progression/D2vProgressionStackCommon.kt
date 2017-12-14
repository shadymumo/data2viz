package io.data2viz.examples.progression

import io.data2viz.axis.*
import io.data2viz.color.*
import io.data2viz.format.formatter
import io.data2viz.scale.*
import io.data2viz.shape.StackGenerator
import io.data2viz.shape.offset.StackOffsets
import io.data2viz.shape.order.StackOrders
import io.data2viz.viz.*
import kotlin.math.absoluteValue

val legendLineHeight = 30.0

val margins = Margins(50.0, bottom = 4 * legendLineHeight + 40.0)

val width = 1600.0 - margins.hMargins
val height = 450.0

//Colors from http://colorbrewer2.org/#type=diverging&scheme=RdBu&n=7
val colorExpected = "#dddddd".color
val colorTest = "#b2182b".color
val colorCommon = "#2166ac".color
val colorJs = "#67a9cf".color
val colorJVM = "#d1e5f0".color
val colorList = listOf(colorCommon, colorJs, colorJVM, colorExpected, colorTest)

val ModuleState.totalLOC: Int
    get() = commonLOC + jsLOC + JVMLOC

fun checkData(data: List<ModuleState>) {
    data.forEach {
        check(it.estimatedLOC >= it.totalLOC) { "Estimated LOC under total LOC (${it.estimatedLOC} > ${it.totalLOC}" }
    }
}

fun VizContext.progression() {

    checkData(modules)

    transform {
        translate(x = margins.left, y = margins.top)
    }

    val maxEstimated = modules.maxBy { it.estimatedLOC }?.estimatedLOC?.toDouble() ?: .0
    val maxTest = modules.minBy { it.testsLoc }?.testsLoc?.toDouble() ?: .0

    val yScale = scales.continuous.linear {
        domain = listOf(maxTest, maxEstimated)
        range = listOf(height, .0)
    }
    yScale.nice()

    val y0 = yScale(0.0)

    val moduleNames = modules.map { it.name }

    val xScale = scales.band(moduleNames) {
        range = intervalOf(.0, width)
        padding = .1
    }

    axis(Orient.LEFT, scales.continuous.linear {
        domain = yScale.domain
        range = yScale.range
    }) {
        tickFormat = { it.absoluteValue.toInt().toString() }
    }


    // STACK LAYOUT
    val stack = StackGenerator<ModuleState>()
    stack.values = {
        arrayOf(it.commonLOC.toDouble(), it.jsLOC.toDouble(), it.JVMLOC.toDouble(), it.remainingLOC.toDouble(), it.testsLoc.toDouble())
    }
    stack.offset = StackOffsets.DIVERGING       // we want to separate tests (counted as negative lines of code) and program code (positive)
    stack.order = StackOrders.NONE              // we don't want to change the order defined by stack.values

    val stackLayout = stack.stack(modules.toTypedArray())

    legend()

    stackLayout.forEachIndexed { indexLOCType, LOCtypeLayout ->

        val moduleStack = LOCtypeLayout.stackedValues

        moduleStack.forEachIndexed { indexModule, moduleLayout ->
            rect {
                fill = colorList[indexLOCType]
                stroke = null
                x = xScale(moduleNames[indexModule])
                width = xScale.bandwidth

                // yScale has a "negative range" : height > 0 so we need to invert position "from" and "to"
                y = yScale(moduleLayout.to)
                height = (yScale(moduleLayout.from) - yScale(moduleLayout.to))
            }
        }
    }

    group {
        transform {
            translate(y = y0)
        }
        axis(Orient.BOTTOM, xScale)
    }

    //module vertical separations
    val modulesByVersion = modules
            .groupBy { it.version }

    val versions = modulesByVersion.keys.sorted()
    var xSeparation = xScale.paddingInner * xScale.step / 2
    (0 until versions.size).forEach {

        val key = versions[it]
        val packageCountForVersion = modulesByVersion.get(key)?.size ?: 0
        val versionWidth = xScale.step * packageCountForVersion

        text {
            anchor = TextAnchor.MIDDLE
            transform {
                translate(x = xSeparation + .5 * versionWidth)
            }
            textContent = "version $key"
        }
        xSeparation += versionWidth

        if (it != versions.size - 1)
            group {
                transform { translate(x = xSeparation) }
                line {
                    y1 = .0
                    y2 = height
                }
            }
    }
}

private fun VizContext.legend() {
    group {
        transform { translate(x = 20.0, y = height) }
        colorLegend(colorJVM, "Current kotlin jvm LOC", line = 0)
        colorLegend(colorJs, "Current kotlin js LOC", line = 1)
        colorLegend(colorCommon, "Current kotlin common LOC", line = 2)
        colorLegend(colorTest, "Current total test LOC", line = 3)
    }
}

private fun ParentItem.colorLegend(color: Color, legend: String, line: Int = 0) =
        group {
            transform {
                translate(y = line * legendLineHeight)
            }
            rect {
                fill = color
                stroke = colors.black
                height = 20.0
                width = 20.0
            }

            text {
                transform { translate(x = 25.0, y = 15.0) }
                textContent = legend
            }
        }