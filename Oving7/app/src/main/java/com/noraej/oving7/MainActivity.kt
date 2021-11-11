package com.noraej.oving7

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noraej.oving7.managers.FileManager
import com.noraej.oving7.managers.MyPreferenceManager
import com.noraej.oving7.service.Database
import com.noraej.oving7.ui.theme.Oving7Theme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Database(this)
        val movies = FileManager(this).readFileFromResFolder(R.raw.movies).split("*\n")

        movies.forEach {
            val lines = it.lines()
            db.insert(lines[0], lines[1], lines[2].split("/"))
            Log.i(lines[0], lines[1] + " " + lines[2])
        }
        setContent {
            Navigation(db)
        }
    }
}

@Composable
private fun Navigation(db: Database) {
    val navController = rememberNavController()

    Oving7Theme {
        Surface {
            NavHost(navController, startDestination = "movies") {
                composable(route = "movies") {
                    MainScreen(navController, db)
                }
                composable(route = "settings") {
                    SettingsScreen(navController)
                }
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MainScreen(navController: NavController, db: Database) {
    val myPreferenceManager = MyPreferenceManager(LocalContext.current)
    val color = myPreferenceManager.updateBackgroundColor()
    val defaultText = {
        val res = StringBuffer("")
        for (s in db.allMovies) res.append("$s\n")
        res.toString()
    }
    val (text, setText) = remember { mutableStateOf(defaultText()) }
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var directorNameState = remember { mutableStateOf(TextFieldValue()) }

    fun showResults(list: ArrayList<String>) {
        val res = StringBuffer("")
        for (s in list) {
            res.append("$s\n")
        }
        setText(res.toString())
    }


    Scaffold(
        backgroundColor = Color(color),
        topBar = {
            TopAppBar(
                elevation = 4.dp,
                title = { Text("Movies") },
                backgroundColor = MaterialTheme.colors.primarySurface,
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Rounded.Settings, null)
                    }
                })
        },
        floatingActionButton = {
            if (!state.isVisible) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Rounded.MoreVert, "") },
                    text = { Text("Filters") },
                    onClick = { scope.launch { state.show() } },
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                )
            }
        }
    ) {
        ModalBottomSheetLayout(
            sheetState = state,
            sheetContent = {
                LazyColumn {
                    item {
                        ListItem(
                            Modifier.clickable(onClick = {
                                showResults(db.allMovies)
                                scope.launch { state.hide() }
                            }),
                            text = { Text("All movies") }
                        )
                    }
                    item {
                        ListItem(
                            Modifier.clickable(onClick = {
                                showResults(db.allDirectors)
                                scope.launch { state.hide() }
                            }),
                            text = { Text(text = "All directors") }
                        )
                    }
                    item {
                        ListItem(
                            Modifier.clickable(onClick = {
                                showResults(db.allActors)
                                scope.launch { state.hide() }
                            }),
                            text = { Text("All actors") }
                        )
                    }
                    item {
                        ListItem(
                            Modifier.clickable(onClick = {
                                showResults(db.getAllActorsGroupByMovie)
                                scope.launch { state.hide() }
                            }),
                            text = { Text("All movies and actors") }
                        )
                    }
                    item {
                        ListItem {
                            Column {
                                Text("Find all movies by")
                                Row {
                                    TextField(
                                        value = directorNameState.value,
                                        onValueChange = { directorNameState.value = it },
                                        label = { Text("Director") },
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(2f)
                                    )
                                    Button(
                                        onClick = {
                                            showResults(db.getMoviesByDirector(directorNameState.value.text))
                                            scope.launch { state.hide() }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Find")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))
                Text(text)
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    val myPreferenceManager = MyPreferenceManager(LocalContext.current)
    val defaultColor = myPreferenceManager.updateBackgroundColor()
    val (color, setColor) = remember {
        mutableStateOf(defaultColor)
    }
    val key = LocalContext.current.resources.getString(R.string.background_color)
    val backgroundColorValues =
        LocalContext.current.resources.getStringArray(R.array.background_color_values)

    fun getOptionFromPreference() =
        when (myPreferenceManager.getString(key, backgroundColorValues[0])) {
            backgroundColorValues[0] -> "White"
            backgroundColorValues[1] -> "Blue"
            backgroundColorValues[2] -> "Red"
            else -> "White"
        }

    val radioOptions = listOf("White", "Blue", "Red")
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(getOptionFromPreference())
    }
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        if (k == key) {
            onOptionSelected(getOptionFromPreference())
        }
    }
    myPreferenceManager.registerListener(listener)

    fun handleSelectOption(option: String) {
        onOptionSelected(option)
        when (option) {
            "White" -> myPreferenceManager.editPreference(backgroundColorValues[0])
            "Blue" -> myPreferenceManager.editPreference(backgroundColorValues[1])
            "Red" -> myPreferenceManager.editPreference(backgroundColorValues[2])
            else -> myPreferenceManager.editPreference(backgroundColorValues[0])
        }
        setColor(myPreferenceManager.updateBackgroundColor())
    }
    Scaffold(
        backgroundColor = Color(color),
        topBar = {
            TopAppBar(
                elevation = 4.dp,
                title = { Text("Settings") },
                backgroundColor = MaterialTheme.colors.primarySurface,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                })
        }
    ) {
        Column {
            Text("Select application theme")
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { handleSelectOption(text) }
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = { handleSelectOption(text) }
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}


