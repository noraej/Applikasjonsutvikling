package com.example.oving5

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.oving5.ui.theme.Oving5Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty0

enum class AppView {
    START,
    GAME,
}

enum class GameState {
    BEFORE_FIRST_GUESS,
    BEFORE_SECOND_GUESS,
    BEFORE_THIRD_GUESS,
    CORRECT_GUESS,
    NO_MORE_GUESSES,
}

class GameViewModel() : ViewModel() {
    private val BASE_URL = "https://bigdata.idi.ntnu.no/mobil/tallspill.jsp"
    private val network: HttpWrapper = HttpWrapper(BASE_URL)

    var feedback: String? = null
    var viewStart by mutableStateOf(AppView.START)
    var gameState by mutableStateOf(GameState.BEFORE_FIRST_GUESS)
        private set

    fun makeFeedback(msg: String) {
        feedback = msg
    }

    fun openView(view: AppView) {
        if (view == AppView.GAME) {
            gameState = GameState.BEFORE_FIRST_GUESS
        }
        viewStart = view
        feedback = null
    }

    fun initGame(name: String, cardNumber: String) {
        performRequest(network, mapOf("navn" to name, "kortnummer" to cardNumber)) {
            val initSucsessCheck = "Oppgi et"
            if (it.contains(initSucsessCheck, ignoreCase = true)) {
                openView(AppView.GAME)
            }
            makeFeedback(it)
        }
    }

    fun performRequest(
        network: HttpWrapper,
        parameterList: Map<String, String>,
        onResult: (String) -> Unit
    ) {
        CoroutineScope(IO).launch {
            val response: String = try {
                network.get(parameterList)
            } catch (exception: Exception) {
                Log.e("performeRequest()", exception.message!!)
                exception.printStackTrace()
                exception.toString()
            }
            MainScope().launch {
                Log.i("Response", response)
                onResult(response)
            }
        }
    }

    fun guessNumber(number: String) {
        Log.i("Guess", "Making a guess and sending reguest")
        performRequest(network, mapOf("tall" to number)) {
            Log.i("Guess", "Response to Guess request: $it")
            val wonSucMsg = "du har vunnet 100 kr"
            makeFeedback(it)

            if (it.contains(wonSucMsg, ignoreCase = true)) {
                gameState = GameState.CORRECT_GUESS
                Log.i("Guess", "Correct guess")
            } else {
                when (gameState) {
                    GameState.BEFORE_FIRST_GUESS -> gameState = GameState.BEFORE_SECOND_GUESS
                    GameState.BEFORE_SECOND_GUESS -> gameState = GameState.BEFORE_THIRD_GUESS
                    GameState.BEFORE_THIRD_GUESS -> gameState = GameState.NO_MORE_GUESSES
                    else -> Log.e("Gamestate", "Gamestate unknown")
                }
            }
        }
    }
}


class MainActivity : ComponentActivity() {
    private val gameViewModel by viewModels<GameViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Oving5Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (gameViewModel.viewStart == AppView.START) {
                                StartUpView(gameViewModel::initGame)
                            } else {
                                GameView(
                                    gameViewModel::guessNumber,
                                    gameViewModel.gameState,
                                    gameViewModel::openView
                                )
                            }
                            if (gameViewModel.feedback != null) {
                                Text(gameViewModel.feedback!!)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StartUpView(initGame: (String, String) -> Unit) {

        var name by remember { mutableStateOf("") }
        var cardNumber by remember { mutableStateOf("") }

        fun start(name: String, cardNumber: String) {
            initGame(name, cardNumber)
            Log.i("INFO", "Name: " + name + "\nCard number: " + cardNumber)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Welcome. Please enter name and cardnumbah",
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.padding(6.dp)

            )
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = { Text("Card number") },
                modifier = Modifier.padding(6.dp)

            )
            Button(
                onClick = { start(name, cardNumber) },
                modifier = Modifier.padding(6.dp)
            ) {
                Text(text = "Go!")
            }
        }
    }

    @Composable
    fun GameView(
        guessNumber: (String) -> Unit,
        gameState: GameState,
        setViewOpen: (AppView) -> Unit,
    ) {
        var guess = remember { mutableStateOf(TextFieldValue()) }

        when(gameState) {
            GameState.BEFORE_FIRST_GUESS, GameState.BEFORE_SECOND_GUESS, GameState.BEFORE_THIRD_GUESS -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Make your guess",
                        modifier = Modifier.padding(6.dp)
                    )
                    TextField(
                        value = guess.value,
                        onValueChange = { guess.value = it },
                        modifier = Modifier.padding(6.dp)
                    )
                    Button(
                        onClick = { guessNumber(guess.value.text) },
                        modifier = Modifier.padding(6.dp)

                    ) {
                        Text(text = "Check")
                    }
                }
            }
            GameState.CORRECT_GUESS ->{
                Text(
                    text = "Congratulations!",
                    modifier = Modifier.padding(6.dp),
                    color = Color.Magenta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp

                )
                Text(
                    text = "You made the right guess",
                    fontSize = 10.sp,
                    modifier = Modifier.padding(6.dp)
                )
                Button(onClick = { setViewOpen(AppView.START) }) {
                    Text("Play again!")
                }
            }
            GameState.NO_MORE_GUESSES -> {
                Text(
                    text = "You lost",
                    modifier = Modifier.padding(6.dp),
                    color = Color.Magenta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
                Button(onClick = { setViewOpen(AppView.START)},
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Try again!")
                }
            }
        }
    }
}
