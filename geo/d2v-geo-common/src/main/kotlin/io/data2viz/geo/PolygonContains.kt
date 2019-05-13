package io.data2viz.geo

import io.data2viz.math.EPSILON
import io.data2viz.math.QUARTERPI
import io.data2viz.math.TAU
import kotlin.math.*

fun polygonContainsOld(polygon: List<List<DoubleArray>>, point: DoubleArray): Boolean {
    val lambda = point[0]
    val phi = point[1]
    val normal = doubleArrayOf(sin(lambda), -cos(lambda), 0.0)
    var angle = 0.0
    var winding = 0

    var sum = .0

    for (i in polygon.indices) {
        val ring = polygon[i]
        if (ring.isEmpty()) continue

        var point0 = ring.last()
        var lambda0 = point0[0]
        val phi0 = point0[1] / 2 + QUARTERPI
        var sinPhi0 = sin(phi0)
        var cosPhi0 = cos(phi0)

        for (j in ring.indices) {
            val point1 = ring[j]
            val lambda1 = point1[0]
            val phi1 = point1[1] / 2 + QUARTERPI
            val sinPhi1 = sin(phi1)
            val cosPhi1 = cos(phi1)
            val delta = lambda1 - lambda0
            val sign = if (delta >= 0) 1 else -1
            val absDelta = sign * delta
            val antimeridian = absDelta > PI
            val k = sinPhi0 * sinPhi1

            sum += atan2(k * sign * sin(absDelta), cosPhi0 * cosPhi1 + k * cos(absDelta))
            angle += if (antimeridian) delta + sign * TAU else delta

            // Are the longitudes either side of the point’s meridian (lambda),
            // and are the latitudes smaller than the parallel (phi)?
            if (antimeridian xor (lambda0 >= lambda) xor (lambda1 >= lambda)) {
                val arc = cartesianNormalize(cartesianCross(cartesian(point0), cartesian(point1)))
                val intersection = cartesianNormalize(cartesianCross(normal, arc))
                val phiArc = (if (antimeridian xor (delta >= 0)) -1 else 1) * asin(intersection[2])
                if (phi > phiArc || (phi == phiArc && ((arc[0] != .0 && !arc[0].isNaN()) || (arc[1] != .0 && !arc[1].isNaN())))) {
                    winding += if (antimeridian xor (delta >= 0)) 1 else -1
                }
            }

            lambda0 = lambda1
            sinPhi0 = sinPhi1
            cosPhi0 = cosPhi1
            point0 = point1
        }
    }

    // First, determine whether the South pole is inside or outside:
    //
    // It is inside if:
    // * the polygon winds around it in a clockwise direction.
    // * the polygon does not (cumulatively) wind around it, but has a negative
    //   (counter-clockwise) area.
    //
    // Second, count the (signed) number of times a segment crosses a lambda
    // from the point to the South pole.  If it is zero, then the point is the
    // same side as the South pole.

    return (angle < -EPSILON || angle < EPSILON && sum < -EPSILON) xor ((winding and 1) != 0)
}

fun polygonContains(polygon: List<List<DoubleArray>>, point: DoubleArray): Boolean {
    val lambda = point[0]
    val phi = point[1]
    val normal0 = sin(lambda)
    val normal1 = -cos(lambda)
    
    var angle = 0.0
    var winding = 0

    var sum = .0

    for (i in polygon.indices) {
        val ring = polygon[i]
        if (ring.isEmpty()) continue

        var point0 = ring.last()
        var lambda0 = point0[0]
        var phi0 = point0[1] / 2 + QUARTERPI

        var sinPhi0 = sin(phi0)
        var cosPhi0 = cos(phi0)


        for (j in ring.indices) {
            val point1 = ring[j]
            val lambda1 = point1[0]
            val phi1 = point1[1] / 2 + QUARTERPI

            val sinPhi1 = sin(phi1)
            val cosPhi1 = cos(phi1)
            val delta = lambda1 - lambda0
            val sign = if (delta >= 0) 1 else -1
            val absDelta = sign * delta
            val antimeridian = absDelta > PI
            val k = sinPhi0 * sinPhi1

            sum += atan2(k * sign * sin(absDelta), cosPhi0 * cosPhi1 + k * cos(absDelta))
            angle += if (antimeridian) delta + sign * TAU else delta

            // Are the longitudes either side of the point’s meridian (lambda),
            // and are the latitudes smaller than the parallel (phi)?
            if (antimeridian xor (lambda0 >= lambda) xor (lambda1 >= lambda)) {
                // Are the longitudes either side of the point’s meridian (lambda),
                // and are the latitudes smaller than the parallel (phi)?

                val cosPhiA = cos(phi0)
                val a0 = cosPhiA * cos(lambda0)
                val a1 = cosPhiA * sin(lambda0)
                val a2 = sin(phi0)


                val cosPhiB = cos(phi1)
                val b0 = cosPhiB * cos(lambda1)
                val b1 = cosPhiB * sin(lambda1)
                val b2 = sin(phi1)


                var d0 = a1 * b2 - a2 * b1
                var d1 = a2 * b0 - a0 * b2
                var d2 = a0 * b1 - a1 * b0

                val l = sqrt(d0 * d0 + d1 * d1 + d2 * d2)
                d0 /= l
                d1 /= l
                d2 /= l

                val intersectioncosPhiA = cos(normal1)
                val intersectiona0 = intersectioncosPhiA * cos(normal0)
                val intersectiona1 = intersectioncosPhiA * sin(normal0)
                val intersectiona2 = sin(normal1)


                val intersectioncosPhiB = cos(d1)
                val intersectionB0 = intersectioncosPhiB * cos(d0)
                val intersectionB1 = intersectioncosPhiB * sin(d0)
                val intersectionB2 = sin(d1)

                var intersectionD0 = intersectiona1 * intersectionB2 - intersectiona2 * intersectionB1
                var intersectionD1 = intersectiona2 * intersectionB0 - intersectiona0 * intersectionB2
                var intersectionD2 = intersectiona0 * intersectionB1 - intersectiona1 * intersectionB0

                val intersectionl =
                    sqrt(intersectionD0 * intersectionD0 + intersectionD1 * intersectionD1 + intersectionD2 * intersectionD2)
                intersectionD0 /= intersectionl
                intersectionD1 /= intersectionl
                intersectionD2 /= intersectionl

                val phiArc = (if (antimeridian xor (delta >= 0)) -1 else 1) * asin(intersectionD2)
                if (phi > phiArc || (phi == phiArc && ((d0 != .0 && !d0.isNaN()) || (d1 != .0 && !d1.isNaN())))) {
                    winding + if (antimeridian xor (delta >= 0)) 1 else -1
                }

            }

            sinPhi0 = sinPhi1
            cosPhi0 = cosPhi1
            lambda0 = lambda1
            phi0 = phi1
        }
    }

    // First, determine whether the South pole is inside or outside:
    //
    // It is inside if:
    // * the polygon winds around it in a clockwise direction.
    // * the polygon does not (cumulatively) wind around it, but has a negative
    //   (counter-clockwise) area.
    //
    // Second, count the (signed) number of times a segment crosses a lambda
    // from the point to the South pole.  If it is zero, then the point is the
    // same side as the South pole.

    return (angle < -EPSILON || angle < EPSILON && sum < -EPSILON) xor ((winding and 1) != 0)
}