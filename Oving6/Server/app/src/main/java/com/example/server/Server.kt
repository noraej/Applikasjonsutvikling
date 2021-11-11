package com.example.server

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class Server : ViewModel() {

    private val PORT: Int = 12345
    private var connectedSockets = mutableStateListOf<Socket>()

    var outgoingMessages = mutableStateListOf<String>()
        private set

    private var ui: String? = ""
        set(str) {
            if (str != null) {
                MainScope().launch {
                    outgoingMessages.add(str)
                }
            }
            field = str
        }

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ui = "Starter Tjener ..."
                ServerSocket(PORT).use { serverSocket: ServerSocket ->

                    ui = "ServerSocket opprettet, venter på at en klient kobler seg til...."
                    while (true) {
                        val clientSocket = serverSocket.accept()
                        ui = "En Klient koblet seg til:\n$clientSocket"

                        newClientHandeler(clientSocket)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                ui = e.message
            }
        }
    }

    private suspend fun messagesFromClientListener(socket: Socket) = coroutineScope {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val message = reader.readLine()
                if (message == null) {
                    ui = "Klient koblet fra"
                    connectedSockets.remove(socket)
                    socket.close()
                    break
                } else {
                    connectedSockets.filter { it !== socket }.forEach { sendToClient(it, message) }
                    ui = "Klienten sier:\n$message"
                }
            }
        }
    }

    private suspend fun sendToClient(socket: Socket, message: String) = coroutineScope {
        CoroutineScope(Dispatchers.IO).launch {
            val writer = PrintWriter(socket.getOutputStream(), true)
            writer.println(message)
            ui = "Sendte følgende til klienten:\n$message"
        }
    }

    private suspend fun newClientHandeler(clientSocket: Socket) = coroutineScope {
        CoroutineScope(Dispatchers.IO).launch {
            connectedSockets.add(clientSocket)
            ui = "En Klient koblet seg til:\n$clientSocket"
            sendToClient(clientSocket, "Velkommen Klient!")
            messagesFromClientListener(clientSocket)
        }
    }
}