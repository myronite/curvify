# Curvify

![Maven Central](https://img.shields.io/maven-central/v/io.github.myronite/curvify)

Based on the original project: [Capsule](https://github.com/Kyant0/Capsule)

Original author: [Kyant0](https://github.com/kyant0)

Curvify is a Compose Multiplatform library that creates G2 continuous rounded rectangles.

## Installation

In build.gradle.kts, add

```kotlin
implementation("io.github.myronite:curvify:<version>")
```

## Usages

Replace the `RoundedCornerShape` with `ContinuousRoundedRectangle` or `ContinuousCapsule`:

```kotlin
// create a basic rounded corner shape
ContinuousRoundedRectangle(16.dp)

// create a capsule shape
ContinuousCapsule
```

### Directional vs absolute corners

`ContinuousRoundedRectangle` resolves `topStart`/`topEnd` according to the layout direction (mirrored in RTL).
If you always want corners relative to the top-left, use the absolute variants:

```kotlin
// corners stay fixed regardless of layout direction
AbsoluteContinuousRoundedRectangle(topLeft = 16.dp, bottomRight = 16.dp)
AbsoluteContinuousCapsule
```

### Concentric shapes

Grow or shrink a shape while keeping the corner curves concentric, similar to the iOS squircle insets:

```kotlin
val outer = ContinuousRoundedRectangle(24.dp)
val inner = outer.concentricInset(8.dp)   // smaller, concentric corners
val bigger = outer.concentricOutset(4.dp) // larger, concentric corners
```

Also available on the `AbsoluteContinuousRoundedRectangle` family.

### Shape interpolation

Interpolate between two shapes (corners and continuity both animate):

```kotlin
val shape = lerp(startShape, stopShape, fraction)
```

### Export as SVG

Every shape geometry is built from pure `PathSegments`, which can be exported:

```kotlin
val segments = G2Continuity().createRoundedRectanglePathSegments(
    width = 200.0, height = 100.0,
    topLeft = 16.0, topRight = 16.0, bottomRight = 16.0, bottomLeft = 16.0
)
val svg = segments.toSvg(asDocument = true)
```

Custom continuity:

```kotlin
val g0 = G0Continuity // sharp corners
val g1 = G1Continuity // plain rounded corners, no corner smoothing
val g2 = G2Continuity(
    profile = G2ContinuityProfile.RoundedRectangle.copy(
        extendedFraction = 0.5,
        arcFraction = 0.5,
        bezierCurvatureScale = 1.1,
        arcCurvatureScale = 1.1
    ),
    capsuleProfile = G2ContinuityProfile.Capsule.copy(
        extendedFraction = 0.5,
        arcFraction = 0.25
    )
)

// create shapes with custom continuity
ContinuousRoundedRectangle(16.dp, continuity = g2)
ContinuousCapsule(continuity = g2)
```

The following parameters are supported by `G2ContinuityProfile`:

- **extended fraction:** the transition length between original corner and line, relative to the corner radius
- **arc fraction:** the ratio of the arc to the corner
- **Bezier curvature scale**: the multiplier of the end curvature of the Bezier curve
- **arc curvature scale**: the multiplier of the arc curvature

**Note:** It guarantees G1 continuity at least. Only if the Bezier curvature scale equals the arc curvature scale,
it will have exact G2 continuity.

## Tips

### Performance

Drawing cubic Bézier curves on Android performs poorly. However, the Curvify library uses a very efficient method to
calculate the control points, achieving optimal theoretical performance.

When the shape area is large (almost fullscreen) and the corner radius is constantly changing, performance may decrease.
Use `animatedShape.copy(continuity = G1Continuity)` to temporarily disable corner smoothing during the
animation.
