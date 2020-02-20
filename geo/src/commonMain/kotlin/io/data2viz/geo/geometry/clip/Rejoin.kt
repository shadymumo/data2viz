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

package io.data2viz.geo.geometry.clip

import io.data2viz.geo.GeoPoint
import io.data2viz.geo.KPoint
import io.data2viz.geo.Point3D
import io.data2viz.geo.stream.Stream
import io.data2viz.math.EPSILON
import kotlin.math.abs

data class Intersection<T>(
    val point: T,
    val points: List<T>?,
    var other: Intersection<T>?,
    var entry: Boolean,
    var visited: Boolean = false,
    var next: Intersection<T>? = null,
    var previous: Intersection<T>? = null
)

/**
 * A generalized polygon clipping algorithm: given a polygon that has been cut into its visible line segments,
 * and rejoins the segments by interpolating along the postClip edge.
 */
fun <T: KPoint> rejoin(
    segments: List<List<T>>,
    compareIntersection: Comparator<Intersection<T>>,
    startInside: Boolean,
    clipper: Clipper<T>,
    stream: Stream<T>
) {
    val subject = mutableListOf<Intersection<T>>()
    val clip = mutableListOf<Intersection<T>>()

    segments.forEach {segment ->
        val n = segment.size - 1
        if (n <= 0) return

        var p0 = segment[0]
        val p1 = segment[n]

        // If the first and last points of a segment are coincident, then treat as a
        // closed ring.
        // TODO if all rings are closed, then the winding order of the exterior ring should be checked.
        if (pointEqual(p0, p1)) {
            stream.lineStart()
            (0 until n).forEach {index ->
                p0 = segment[index]
                stream.point(p0)
            }
            stream.lineEnd()
            return
        }

        var x = Intersection(p0, segment, null, true)
        subject.add(x)

        x.other = Intersection(p0, null, x, false)
        clip.add(x.other!!)

        x = Intersection(p1, segment, null, false)
        subject.add(x)

        x.other = Intersection(p1, null, x, true)
        clip.add(x.other!!)
    }

    if (subject.isEmpty()) return

    clip.sortWith(compareIntersection)
    link(subject)
    link(clip)

    var newStartInside = startInside

    clip.forEach {
        newStartInside = !newStartInside
        it.entry = newStartInside
    }

    val start = subject[0]

    while (true) {

        // Find first unvisited intersection.
        var current = start
        var isSubject = true

        while (current.visited) {
            current = current.next!!
            if (current == start) return
        }

        var points = current.points
        stream.lineStart()

        do {
            current.other!!.visited = true
            current.visited = true

            if (current.entry) {
                if (isSubject) {
                    points?.forEach { stream.point(it) }
                } else {
                    clipper.interpolate(current.point, current.next!!.point, 1, stream)
                }
                current = current.next!!
            } else {
                if (isSubject) {
                    points = current.previous!!.points
                    points?.asReversed()?.forEach { stream.point(it) }
                } else {
                    clipper.interpolate(current.point, current.previous!!.point, -1, stream)
                }
                current = current.previous!!
            }
            current = current.other!!
            points = current.points
            isSubject = !isSubject

        } while (!current.visited)

        stream.lineEnd()
    }
}

fun <T> link(list: List<Intersection<T>>) {
    if (list.isEmpty()) return

    var a = list.first()
    var b:Intersection<T>

    (1 until list.size).forEach { index ->
        a.next = list[index]
        b = list[index]
        b.previous = a
        a = b
    }
    b = list.first()
    a.next = b
    b.previous = a
}

internal fun <POINT: KPoint> pointEqual(p0: POINT, p1: POINT): Boolean =
    when(p0) {
        is GeoPoint ->
                abs(p0.lon.rad - (p1 as GeoPoint).lon.rad) < EPSILON &&
                abs(p0.lat.rad - p1.lat.rad) < EPSILON

        is Point3D ->
                    abs(p0.x - (p1 as Point3D).x) < EPSILON &&
                    abs(p0.y - p1.y) < EPSILON

        else -> false //why KPoint is a sealed class
    }




/**
 * Todo check if EPSILON is enought for GeoJsonPoint (1e-6 rad may be big).
 */
internal fun pointEqual(p0: GeoPoint, p1: GeoPoint): Boolean =
        abs(p0.lon.rad - p1.lon.rad) < EPSILON &&
        abs(p0.lat.rad - p1.lat.rad) < EPSILON
