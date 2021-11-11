package com.example.server

import android.os.Bundle
import androidx.activity.viewModels
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val serverViewModel by viewModels<Server>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        serverViewModel.start()

        setContent {
            Surface(color = MaterialTheme.colors.background) {
                Scaffold {
                    LazyColumn {
                        item {
                            Text("Meldinger:", fontWeight = FontWeight.Bold)
                        }

                        items(serverViewModel.outgoingMessages) { message ->
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                elevation = 2.dp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(all = 6.dp),
                            ) {
                                Text(
                                    text = message,
                                    Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
