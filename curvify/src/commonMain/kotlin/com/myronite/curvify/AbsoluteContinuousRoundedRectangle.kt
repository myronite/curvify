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

    public fun copy(
        topLeft: CornerSize = this.topStart,
        topRight: CornerSize = this.topEnd,
        bottomRight: CornerSize = this.bottomEnd,
        bottomLeft: CornerSize = this.bottomStart,
        continuity: Continuity = this.continuity
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

@Stable
public val AbsoluteContinuousRectangle: AbsoluteContinuousRoundedRectangle = AbsoluteContinuousRectangleImpl()

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

@Stable
public val AbsoluteContinuousCapsule: AbsoluteContinuousRoundedRectangle = AbsoluteContinuousCapsule()

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

@Stable
public fun AbsoluteContinuousRoundedRectangle(
    size: Dp,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

@Stable
public fun AbsoluteContinuousRoundedRectangle(
    @FloatRange(from = 0.0) size: Float,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

@Stable
public fun AbsoluteContinuousRoundedRectangle(
    @IntRange(from = 0, to = 100) percent: Int,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(percent),
        continuity = continuity
    )

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
