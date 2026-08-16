package com.myronite.curvify

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * A [ContinuousRoundedRectangle] that ignores [LayoutDirection]: corners are always resolved
 * from the top-left in absolute coordinates instead of from the layout start.
 */
@Immutable
public open class AbsoluteContinuousRoundedRectangle(
    topLeft: CornerSize,
    topRight: CornerSize,
    bottomRight: CornerSize,
    bottomLeft: CornerSize,
    continuity: Continuity = Continuity.Default
) : ContinuousRoundedRectangle(
    topStart = topLeft,
    topEnd = topRight,
    bottomEnd = bottomRight,
    bottomStart = bottomLeft,
    continuity = continuity
) {

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline = createClampedOutline(
        size = size,
        topLeft = topStart,
        topRight = topEnd,
        bottomRight = bottomEnd,
        bottomLeft = bottomStart
    )

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): AbsoluteContinuousRoundedRectangle {
        return AbsoluteContinuousRoundedRectangle(
            topLeft = topStart,
            topRight = topEnd,
            bottomRight = bottomEnd,
            bottomLeft = bottomStart,
            continuity = continuity
        )
    }

    /** Returns a copy of this shape with the given properties replaced.
     *  Defaults are inherited from [ContinuousRoundedRectangle.copy]. */
    override fun copy(
        topLeft: CornerSize,
        topRight: CornerSize,
        bottomRight: CornerSize,
        bottomLeft: CornerSize,
        continuity: Continuity
    ): AbsoluteContinuousRoundedRectangle {
        return AbsoluteContinuousRoundedRectangle(
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft,
            continuity = continuity
        )
    }

    override fun toString(): String {
        return "AbsoluteContinuousRoundedRectangle(topLeft=$topStart, topRight=$topEnd, bottomRight=$bottomEnd, " +
                "bottomLeft=$bottomStart, continuity=$continuity)"
    }
}

/** A rectangle shape with continuous corners (zero radius), unaffected by layout direction. */
@Stable
public val AbsoluteContinuousRectangle: AbsoluteContinuousRoundedRectangle = AbsoluteContinuousRectangleImpl()

/** Creates a rectangle shape with continuous corners (zero radius), unaffected by layout direction. */
@Suppress("FunctionName")
@Stable
public fun AbsoluteContinuousRectangle(continuity: Continuity = Continuity.Default): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRectangleImpl(continuity)

@Immutable
private data class AbsoluteContinuousRectangleImpl(
    override val continuity: Continuity = Continuity.Default
) : AbsoluteContinuousRoundedRectangle(
    topLeft = ZeroCornerSize,
    topRight = ZeroCornerSize,
    bottomRight = ZeroCornerSize,
    bottomLeft = ZeroCornerSize,
    continuity = continuity
) {

    override fun toString(): String {
        return "AbsoluteContinuousRectangle(continuity=$continuity)"
    }
}

/** A capsule shape with continuous corners, unaffected by layout direction. */
@Stable
public val AbsoluteContinuousCapsule: AbsoluteContinuousRoundedRectangle = AbsoluteContinuousCapsule()

/** Creates a capsule shape with continuous corners, unaffected by layout direction. */
@Suppress("FunctionName")
@Stable
public fun AbsoluteContinuousCapsule(continuity: Continuity = Continuity.Default): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousCapsuleImpl(continuity)

@Immutable
private data class AbsoluteContinuousCapsuleImpl(
    override val continuity: Continuity = Continuity.Default
) : AbsoluteContinuousRoundedRectangle(
    topLeft = FullCornerSize,
    topRight = FullCornerSize,
    bottomRight = FullCornerSize,
    bottomLeft = FullCornerSize,
    continuity = continuity
) {

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline = continuity.createCapsuleOutline(size)

    override fun toString(): String {
        return "AbsoluteContinuousCapsule(continuity=$continuity)"
    }
}

/** Creates a shape with the same [corner] on all four corners. */
@Stable
public fun AbsoluteContinuousRoundedRectangle(
    corner: CornerSize,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = corner,
        topRight = corner,
        bottomRight = corner,
        bottomLeft = corner,
        continuity = continuity
    )

/** Creates a shape with the same corner of [size] on all four corners. */
@Stable
public fun AbsoluteContinuousRoundedRectangle(
    size: Dp,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

/** Creates a shape with the same corner of [size] pixels on all four corners. */
@Stable
public fun AbsoluteContinuousRoundedRectangle(
    @FloatRange(from = 0.0) size: Float,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

/** Creates a shape with the same corner of [percent] on all four corners. */
@Stable
public fun AbsoluteContinuousRoundedRectangle(
    @IntRange(from = 0, to = 100) percent: Int,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(percent),
        continuity = continuity
    )

/** Creates a shape with individual corners given in [Dp]. */
@Stable
public fun AbsoluteContinuousRoundedRectangle(
    topLeft: Dp = 0f.dp,
    topRight: Dp = 0f.dp,
    bottomRight: Dp = 0f.dp,
    bottomLeft: Dp = 0f.dp,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = CornerSize(topLeft),
        topRight = CornerSize(topRight),
        bottomRight = CornerSize(bottomRight),
        bottomLeft = CornerSize(bottomLeft),
        continuity = continuity
    )

/** Creates a shape with individual corners given in pixels. */
@Stable
public fun AbsoluteContinuousRoundedRectangle(
    @FloatRange(from = 0.0) topLeft: Float = 0f,
    @FloatRange(from = 0.0) topRight: Float = 0f,
    @FloatRange(from = 0.0) bottomRight: Float = 0f,
    @FloatRange(from = 0.0) bottomLeft: Float = 0f,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = CornerSize(topLeft),
        topRight = CornerSize(topRight),
        bottomRight = CornerSize(bottomRight),
        bottomLeft = CornerSize(bottomLeft),
        continuity = continuity
    )

/** Creates a shape with individual corners given in percents. */
@Stable
public fun AbsoluteContinuousRoundedRectangle(
    @IntRange(from = 0, to = 100) topLeftPercent: Int = 0,
    @IntRange(from = 0, to = 100) topRightPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomRightPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomLeftPercent: Int = 0,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = CornerSize(topLeftPercent),
        topRight = CornerSize(topRightPercent),
        bottomRight = CornerSize(bottomRightPercent),
        bottomLeft = CornerSize(bottomLeftPercent),
        continuity = continuity
    )
