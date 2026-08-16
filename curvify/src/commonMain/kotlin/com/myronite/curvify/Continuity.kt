package com.myronite.curvify

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import com.myronite.curvify.continuities.G2Continuity
import com.myronite.curvify.path.PathSegments
import com.myronite.curvify.path.toPath

/**
 * Defines how the outline of a rounded rectangle is generated, i.e. the smoothness of its corners.
 */
@Immutable
public interface Continuity {

    /** Creates the path segments of a rounded rectangle with the given corner radii. */
    public fun createRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments

    /** Creates the [Outline] of a rounded rectangle with the given corner radii. */
    public fun createRoundedRectangleOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline {
        val path =
            createRoundedRectanglePathSegments(
                width = size.width.toDouble(),
                height = size.height.toDouble(),
                topLeft = topLeft.toDouble(),
                topRight = topRight.toDouble(),
                bottomRight = bottomRight.toDouble(),
                bottomLeft = bottomLeft.toDouble()
            ).toPath()
        return Outline.Generic(path)
    }

    /** Creates the [Outline] of a capsule that fills [size]. */
    public fun createCapsuleOutline(size: Size): Outline {
        val radius = size.minDimension * 0.5f
        return createRoundedRectangleOutline(
            size = size,
            topLeft = radius,
            topRight = radius,
            bottomRight = radius,
            bottomLeft = radius
        )
    }

    /** Linearly interpolates from this continuity towards [stop] by [fraction]. */
    public fun lerp(stop: Continuity, fraction: Double): Continuity

    public companion object {

        /** The default continuity: a [G2Continuity] with the default profiles. */
        @Stable
        public val Default: Continuity = G2Continuity()
    }
}
