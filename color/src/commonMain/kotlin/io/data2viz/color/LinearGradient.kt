/*
 * Copyright (c) 2018-2019. data2viz sàrl.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

@file:Suppress("DEPRECATION")

package io.data2viz.color

import io.data2viz.geom.Point
import io.data2viz.math.Percent
import io.data2viz.math.pct

// TODO : move to "core.geom" ?
// TODO : remove access to x1, y1, x2, y2
interface HasStartAndEnd {
    var x1: Double
    var y1: Double
    var x2: Double
    var y2: Double

    var start: Point
        get() = Point(x1, y1)
        set(value) {
            x1 = value.x
            y1 = value.y
        }

    var end: Point
        get() = Point(x2, y2)
        set(value) {
            x2 = value.x
            y2 = value.y
        }
}

data class LinearGradientFirstColorBuilder
internal constructor(val start: Point, val end: Point) {
    fun withColor(startColor: Color, percent: Percent = 0.pct): LinearGradientSecondColorBuilder =
        LinearGradientSecondColorBuilder(this, ColorStop(percent, startColor))
}

data class LinearGradientSecondColorBuilder
internal constructor(val builder: LinearGradientFirstColorBuilder, val firstColor: ColorStop) {
    fun andColor(color: Color, percent: Percent = 100.pct): LinearGradient = LinearGradient()
        .apply {
            x1 = builder.start.x
            y1 = builder.start.y
            x2 = builder.end.x
            y2 = builder.end.y
            andColor(firstColor.color, firstColor.percent)
            andColor(color, percent)
        }
}

class LinearGradient
@Deprecated("Deprecated", ReplaceWith("Colors.Gradient.linear()", "io.data2viz.colors.Colors"))
internal constructor() : Gradient, HasStartAndEnd {

    override var x1: Double = .0
    override var y1: Double = .0
    override var x2: Double = .0
    override var y2: Double = .0

    private val colors = mutableListOf<ColorStop>()
    override val colorStops: List<ColorStop>
        get() = colors.toList()

    fun andColor(color: Color, percent: Percent): LinearGradient {
        colors.add(ColorStop(percent.coerceToDefault(), color))
        return this
    }
}