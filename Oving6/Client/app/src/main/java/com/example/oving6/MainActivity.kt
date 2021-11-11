package com.example.oving6

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import java.net.Socket
import kotlin.reflect.KFunction2

class MainActivity : ComponentActivity() {
    private val client by viewModels<Client>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        client.start()
        setContent {
                // A surface container using the 'background' color from the theme
                Surface(shape = MaterialTheme.shapes.medium) {
                    Scaffold {
                        Column {
                            Row {
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .fillMaxWidth()
                                        .padding(6.dp)
                                ) {
                                    item {
                                        Text(
                                            "Meldinger sendt:",
                                            style = MaterialTheme.typography.subtitle2,
                                        )
                                    }
                                    items(client.outgoingMessages) { message ->
                                        Surface(shape = MaterialTheme.shapes.medium,
                                            elevation = 2.dp,
                                            color = colorResource(R.color.design_default_color_primary_dark),
                                            modifier = Modifier.padding(all = 8.dp),) {
                                            Text(
                                                text = message,
                                                modifier = Modifier.padding(6.dp).fillMaxWidth()
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .fillMaxWidth()
                                        .padding(6.dp)
                                ) {
                                    item {
                                        Text(
                                            "Meldinger mottatt:",
                                            style = MaterialTheme.typography.subtitle2,
                                        )
                                    }
                                    items(client.incomingMessages) { message ->
                                        Surface(shape = MaterialTheme.shapes.medium,
                                            elevation = 2.dp,
                                            color =  Color.LightGray,
                                            modifier = Modifier.padding(all = 8.dp),) {
                                            Text(
                                                text = message,
                                                modifier = Modifier.padding(6.dp).fillMaxWidth()
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }
                            if (client.server !== null) {
                                SendMessageView(client::sendToServer)
                            }
                        }
                    }
                }
            }
        }
    }


@Composable
fun SendMessageView(sendToServer: suspend (String) -> Unit) {
    val messageState = remember { mutableStateOf(TextFieldValue()) }
    fun send() = runBlocking {
        sendToServer(messageState.value.text)
    }
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Send en melding",
            modifier = Modifier.padding(8.dp)
            )
        Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
            TextField(
                value = messageState.value,
                onValueChange = { messageState.value = it },
                label = { Text("Melding") },
                modifier = Modifier
                    .padding(6.dp)
                    .weight(3f)
            )
            Button(onClick = { send() },
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
            ) {
                Text("Send")
            }
        }

    }
}