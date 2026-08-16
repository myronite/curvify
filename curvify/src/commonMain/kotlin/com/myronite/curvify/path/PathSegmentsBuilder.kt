package com.myronite.curvify.path

import com.myronite.curvify.core.Point

/** Builds [PathSegments] using [block]. */
public inline fun buildPathSegments(block: PathSegmentsBuilder.() -> Unit): PathSegments {
    return PathSegmentsBuilder().apply(block).build()
}

/** Creates the path segments of a circle with the given [center] and [radius]. */
public fun buildCirclePathSegments(center: Point, radius: Double): PathSegments {
    return listOf(PathSegment.Circle(center, radius))
}

/** A builder for [PathSegments], mirroring the Compose [Path] API in double precision. */
public class PathSegmentsBuilder {

    private var startPoint = Point.Zero
    private var currentPoint = Point.Zero
    private var didMove = false

    private var segments = mutableListOf<PathSegment>()

    /** Starts the path at the given point. Can only be called once, at the beginning. */
    public fun moveTo(x: Double, y: Double) {
        if (didMove) {
            throw IllegalStateException("moveTo can only be called once at the beginning of the path")
        }
        didMove = true
        startPoint = Point(x, y)
        currentPoint = startPoint
    }

    /** Appends a straight line to the given point. */
    public fun lineTo(x: Double, y: Double) {
        val segment = PathSegment.Line(currentPoint, Point(x, y))
        segments += segment
        currentPoint = segment.to
    }

    /** Appends a circular arc. Angles are in radians. */
    public fun arcTo(center: Point, radius: Double, startAngle: Double, sweepAngle: Double) {
        val segment = PathSegment.Arc(center, radius, startAngle, sweepAngle)
        segments += segment
        currentPoint = segment.to
    }

    /** Appends a cubic Bezier curve from the current point to (x3, y3). */
    public fun cubicTo(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) {
        val segment = PathSegment.Cubic(
            currentPoint,
            Point(x1, y1),
            Point(x2, y2),
            Point(x3, y3)
        )
        segments += segment
        currentPoint = segment.to
    }

    /** Appends a straight line back to the start point, closing the path. */
    public fun close() {
        val segment = PathSegment.Line(currentPoint, startPoint)
        segments += segment
        currentPoint = segment.to
    }

    /** Returns the built segments and resets the builder's segment list. */
    public fun build(): PathSegments {
        // Swap the backing list so the returned one is never mutated by later builder calls.
        val result = segments
        segments = mutableListOf()
        return result
    }
}
