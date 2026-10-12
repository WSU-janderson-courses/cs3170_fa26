package com.example.rgbplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.rgbplayground.ui.theme.RGBPlaygroundTheme

data class ColorChoice(
    val name: String,
    val color: Color,
) {
    // TODO add methods `withString` and `withColorChannel`
    fun withString(newName: String): ColorChoice {
        return copy(
            name = newName,
            color = colorFromString(newName),
        )
    }
}

fun colorFromString(colorName: String): Color {
    return when (colorName.lowercase()) {
        "red" -> Color.Red
        "green" -> Color.Green
        "blue" -> Color.Blue
        else -> Color.Unspecified
    }
}

val colorOptions = listOf(
    "Red",
    "Green",
    "Blue"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RGBPlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RGBPlaygroundApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RGBPlaygroundApp(modifier: Modifier = Modifier) {
    var colorChoice by remember {
        mutableStateOf(
            ColorChoice(
                name = "Red",
                color = Color.Red
            )
        )
    }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(color = colorChoice.color)
    ) {
        Text(
            text = "RGB Playground",
            fontSize = 30.sp
        )

        ColorSelector(
            selectedColor = colorChoice.name,
            onColorSelected = { newColorString: String ->
                colorChoice = colorChoice.withString(newColorString)
            }
        )
    }
}

@Composable
fun ColorSelector(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO
    Column() {
        colorOptions.forEach { color: String ->
            ColorOption(
                name = color,
                selected = color == selectedColor,
                onClick = {
                    onColorSelected(color)
                },
            )
        }
    }
}

@Composable
fun ColorSliders(
    color: Color,
    updateColor: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column() {
        ColorSlider(
            name = "Red",
            value = color.red,
            onValueChange = { sliderValue -> updateColor("Red", sliderValue) }
        )
    }
}

@Composable
fun ColorOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(text = name)
    }
}

@Composable
fun ColorSlider(
    name: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
    ){
        Text(text = name)

        Slider(
            value = value,
            onValueChange = onValueChange
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RGBPlaygroundAppPreview() {
    RGBPlaygroundTheme {
        RGBPlaygroundApp(modifier = Modifier
            .fillMaxSize()
            .clipToBounds())
    }
}