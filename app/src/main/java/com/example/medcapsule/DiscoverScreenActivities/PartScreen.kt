package com.example.medcapsule.DiscoverScreenActivities

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.medcapsule.R
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import kotlinx.coroutines.delay

class PartScreen : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val player = ExoPlayer.Builder(this).build()
        val PartName = intent.getStringExtra("PartName")?: ""

        setContent {
            val navController = rememberNavController()
            val isPlaying = remember {
                mutableStateOf(false)
            }
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(PartName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            val activity = (LocalActivity.current)
                            IconButton(onClick = { activity?.finish() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    Box( modifier = Modifier.fillMaxWidth()) {

                        LaunchedEffect(Unit) {
                            val uri = Uri.parse(getString(R.string.Url))
                            val mediaItem = MediaItem.fromUri(uri)
                            player.addMediaItem(mediaItem)
                        }
                        player.prepare()

                        val currentPosition = remember {
                            mutableLongStateOf(0)
                        }

                        val sliderPosition = remember {
                            mutableLongStateOf(0)
                        }

                        val totalDuration = remember {
                            mutableLongStateOf(0)
                        }

                        LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
                            delay(100)
                            currentPosition.longValue = player.currentPosition
                            sliderPosition.longValue = currentPosition.longValue
                        }

                        LaunchedEffect(currentPosition.longValue) {
                            sliderPosition.longValue = currentPosition.longValue
                        }

                        LaunchedEffect(player.duration) {
                            if (player.duration > 0) {
                                totalDuration.longValue = player.duration
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            TrackSlider(
                                value = sliderPosition.longValue.toFloat(),
                                onValueChange = {
                                    sliderPosition.longValue = it.toLong()
                                },
                                onValueChangeFinished = {
                                    currentPosition.longValue = sliderPosition.longValue
                                    player.seekTo(sliderPosition.longValue)
                                },
                                songDuration = totalDuration.longValue.toFloat()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    text = (currentPosition.longValue).convertToText(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp),
                                    color = Color.Black,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                val remainTime =
                                    totalDuration.longValue - currentPosition.longValue
                                Text(
                                    text = if (remainTime >= 0) remainTime.convertToText() else "",
                                    modifier = Modifier
                                        .padding(8.dp),
                                    color = Color.Black,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(20.dp))
                                ControlButton(
                                    icon = if (isPlaying.value) R.drawable.play else R.drawable.play,
                                    size = 50.dp,
                                    onClick = {
                                        if (isPlaying.value) {
                                            player.pause()
                                        } else {
                                            player.play()
                                        }
                                        isPlaying.value = player.isPlaying
                                    })
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "mainPage",
                    modifier = Modifier.padding(innerPadding)
                        .fillMaxSize()
                ) {
                    composable("mainPage"){
                        mainScreen(navController)
                    }
                    composable("pdfPage"){
                        PdfRendererViewCompose(
                            url = "https://openaccess.thecvf.com/content/CVPR2024/papers/Yang_Depth_Anything_Unleashing_the_Power_of_Large-Scale_Unlabeled_Data_CVPR_2024_paper.pdf",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun TrackSlider(
        value: Float,
        onValueChange: (newValue: Float) -> Unit,
        onValueChangeFinished: () -> Unit,
        songDuration: Float
    ) {
        Slider(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            onValueChangeFinished = {

                onValueChangeFinished()

            },
            valueRange = 0f..songDuration,
        )
    }

    @Composable
    fun ControlButton(icon: Int, size: Dp, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .clickable {
                    onClick()
                }, contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(size / 1.5f),
                painter = painterResource(id = icon),
                tint = Color.Black,
                contentDescription = null
            )
        }
    }

    private fun Long.convertToText(): String {
        val sec = this / 1000
        val minutes = sec / 60
        val seconds = sec % 60

        val minutesString = if (minutes < 10) {
            "0$minutes"
        } else {
            minutes.toString()
        }
        val secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            seconds.toString()
        }
        return "$minutesString:$secondsString"
    }

    @Composable
    private fun mainScreen(navController: NavHostController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Red)
            ) {
                        AsyncImage(
                            model = "https://cdn.corporatefinanceinstitute.com/assets/temporary-account.jpeg",
                            contentDescription = "image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .weight(1f)
                        .padding(12.dp)
                        .clickable(
                            onClick = {
                                navController.navigate("pdfPage")
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, //Card background color
                        contentColor = Color.Black  //Card content color,e.g.text
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("NCERT Highlights")
                    }
                }
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .weight(1f)
                        .padding(12.dp)
                        .clickable(
                            onClick = {
                               navController.navigate("pdfPage")
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, //Card background color
                        contentColor = Color.Black  //Card content color,e.g.text
                    ),
                    shape = RoundedCornerShape(15.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Notes")
                    }
                }
            }
        }
    }
}

