package com.example.happybirthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happybirthday.ui.theme.HappyBirthdayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> // innerPadding might be underlined in red, but we can ignore that for now
                    GreetingImage(
                        message = "Happy Birthday Paris",
                        from = "From James",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Composable for the app text
 */
@Composable
fun GreetingText(
    from: String,
    message: String,
    modifier: Modifier = Modifier,
) {

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.padding(8.dp)
    ) {

        Text(
                text = message,
                fontSize = 100.sp,
                lineHeight = 116.sp,
                textAlign = TextAlign.Center,
            )

            Text(
                text = from,
                fontSize = 36.sp,
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.End)
            )
        }
}

/**
 * Composable that draws the text over the image, and since they are in a Box we can control
 * their arrangement
 */
@Composable
fun GreetingImage(
    from: String,
    message: String,
    modifier: Modifier = Modifier
) {
    val imageId = R.drawable.paris
    val image = painterResource(id = imageId)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(color = Color.LightGray)
    )
    {
        Image(
            painter = image,
            contentDescription = "Birthday card background",
            contentScale = ContentScale.Crop,
            alpha = 0.4f, // image transparency, or how much image vs. background is shown
        )

        GreetingText(
            message = message,
            from = from,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Preview function
 *
 * We can call a Composable function, and it will show up in the preview pane
 */
@Preview(showBackground = true)
@Composable
fun BirthdayCardPreview() {
    HappyBirthdayTheme {
        GreetingImage(
            from = "From Paris",
            message = "Happy Birthday James",
        )
    }
}