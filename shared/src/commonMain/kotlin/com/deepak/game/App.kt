package com.deepak.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import clappybee.shared.generated.resources.Res
import clappybee.shared.generated.resources.background
import clappybee.shared.generated.resources.bee_sprite
import clappybee.shared.generated.resources.moving_background
import com.deepak.game.domain.Game
import com.deepak.game.domain.GameStatus
import com.deepak.game.ui.orange
import com.deepak.game.util.ChewyFontFamily
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import compose.icons.feathericons.RefreshCw
import org.jetbrains.compose.resources.painterResource

/**
 * Main entry point for the Clappy Bee game UI.
 *
 * This composable renders the complete game screen including:
 *
 * - Static and animated background layers
 * - Bee sprite animation
 * - Tap interaction for jumping
 * - Game score UI
 * - Start overlay
 * - Game over overlay with restart action
 *
 * The game state is maintained via [Game], which controls:
 * - Bee position and velocity
 * - Game progress updates
 * - Collision handling
 * - Current [GameStatus]
 *
 * UI behavior:
 * - Tapping anywhere on the canvas while the game is running makes the bee jump
 * - Background continuously scrolls horizontally while the game is active
 * - Bee rotation animates based on vertical velocity
 * - Sprite animation starts on game start and stops on game over
 *
 * Lifecycle:
 * - Sprite resources are cleaned up automatically when the composable leaves composition
 * - Game progress is updated frame-by-frame using [withFrameMillis]
 *
 * Game states:
 * - [GameStatus.Idle] → shows Start button
 * - [GameStatus.Started] → runs gameplay loop
 * - [GameStatus.Over] → shows Game Over screen and Restart button
 */
const val BEE_FRAME_SIZE = 80

@Preview
@Composable
fun App() {
    MaterialTheme {
        var screenWidth by remember { mutableStateOf(0) }
        var screenHeight by remember { mutableStateOf(0) }
        var game by remember { mutableStateOf(Game()) }

        val spriteState = rememberSpriteState(
            totalFrames = 9,
            framesPerRow = 3
        )

        val spriteSpec = remember {
            SpriteSpec(
                screenWidth = screenWidth.toFloat(),
                default = SpriteSheet(
                    frameWidth = BEE_FRAME_SIZE,
                    frameHeight = BEE_FRAME_SIZE,
                    image = Res.drawable.bee_sprite
                )
            )
        }

        val sheetImage = spriteSpec.imageBitmap
        val currentFrame by spriteState.currentFrame.collectAsStateWithLifecycle()
        val animatedAngle by animateFloatAsState(
            targetValue = when {
                (game.beeVelocity > game.beeMaxVelocity / 1.1) -> 30f
                else -> 0f
            }
        )

        DisposableEffect(Unit) {
            onDispose {
                spriteState.stop()
                spriteState.cleanup()
            }
        }

        LaunchedEffect(game.status) {
            while (game.status == GameStatus.Started) {
                withFrameMillis {
                    game.updateGameProgress()
                }
            }
            if (game.status == GameStatus.Over) {
                spriteState.stop()
            }
        }

        val backgroundOffsetX = remember { Animatable(0f) }
        var imageWidth by remember { mutableStateOf(0) }

        LaunchedEffect(game.status){
            while (game.status == GameStatus.Started){
                backgroundOffsetX.animateTo(
                    targetValue = - imageWidth.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 4000,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.background),
                contentDescription = "Background",
                contentScale = ContentScale.FillHeight
            )
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        imageWidth = it.width
                    }
                    .offset {
                        IntOffset(
                            x = backgroundOffsetX.value.toInt(),
                            y = 0
                        )
                    },
                painter = painterResource(Res.drawable.moving_background),
                contentDescription = "Background",
                contentScale = ContentScale.FillHeight
            )
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = backgroundOffsetX.value.toInt() + imageWidth,
                            y = 0
                        )
                    },
                painter = painterResource(Res.drawable.moving_background),
                contentDescription = "Background",
                contentScale = ContentScale.FillHeight
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    val size = it.size
                    if (screenWidth != size.width || screenHeight != size.height) {
                        screenWidth = size.width
                        screenHeight = size.height
                        game = game.copy(
                            screenWidth = size.width,
                            screenHeight = size.height
                        )
                    }
                }.clickable {
                    if (game.status == GameStatus.Started) {
                        game.jump()
                    }
                }
        ) {
            rotate(
                degrees = animatedAngle,
                pivot = Offset(
                    x = game.bee.x - game.beeRadius,
                    y = game.bee.y - game.beeRadius
                )
            ) {
                drawSpriteView(
                    spriteState = spriteState,
                    spriteSpec = spriteSpec,
                    currentFrame = currentFrame,
                    image = sheetImage,
                    offset = IntOffset(
                        x = (game.bee.x - game.beeRadius).toInt(),
                        y = (game.bee.y - game.beeRadius).toInt()
                    )
                )
            }

            game.pipePairs.forEach { pipePair ->
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(
                        x = pipePair.x - game.pipeWidth / 2,
                        y = 0f
                    ),
                    size = Size(game.pipeWidth, pipePair.topHeight)
                )

                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(
                        x = pipePair.x - game.pipeWidth / 2,
                        y = pipePair.y + game.pipeGapSize / 2
                    ),
                    size = Size(game.pipeWidth, pipePair.bottomHeight)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BEST: 0",
                fontWeight = FontWeight.Bold,
                fontFamily = ChewyFontFamily(),
                fontSize = MaterialTheme.typography.displaySmall.fontSize
            )

            Text(
                text = "0",
                fontWeight = FontWeight.Bold,
                fontFamily = ChewyFontFamily(),
                fontSize = MaterialTheme.typography.displaySmall.fontSize
            )
        }

        if (game.status == GameStatus.Idle) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(size = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = orange
                    ),
                    onClick = {
                        game.start()
                        spriteState.start()
                    }
                ) {
                    Icon(
                        imageVector = FeatherIcons.Play,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "START",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = ChewyFontFamily()
                    )
                }
            }
        }

        if (game.status == GameStatus.Over) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Over!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = ChewyFontFamily(),
                    fontSize = MaterialTheme.typography.displayMedium.fontSize
                )
                Text(
                    text = "SCORE: 0",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontFamily = ChewyFontFamily()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(size = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = orange
                    ),
                    onClick = {
                        game.restart()
                        spriteState.start()
                    }
                ) {
                    Icon(
                        imageVector = FeatherIcons.RefreshCw,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "RESTART",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = ChewyFontFamily()
                    )
                }
            }
        }
    }
}