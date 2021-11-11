package com.example.oving6

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client : ViewModel(){

    private val SERVER_IP:String = "10.0.2.2"
    private val SERVER_PORT: Int = 12345

    var server: Socket? = null
        private set

    var incomingMessages = mutableStateListOf<String>()
        private set

    var outgoingMessages = mutableStateListOf<String>()
        private set

    fun start(){
        CoroutineScope(Dispatchers.IO).launch {
            incomingMessages.add("Kobler til tjener...")
            try {
                Socket(SERVER_IP, SERVER_PORT).use { socket: Socket ->
                    server = Socket(SERVER_IP, SERVER_PORT)
                    incomingMessages.add("Koblet til tjener:\n$socket")

                    readFromServer(socket)

                    sendToServer("Heisann Tjener! Hyggelig å hilse på deg")

                }
            } catch (e: IOException) {
                e.printStackTrace()
                e.message?.let {
                    incomingMessages.add(it)
                }
            }
        }
    }

    private fun readFromServer(socket: Socket) {
        CoroutineScope(Dispatchers.IO).launch {
            server?.let {
                while (true){
                    val reader = BufferedReader(InputStreamReader(it.getInputStream()))
                    val message = reader.readLine()
                    if(message !== null){
                        incomingMessages.add(message)
                    } else {
                        it.close()
                        break
                    }
                }
            }
        }
    }

    fun sendToServer(message: String) {
        Log.i("OutgoingMessage", message)
        CoroutineScope(Dispatchers.IO).launch {
            server?.let {
                val writer = PrintWriter(it.getOutputStream(), true)
                writer.println(message)
                outgoingMessages.add(message)
            }
        }
    }
}
