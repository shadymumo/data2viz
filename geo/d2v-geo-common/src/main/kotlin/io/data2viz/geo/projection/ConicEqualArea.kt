package io.data2viz.geo.projection

import io.data2viz.math.EPSILON
import kotlin.math.*


class ConicEqualAreaRawProjectableInvertable(var y0:Double = 0.0,
                                  var y1:Double = io.data2viz.math.PI / 3.0) : ProjectableInvertable {

    var sy0 = sin(y0)
    var n = (sy0 + sin(y1)) / 2;


    var c = 1 + sy0 * (2 * n - sy0)
    var r0 = sqrt(c) / n;


    // TODO refactor
    val cylindricalEqualAreaRaw = CylindricalEqualAreaRaw(y0)

    override fun invert(x: Double, y: Double): DoubleArray {
        return if (abs(n) < EPSILON) {
            cylindricalEqualAreaRaw.invert(x, y)
        } else {
            var r0y = r0 - y;
            doubleArrayOf(atan2(x, abs(r0y)) / n * sign(r0y), asin((c - (x * x + r0y * r0y) * n * n) / (2 * n)))

        }
    }


    override fun project(lambda: Double, phi: Double): DoubleArray {

        return if (abs(n) < EPSILON) {
            cylindricalEqualAreaRaw.project(lambda, phi)
        } else {
            var r = sqrt(c - 2 * n * sin(phi)) / n
            // TODO: check
//            return [r * sin(x *= n), r0 - r * cos(x)];
            doubleArrayOf(r * sin(lambda * n), r0 - r * cos(lambda));
        }


    }

    override fun projectLambda(lambda: Double, phi: Double): Double {
        return if (abs(n) < EPSILON) {
            cylindricalEqualAreaRaw.projectLambda(lambda, phi)
        } else {
            var r = sqrt(c - 2 * n * sin(phi)) / n
            r * sin(lambda * n)
        }
    }

    override fun projectPhi(lambda: Double, phi: Double): Double {
        return if (abs(n) < EPSILON) {
            cylindricalEqualAreaRaw.projectPhi(lambda, phi)
        } else {
            var r = sqrt(c - 2 * n * sin(phi)) / n
            r0 - r * cos(lambda)
        }
    }

}

fun conicEqualAreaProjection() = ConicProjection(ConicEqualAreaRawProjectableInvertable()).also {
    it.scale = 155.424
    it.center = doubleArrayOf(0.0, 33.6442)
}


//
//export default function() {
//    return conicProjection(conicEqualAreaRaw)
//        .scale(155.424)
//        .center([0, 33.6442]);
//}

//import {abs, asin, atan2, cos, epsilon, sign, sin, sqrt} from "../math";
//import {conicProjection} from "./conic";
//import {cylindricalEqualAreaRaw} from "./cylindricalEqualArea";

//fun conicEqualAreaRaw(y0: Double, y1: Double) {
//    var sy0 = sin(y0)
//    var n = (sy0 + sin(y1)) / 2;
//
//    // Are the parallels symmetrical around the Equator?
//    if (abs(n) < EPSILON) return cylindricalEqualAreaRaw(y0);
//
//    var c = 1 + sy0 * (2 * n - sy0), r0 = sqrt(c) / n;
//
//    function project (x, y) {
//        var r = sqrt(c - 2 * n * sin(y)) / n;
//        return [r * sin(x *= n), r0 - r * cos(x)];
//    }
//
//    project.invert = function(x, y) {
//        var r0y = r0 - y;
//        return [atan2(x, abs(r0y)) / n * sign(r0y), asin((c - (x * x + r0y * r0y) * n * n) / (2 * n))];
//    };
//
//    return project;
//}

//export default function() {
//    return conicProjection(conicEqualAreaRaw)
//        .scale(155.424)
//        .center([0, 33.6442]);
//}


//
//import {abs, asin, atan2, cos, epsilon, sign, sin, sqrt} from "../math";
//import {conicProjection} from "./conic";
//import {cylindricalEqualAreaRaw} from "./cylindricalEqualArea";
//
//export function conicEqualAreaRaw(y0, y1) {
//    var sy0 = sin(y0), n = (sy0 + sin(y1)) / 2;
//
//    // Are the parallels symmetrical around the Equator?
//    if (abs(n) < epsilon) return cylindricalEqualAreaRaw(y0);
//
//    var c = 1 + sy0 * (2 * n - sy0), r0 = sqrt(c) / n;
//
//    function project(x, y) {
//        var r = sqrt(c - 2 * n * sin(y)) / n;
//        return [r * sin(x *= n), r0 - r * cos(x)];
//    }
//
//    project.invert = function(x, y) {
//        var r0y = r0 - y;
//        return [atan2(x, abs(r0y)) / n * sign(r0y), asin((c - (x * x + r0y * r0y) * n * n) / (2 * n))];
//    };
//
//    return project;
//}
//
//export default function() {
//    return conicProjection(conicEqualAreaRaw)
//        .scale(155.424)
//        .center([0, 33.6442]);
//}