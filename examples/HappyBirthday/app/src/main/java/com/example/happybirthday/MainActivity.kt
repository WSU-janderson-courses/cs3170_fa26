package com.example.happybirthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.happybirthday.ui.theme.HappyBirthdayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> // innerPadding might be underlined in red, but we can ignore that for now

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
    Column()
    {
        Text(
            text = message,
            fontSize = 100.sp,
            lineHeight = 116.sp
        )

        Text(
            text = from,
            fontSize = 36.sp
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
        GreetingText(
            message = "Happy Birthday James!",
            from = "From Paris"
        )
    }
}