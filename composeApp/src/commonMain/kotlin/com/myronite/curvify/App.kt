package com.myronite.curvify

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.myronite.curvify.continuities.G0Continuity
import com.myronite.curvify.continuities.G1Continuity
import com.myronite.curvify.continuities.G2Continuity
import com.myronite.curvify.continuities.G2ContinuityProfile
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private val G0 = G0Continuity
private val G1 = G1Continuity
private val G2 = G2Continuity()

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    MaterialTheme {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val scope = rememberCoroutineScope()
        val tabs = listOf("Compare", "Shapes", "Playground")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Curvify") },
                    subtitle = { Text("Continuous rounded corners") },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(title) }
                        )
                    }
                }
                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> ComparePage()
                        1 -> ShapesPage()
                        else -> PlaygroundPage()
                    }
                }
            }
        }
    }
}

// ---------- Compare ----------

@Composable
private fun ComparePage() {
    var corner by remember { mutableFloatStateOf(40f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        TitleContainer {
            Text(buildAnnotatedString {
                append("Corner Size")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append("  (${corner.roundToInt()} dp)")
                }
            })
        }
        Slider(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = corner,
            onValueChange = { corner = it },
            valueRange = 8f..60f,
        )

        // Side by side comparison
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ShapeCard("G0", corner.dp, G0, Modifier.weight(1f))
            ShapeCard("G1", corner.dp, G1, Modifier.weight(1f))
            ShapeCard("G2", corner.dp, G2, Modifier.weight(1f))
        }

        // Magnified top-left corners, where the continuity difference lives
        TitleContainer {
            Text(buildAnnotatedString {
                append("Corner Detail")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append("  (x3 zoom)")
                }
            })
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CornerZoomCard("G1", corner.dp, G1, Modifier.weight(1f))
            CornerZoomCard("G2", corner.dp, G2, Modifier.weight(1f))
        }
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            text = "Watch the outline enter the corner: G1 bends abruptly where the arc meets the edge, " +
                    "while G2 eases the curvature in smoothly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ShapeCard(
    label: String,
    corner: Dp,
    continuity: Continuity,
    modifier: Modifier = Modifier
) {
    val shape = ContinuousRoundedRectangle(corner, continuity)
    val outlineColor = MaterialTheme.colorScheme.outline

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, shape)
                .background(
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    shape
                )
                .shapeStroke(outlineColor, shape)
        )
        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * Shows the top-left corner of the shape magnified by [zoom], centered on the crosshair,
 * making the curvature transition at the corner visible to the naked eye.
 */
@Composable
private fun CornerZoomCard(
    label: String,
    corner: Dp,
    continuity: Continuity,
    modifier: Modifier = Modifier,
    zoom: Float = 3f,
) {
    val viewport = 120.dp
    val bigCorner = corner * zoom
    val boxSize = if (viewport * 2 > bigCorner * 2) viewport * 2 else bigCorner * 2
    val shape = ContinuousRoundedRectangle(bigCorner, continuity)
    val outlineColor = MaterialTheme.colorScheme.outline
    val crosshairColor = MaterialTheme.colorScheme.outlineVariant

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clipToBounds()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Canvas(Modifier.size(viewport)) {
                val c = this.center
                val arm = 7.dp.toPx()
                drawLine(crosshairColor, Offset(c.x - arm, c.y), Offset(c.x + arm, c.y), 1.5f)
                drawLine(crosshairColor, Offset(c.x, c.y - arm), Offset(c.x, c.y + arm), 1.5f)
            }
            Box(
                modifier = Modifier
                    .size(boxSize)
                    .offset(x = viewport / 2 - bigCorner, y = viewport / 2 - bigCorner)
                    .background(MaterialTheme.colorScheme.secondaryContainer, shape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
                        ),
                        shape
                    )
                    .shapeStroke(outlineColor, shape)
            )
        }
        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ---------- Shapes ----------

@Composable
private fun ShapesPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        TitleContainer { Text("Capsule") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShinyBox(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = ContinuousCapsule(G2),
            )
            ShinyBox(
                modifier = Modifier
                    .width(52.dp)
                    .height(104.dp),
                shape = ContinuousCapsule(G2),
            )
        }

        TitleContainer { Text("Concentric Inset") }
        val outer = ContinuousRoundedRectangle(32.dp, G2)
        val inner = outer.concentricInset(12.dp)
        ShinyBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(150.dp),
            shape = outer,
        ) {
            ShinyBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                shape = inner,
                highlight = false,
            )
        }

        TitleContainer { Text("Asymmetric Corners") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                ShinyBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    shape = ContinuousRoundedRectangle(
                        topStart = 36.dp,
                        bottomEnd = 36.dp,
                        continuity = G2,
                    ),
                )
                Caption("topStart + bottomEnd")
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                ShinyBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    shape = AbsoluteContinuousRoundedRectangle(
                        topLeft = 36.dp,
                        bottomRight = 36.dp,
                        continuity = G2,
                    ),
                )
                Caption("absolute topLeft + bottomRight")
            }
        }

        TitleContainer { Text("Shape Morph") }
        val transition = rememberInfiniteTransition(label = "morph")
        val fraction by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "fraction",
        )
        val morphingShape = lerp(
            start = ContinuousRoundedRectangle(18.dp, G1),
            stop = ContinuousCapsule(G2),
            fraction = fraction,
        )
        ShinyBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(120.dp),
            shape = morphingShape,
        )
    }
}

