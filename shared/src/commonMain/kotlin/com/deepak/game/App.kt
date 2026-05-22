package com.deepak.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import clappybee.shared.generated.resources.Res
import clappybee.shared.generated.resources.background
import com.deepak.game.util.ChewyFontFamily
import org.jetbrains.compose.resources.painterResource

@Preview
@Composable
fun App() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.background),
                contentDescription = "Background",
                contentScale = ContentScale.Crop
            )

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
        }
    }
}