package com.myronite.curvify

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.min

/**
 * A [CornerBasedShape] whose corner smoothness is defined by [continuity].
 *
 * Corners are resolved relative to the layout start and mirrored in RTL layouts; use
 * [AbsoluteContinuousRoundedRectangle] for corners that are always absolute.
 */
@Immutable
public open class ContinuousRoundedRectangle(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
    public open val continuity: Continuity = Continuity.Default
) : CornerBasedShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomEnd = bottomEnd,
    bottomStart = bottomStart
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
        topLeft = if (layoutDirection == Ltr) topStart else topEnd,
        topRight = if (layoutDirection == Ltr) topEnd else topStart,
        bottomRight = if (layoutDirection == Ltr) bottomEnd else bottomStart,
        bottomLeft = if (layoutDirection == Ltr) bottomStart else bottomEnd
    )

    /** Creates the outline with all corner radii clamped to the shape bounds. */
    protected fun createClampedOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline {
        // rectangle
        if (topLeft + topRight + bottomRight + bottomLeft == 0f) {
            return Outline.Rectangle(size.toRect())
        }

        val maxRadius = min(size.width, size.height) * 0.5f
        return continuity.createRoundedRectangleOutline(
            size = size,
            topLeft = topLeft.fastCoerceIn(0f, maxRadius),
            topRight = topRight.fastCoerceIn(0f, maxRadius),
            bottomRight = bottomRight.fastCoerceIn(0f, maxRadius),
            bottomLeft = bottomLeft.fastCoerceIn(0f, maxRadius)
        )
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): ContinuousRoundedRectangle {
        return ContinuousRoundedRectangle(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
            continuity = continuity
        )
    }

    /** Returns a copy of this shape with the given properties replaced. */
    public open fun copy(
        topStart: CornerSize = this.topStart,
        topEnd: CornerSize = this.topEnd,
        bottomEnd: CornerSize = this.bottomEnd,
        bottomStart: CornerSize = this.bottomStart,
        continuity: Continuity = this.continuity
    ): ContinuousRoundedRectangle {
        return ContinuousRoundedRectangle(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
            continuity = continuity
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContinuousRoundedRectangle) return false
        // Directional and absolute variants render differently under RTL, keep them unequal.
        if ((other is AbsoluteContinuousRoundedRectangle) != (this is AbsoluteContinuousRoundedRectangle)) return false

        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomEnd != other.bottomEnd) return false
        if (bottomStart != other.bottomStart) return false
        if (continuity != other.continuity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        result = 31 * result + continuity.hashCode()
        return result
    }

    override fun toString(): String {
        return "ContinuousRoundedRectangle(topStart=$topStart, topEnd=$topEnd, bottomEnd=$bottomEnd, " +
                "bottomStart=$bottomStart, continuity=$continuity)"
    }
}

/** A rectangle shape with continuous corners (zero radius). */
@Stable
public val ContinuousRectangle: ContinuousRoundedRectangle = ContinuousRectangleImpl()

/** Creates a rectangle shape with continuous corners (zero radius). */
@Suppress("FunctionName")
@Stable
public fun ContinuousRectangle(continuity: Continuity = Continuity.Default): ContinuousRoundedRectangle =
    ContinuousRectangleImpl(continuity)

@Immutable
private data class ContinuousRectangleImpl(
    override val continuity: Continuity = Continuity.Default
) : ContinuousRoundedRectangle(
    topStart = ZeroCornerSize,
    topEnd = ZeroCornerSize,
    bottomEnd = ZeroCornerSize,
    bottomStart = ZeroCornerSize,
    continuity = continuity
) {

    override fun toString(): String {
        return "ContinuousRectangle(continuity=$continuity)"
    }
}

internal val FullCornerSize = CornerSize(50)

/** A capsule shape with continuous corners. */
@Stable
public val ContinuousCapsule: ContinuousRoundedRectangle = ContinuousCapsule()

