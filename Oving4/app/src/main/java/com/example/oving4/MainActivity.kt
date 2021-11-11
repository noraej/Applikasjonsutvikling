package com.example.oving4

import android.os.Bundle
import android.text.Layout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.oving4.ui.theme.Oving4Theme
import org.w3c.dom.NameList

//TODO: Display bilder
//TODO: Gjør så den endrer seg når skjermen endrer retning

data class ImageObject(val name: String, val description: String, val url: String)

class ImageViewModel : ViewModel() {

    var currentImageViewed by mutableStateOf(0)
        private set

    var imageList = mutableStateListOf(
        ImageObject("Islandshest", "En hest med to ekstra gangarter, tølt og skritt","https://www.rarebreeds.co.nz/icelandic1.jpg"),
        ImageObject(
            "Fjordhest",
            "Den eldste norske hesterasen",
            "https://cdn.pixabay.com/photo/2017/09/10/12/54/horse-2735475_960_720.jpg"
        ),
        ImageObject(
            "Dølahest",
            "Norske hesterasers svar på traktoren",
            "https://www.nordgen.org/wp-content/uploads/2021/02/fotokred-fr%C3%B8ydis-norsk-hestesenter-flip.jpg"
        )
    )
        private set

    fun setShownImage(index: Int) {
        currentImageViewed = index
    }

    fun setNextImage() {
        if (currentImageViewed < imageList.size -1) {
            setShownImage(currentImageViewed + 1)
        } else currentImageViewed = 0
    }

    fun setPrevImage() {
        if (currentImageViewed != 0) {
            setShownImage(currentImageViewed - 1)
        } else currentImageViewed = imageList.size - 1
    }
}

class MainActivity : ComponentActivity() {
    val imageModelView by viewModels<ImageViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Oving4Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                elevation = 4.dp,
                                title = { Text("Hesteraser") },
                                backgroundColor = MaterialTheme.colors.primaryVariant,
                                actions = {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                                        modifier = Modifier.padding(2.dp),
                                        onClick = { imageModelView.setPrevImage()}
                                        ) {
                                        Text("< Forrige")
                                    }
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                                        modifier = Modifier.padding(2.dp),
                                        onClick = { imageModelView.setNextImage() }
                                    ) {
                                        Text("Neste >")
                                    }
                                })
                        },
                    ) {
                        BoxWithConstraints() {
                            if (maxWidth < 400.dp) {
                                Column() {
                                        Row(
                                            modifier = Modifier
                                                .weight(1.0f)
                                                .fillMaxWidth()
                                        ) {
                                            NameListView(
                                                imageModelView.imageList,
                                                imageModelView::setShownImage
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .weight(2.0f)
                                                .fillMaxWidth()
                                        ) {
                                            ImageCarousel(
                                                imageModelView.imageList,
                                                imageModelView.currentImageViewed
                                            )
                                        }
                                    }
                            }
                            else {
                                        Row() {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1.0f)
                                                    .fillMaxWidth()
                                            ) {
                                                NameListView(
                                                    imageModelView.imageList,
                                                    imageModelView::setShownImage
                                                )
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .weight(1.0f)
                                                    .fillMaxWidth()
                                            ) {
                                                ImageCarousel(
                                                    imageModelView.imageList,
                                                    imageModelView.currentImageViewed
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

@Composable
fun NameListView(imageList: List<ImageObject>, setShownImage: (Int) -> Unit) {
    LazyColumn {
        items(imageList.size) { index ->
            Button(onClick = { setShownImage(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = MaterialTheme.shapes.medium

                ) {
                Text(text = imageList[index].name)
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun ImageCarousel(
    images: List<ImageObject>,
    currentImageViewed: Int
) {
    Column(modifier = Modifier.fillMaxHeight(), Arrangement.SpaceBetween) {
        ImageView(images[currentImageViewed])
    }
}

@ExperimentalCoilApi
@Composable
fun ImageView(imageObject: ImageObject) {
    val painter = rememberImagePainter(
        data = imageObject.url,
        builder = {
            crossfade(true)
        }
    )

    Box {
        Column {
            Image(
                painter = painter,
                contentDescription = imageObject.name,
                modifier = Modifier.fillMaxWidth().padding(4.dp).weight(4.0f).fillMaxHeight()
            )
            Text(
                text = imageObject.name,
                color = MaterialTheme.colors.primaryVariant,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(4.dp).weight(1.0f).fillMaxHeight()
            )
            Text(
                text = imageObject.description,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(4.dp).weight(1.0f).fillMaxHeight()

            )
        }

        when (painter.state) {
            is ImagePainter.State.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            else -> {
            }
        }
    }

}