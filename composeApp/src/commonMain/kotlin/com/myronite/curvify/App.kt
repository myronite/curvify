package com.myronite.curvify

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

import capsule_compose_multiplatform.composeapp.generated.resources.Res
import capsule_compose_multiplatform.composeapp.generated.resources.compose_multiplatform
import com.myronite.curvify.continuities.G0Continuity
import com.myronite.curvify.continuities.G1Continuity
import com.myronite.curvify.continuities.G2Continuity

private enum class ContinuityProfile {
    G0, G1, G2
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    val sliderState = rememberSliderState(valueRange = 0f..60f)
    var continuity: Continuity by remember { mutableStateOf(G0Continuity) }
    val shape = ContinuousRoundedRectangle(sliderState.value.dp, continuity = continuity)

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Capsule") },
                    subtitle = { Text("Compose Multiplatform") },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                Column {
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 2.dp)
                                .height(120.dp),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 4.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 4.dp
                            ),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(shape = shape, onClick = { }) { Text("Button") }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(start = 2.dp, end = 8.dp)
                                .height(120.dp),
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 16.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 16.dp
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Surface(
                                    modifier = Modifier
                                        .offset(60.dp, 40.dp)
                                        .fillMaxSize(),
                                    shape = shape.copy(
                                        topEnd = CornerSize(0.dp),
                                        bottomStart = CornerSize(0.dp),
                                        bottomEnd = CornerSize(0.dp)
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                ) { }
                            }
                        }
                    }
                }

                Column {
                    TitleContainer {
                        Text(buildAnnotatedString {
                            append("Corner Size")
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                append(" (${sliderState.value.dp})")
                            }
                        })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Slider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            state = sliderState
                        )
                    }
                }
                Column {
                    TitleContainer {
                        Text("Continuity")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { continuity = G0Continuity }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = continuity == G0Continuity,
                            onClick = { continuity = G0Continuity },
                        )
                        Text("G0 Continuity")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { continuity = G1Continuity }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = continuity == G1Continuity,
                            onClick = { continuity = G1Continuity },
                        )
                        Text("G1 Continuity")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { continuity = G2Continuity() }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = continuity == G2Continuity(),
                            onClick = { continuity = G2Continuity() },
                        )
                        Text("G2 Continuity")
                    }
                }
            }
        }
    }
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