/** Creates a capsule shape with continuous corners. */
@Suppress("FunctionName")
@Stable
public fun ContinuousCapsule(continuity: Continuity = Continuity.Default): ContinuousRoundedRectangle =
    ContinuousCapsuleImpl(continuity)

@Immutable
private data class ContinuousCapsuleImpl(
    override val continuity: Continuity = Continuity.Default
) : ContinuousRoundedRectangle(
    topStart = FullCornerSize,
    topEnd = FullCornerSize,
    bottomEnd = FullCornerSize,
    bottomStart = FullCornerSize,
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
        return "ContinuousCapsule(continuity=$continuity)"
    }
}

/** Creates a shape with the same [corner] on all four corners. */
@Stable
public fun ContinuousRoundedRectangle(
    corner: CornerSize,
    continuity: Continuity = Continuity.Default
): ContinuousRoundedRectangle =
    ContinuousRoundedRectangle(
        topStart = corner,
        topEnd = corner,
        bottomEnd = corner,
        bottomStart = corner,
        continuity = continuity
    )

/** Creates a shape with the same corner of [size] on all four corners. */
@Stable
public fun ContinuousRoundedRectangle(
    size: Dp,
    continuity: Continuity = Continuity.Default
): ContinuousRoundedRectangle =
    ContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

/** Creates a shape with the same corner of [size] pixels on all four corners. */
@Stable
public fun ContinuousRoundedRectangle(
    @FloatRange(from = 0.0) size: Float,
    continuity: Continuity = Continuity.Default
): ContinuousRoundedRectangle =
    ContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

/** Creates a shape with the same corner of [percent] on all four corners. */
@Stable
public fun ContinuousRoundedRectangle(
    @IntRange(from = 0, to = 100) percent: Int,
    continuity: Continuity = Continuity.Default
): ContinuousRoundedRectangle =
    ContinuousRoundedRectangle(
        corner = CornerSize(percent),
        continuity = continuity
    )

/** Creates a shape with individual corners given in [Dp]. */
@Stable
public fun ContinuousRoundedRectangle(
    topStart: Dp = 0f.dp,
    topEnd: Dp = 0f.dp,
    bottomEnd: Dp = 0f.dp,
    bottomStart: Dp = 0f.dp,
    continuity: Continuity = Continuity.Default
): ContinuousRoundedRectangle =
    ContinuousRoundedRectangle(
        topStart = CornerSize(topStart),
        topEnd = CornerSize(topEnd),
        bottomEnd = CornerSize(bottomEnd),
        bottomStart = CornerSize(bottomStart),
        continuity = continuity
    )

/** Creates a shape with individual corners given in pixels. */
@Stable
public fun ContinuousRoundedRectangle(
    @FloatRange(from = 0.0) topStart: Float = 0f,
    @FloatRange(from = 0.0) topEnd: Float = 0f,
    @FloatRange(from = 0.0) bottomEnd: Float = 0f,
    @FloatRange(from = 0.0) bottomStart: Float = 0f,
    continuity: Continuity = Continuity.Default
): ContinuousRoundedRectangle =
    ContinuousRoundedRectangle(
        topStart = CornerSize(topStart),
        topEnd = CornerSize(topEnd),
        bottomEnd = CornerSize(bottomEnd),
        bottomStart = CornerSize(bottomStart),
        continuity = continuity
    )

/** Creates a shape with individual corners given in percents. */
@Stable
public fun ContinuousRoundedRectangle(
    @IntRange(from = 0, to = 100) topStartPercent: Int = 0,
    @IntRange(from = 0, to = 100) topEndPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomEndPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomStartPercent: Int = 0,
    continuity: Continuity = Continuity.Default
): ContinuousRoundedRectangle =
    ContinuousRoundedRectangle(
        topStart = CornerSize(topStartPercent),
        topEnd = CornerSize(topEndPercent),
        bottomEnd = CornerSize(bottomEndPercent),
        bottomStart = CornerSize(bottomStartPercent),
        continuity = continuity
    )