@Composable
private fun ShinyBox(
    modifier: Modifier = Modifier,
    shape: ContinuousRoundedRectangle,
    highlight: Boolean = true,
    content: (@Composable () -> Unit)? = null,
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, shape)
            .background(
                if (highlight) {
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
                    )
                } else {
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                },
                shape
            )
            .shapeStroke(outlineColor, shape)
    ) {
        content?.invoke()
    }
}

/**
 * Strokes the true outline of [shape]. Unlike [androidx.compose.foundation.border], which insets
 * generic Bezier outlines approximately and renders uneven width on the corners, this keeps the
 * stroke width uniform and the corner curvature intact.
 */
private fun Modifier.shapeStroke(
    color: Color,
    shape: ContinuousRoundedRectangle,
    width: Dp = 1.dp,
): Modifier = drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    drawOutline(outline, color, style = Stroke(width.toPx()))
}

@Composable
private fun Caption(text: String) {
    Text(
        modifier = Modifier.padding(top = 6.dp),
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

// ---------- Playground ----------

private const val DEFAULT_EXTENDED_FRACTION = 0.5286651f
private const val DEFAULT_ARC_FRACTION = 5f / 9f
private const val DEFAULT_CURVATURE_SCALE = 1.0732051f

@Composable
private fun PlaygroundPage() {
    var corner by remember { mutableFloatStateOf(40f) }
    var extendedFraction by remember { mutableFloatStateOf(DEFAULT_EXTENDED_FRACTION) }
    var arcFraction by remember { mutableFloatStateOf(DEFAULT_ARC_FRACTION) }
    var bezierScale by remember { mutableFloatStateOf(DEFAULT_CURVATURE_SCALE) }
    var arcScale by remember { mutableFloatStateOf(DEFAULT_CURVATURE_SCALE) }

    val profile = G2ContinuityProfile(
        extendedFraction = extendedFraction.toDouble(),
        arcFraction = arcFraction.toDouble(),
        bezierCurvatureScale = bezierScale.toDouble(),
        arcCurvatureScale = arcScale.toDouble(),
    )
    val continuity = G2Continuity(profile = profile)
    val isExactG2 = abs(bezierScale - arcScale) < 0.01f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        ShinyBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(180.dp),
            shape = ContinuousRoundedRectangle(corner.dp, continuity),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isExactG2) "Exact G2 continuity" else "G1 only (scales differ)",
                style = MaterialTheme.typography.labelMedium,
                color = if (isExactG2) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                corner = 40f
                extendedFraction = DEFAULT_EXTENDED_FRACTION
                arcFraction = DEFAULT_ARC_FRACTION
                bezierScale = DEFAULT_CURVATURE_SCALE
                arcScale = DEFAULT_CURVATURE_SCALE
            }) { Text("Reset") }
        }

        LabeledSlider("Corner Size", corner, 8f..60f, "dp") { corner = it }
        LabeledSlider("Extended Fraction", extendedFraction, 0f..1f) { extendedFraction = it }
        LabeledSlider("Arc Fraction", arcFraction, 0f..1f) { arcFraction = it }
        LabeledSlider("Bezier Curvature Scale", bezierScale, 0.5f..2f) { bezierScale = it }
        LabeledSlider("Arc Curvature Scale", arcScale, 0.5f..2f) { arcScale = it }

        // Real components with the live profile
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(shape = ContinuousCapsule(continuity), onClick = {}) { Text("Button") }
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueSuffix: String = "",
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
    ) {
        Text(buildAnnotatedString {
            append(label)
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                append("  (${value.decimals(if (valueSuffix.isEmpty()) 2 else 0)}$valueSuffix)")
            }
        })
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange)
    }
}

private fun Float.decimals(digits: Int): String {
    var factor = 1
    repeat(digits) { factor *= 10 }
    val scaled = (this * factor).roundToInt()
    return "${scaled / factor}.${(scaled % factor).toString().padStart(digits, '0')}"
}

@Composable
private fun TitleContainer(
    content: @Composable RowScope.() -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ProvideTextStyle(MaterialTheme.typography.titleMedium) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 14.dp),
                content = content
            )
        }
    }
}
