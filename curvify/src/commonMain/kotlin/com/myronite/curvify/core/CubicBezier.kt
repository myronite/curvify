package com.myronite.curvify.core

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/** A cubic Bezier curve defined by four control points. */
@Immutable
public data class CubicBezier(
    public val p0: Point,
    public val p1: Point,
    public val p2: Point,
    public val p3: Point
) {

    @Stable
    public operator fun times(operand: Double): CubicBezier {
        return CubicBezier(
            p0 * operand,
            p1 * operand,
            p2 * operand,
            p3 * operand
        )
    }
}
