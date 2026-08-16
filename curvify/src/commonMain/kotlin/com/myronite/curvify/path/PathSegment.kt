package com.myronite.curvify.path

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.myronite.curvify.core.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** A single segment of a shape path. */
@Immutable
public sealed interface PathSegment {

    /** The start point of the segment. */
    public val from: Point

    /** The end point of the segment. */
    public val to: Point

    /** Appends this segment to [path]. */
    public fun drawTo(path: Path)

    /** A straight line from [from] to [to]. */
    public data class Line(
        override val from: Point,
        override val to: Point
    ) : PathSegment {

        override fun drawTo(path: Path) {
            path.lineTo(to.x.toFloat(), to.y.toFloat())
        }
    }

    /** A circular arc around [center]. */
    public data class Arc(
        public val center: Point,
        public val radius: Double,
        public val startAngle: Double,
        public val sweepAngle: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(
                center.x + cos(startAngle) * radius,
                center.y + sin(startAngle) * radius
            )

        override val to: Point
            get() = Point(
                center.x + cos(startAngle + sweepAngle) * radius,
                center.y + sin(startAngle + sweepAngle) * radius
            )

        override fun drawTo(path: Path) {
            path.arcTo(
                rect = Rect(
                    (center.x - radius).toFloat(),
                    (center.y - radius).toFloat(),
                    (center.x + radius).toFloat(),
                    (center.y + radius).toFloat()
                ),
                startAngleDegrees = (startAngle * (180.0 / PI)).toFloat(),
                sweepAngleDegrees = (sweepAngle * (180.0 / PI)).toFloat(),
                forceMoveTo = false
            )
        }
    }

    /** A full circle around [center]. */
    public data class Circle(
        public val center: Point,
        public val radius: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(center.x + radius, center.y)

        override val to: Point
            get() = from

        override fun drawTo(path: Path) {
            path.addOval(
                Rect(
                    (center.x - radius).toFloat(),
                    (center.y - radius).toFloat(),
                    (center.x + radius).toFloat(),
                    (center.y + radius).toFloat()
                )
            )
        }
    }

    /** A cubic Bezier curve from [p0] to [p3], controlled by [p1] and [p2]. */
    public data class Cubic(
        public val p0: Point,
        public val p1: Point,
        public val p2: Point,
        public val p3: Point
    ) : PathSegment {

        override val from: Point
            get() = p0

        override val to: Point
            get() = p3

        override fun drawTo(path: Path) {
            path.cubicTo(
                p1.x.toFloat(), p1.y.toFloat(),
                p2.x.toFloat(), p2.y.toFloat(),
                p3.x.toFloat(), p3.y.toFloat()
            )
        }
    }
}
