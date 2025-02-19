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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.medcapsule.R
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import kotlinx.coroutines.delay

class PartScreen : ComponentActivity() {

    var extplayer :ExoPlayer? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val player = ExoPlayer.Builder(this).build()
        extplayer = player
        val PartName = intent.getStringExtra("PartName")?: ""

        setContent {
            val navController = rememberNavController()
            var isPlaying by remember {
                mutableStateOf(true)
            }
            var test by remember {
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
                    var url by remember { mutableStateOf("") }
//                    var isReady by remember { mutableStateOf(false) }

                    var storage = Firebase.storage
                    var docRef = intent.getStringExtra("Audio")?.let { storage.reference.child(it) }

                    if (docRef != null) {
                        docRef.downloadUrl.addOnSuccessListener { uri ->
                            url = uri.toString()
                        }
                    }
                    if (url!="") {
                        Box(modifier = Modifier.fillMaxWidth()) {

                            var test by remember { mutableStateOf(false) }
                            var isBuffering by remember { mutableStateOf(false) }
                            var isReady by remember { mutableStateOf(false) } // already defined above

                            val uri = Uri.parse(url)
                            val mediaItem = MediaItem.fromUri(uri)
                            player.addMediaItem(mediaItem)
                            player.prepare()
                            player.addListener(
                                object : Player.Listener {
                                    override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                                        test = isPlayingValue
                                    }

                                    override fun onPlaybackStateChanged(playbackState: Int) {
                                        super.onPlaybackStateChanged(playbackState)

                                        isBuffering = if (playbackState == Player.STATE_BUFFERING) {
                                            true
                                        } else {
                                            false
                                        }

                                        if (playbackState == Player.STATE_READY) {
                                            isReady = true
                                        } else {
                                            isReady = false
                                        }
                                    }
                                }
                            )

                            val currentPosition = remember {
                                mutableLongStateOf(0)
                            }

                            val sliderPosition = remember {
                                mutableLongStateOf(0)
                            }

                            val totalDuration = remember {
                                mutableLongStateOf(0)
                            }


                            LaunchedEffect(
                                key1 = player.currentPosition,
                                key2 = player.isPlaying,
                                key3 = isReady
                            ) {
                                delay(100)
                                while ((player.currentPosition - sliderPosition.longValue) == 0L && player.isPlaying) {
                                    sliderPosition.longValue += 100L
                                    delay(100)
                                }
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
                                // the below text is neccessary for the proper run of the slide and things Reason: Unkown
                                val rubbish = test.toString() + "Ready: $isReady" + "Slide Position: ${sliderPosition.longValue} " + isBuffering.toString() + player.currentPosition.toString() + totalDuration.longValue.toString() + url

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
                                        icon = if (player.isPlaying) R.drawable.play else R.drawable.ic_launcher_foreground,
                                        size = 50.dp,
                                        onClick = {
                                            if (!isBuffering) {
                                                if (player.isPlaying) {
                                                    player.pause()
                                                } else {
                                                    player.play()
                                                }
                                            }
                                        })
                                }
                            }
                        }
                    }
                    else
                    {
                        CircularProgressIndicator()
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
                    composable("NotesPage"){
                        intent.getStringExtra("Notes")?.let { it1 -> PdfViewerCompose(it1) }
                    }
                    composable("NhylytsPage"){
                        intent.getStringExtra("Nhylyts")?.let { it1 -> PdfViewerCompose(it1) }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        extplayer?.pause()
        extplayer?.release()
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

            var storage = Firebase.storage
            var imageRef = intent.getStringExtra("PartimageUrl")
                ?.let { storage.reference.child(it) }

            var imageUrl by remember{mutableStateOf("")}

            imageRef?.downloadUrl?.addOnSuccessListener { uri ->
                imageUrl = uri.toString()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                if(imageUrl!=""){
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                }
                else{
                    CircularProgressIndicator()
                }
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
                                navController.navigate("NhylytsPage")
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
                               navController.navigate("NotesPage")
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

    @Composable
    fun PdfViewerCompose(docUrl: String){
        var url by remember { mutableStateOf("") }

        var storage = Firebase.storage
        var docRef = docUrl.let { storage.reference.child(it) }

        if (docRef != null) {
            docRef.downloadUrl.addOnSuccessListener { uri ->
                url = uri.toString()
            }
        }
        Box(modifier = Modifier
            .fillMaxSize()){
            if(url!="") {
                PdfRendererViewCompose(
                    url = url
                )
            }
            else{
                CircularProgressIndicator()
            }
        }
    }
}

