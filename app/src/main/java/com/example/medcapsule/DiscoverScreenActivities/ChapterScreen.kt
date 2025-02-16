package com.example.medcapsule.DiscoverScreenActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.medcapsule.R

class ChapterScreen : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                intent.getStringExtra("ChapterName").toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            val activity = (LocalActivity.current as? Activity)
                            IconButton(onClick = { activity?.finish() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                // for starting activities
                val context = LocalContext.current
                val chapname = intent.getStringExtra("ChapterName").toString()
                val Nhylyts = intent.getStringExtra("Nhylyts").toString()
                val Notes = intent.getStringExtra("Notes").toString()

                Column(
                    modifier = Modifier.padding(innerPadding)
                        .background(color = Color(0xffeff6ff))
                        .fillMaxSize()
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Red)
                    ) {
                        AsyncImage(
                            model = intent.getStringExtra("ChapterimageUrl"),
                            contentDescription = "image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp,16.dp,16.dp,8.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .clickable(
                                onClick = {
                                    val intentCrnt = Intent(context, ChaptersPart::class.java)
                                    intentCrnt.putExtra("ChapterName",chapname)
                                    context.startActivity(intentCrnt)
                                }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painterResource(id = R.drawable.openbook),
                            contentDescription = "Localized description",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(20.dp))
                        Text("Parts")
                    }

                    Row {
                        Column (
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .padding(16.dp,4.dp,16.dp,4.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.White),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Localized description",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(
                                        onClick = {
                                            val intentCrnt = Intent(context, PdfViewer::class.java)
                                            intentCrnt.putExtra("ChapterName",chapname)
                                            intentCrnt.putExtra("Url",Nhylyts)
                                            context.startActivity(intentCrnt)
                                        }
                                    ))
                            Text(Nhylyts)
                            Text("NCERT Highlights")
                        }

                        Column (
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .padding(16.dp,4.dp,16.dp,4.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.White),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painterResource(id = R.drawable.notes),
                                contentDescription = "Localized description",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(
                                        onClick = {
                                            val intentCrnt = Intent(context, PdfViewer::class.java)
                                            intentCrnt.putExtra("ChapterName",chapname)
                                            intentCrnt.putExtra("Url",Notes)
                                            context.startActivity(intentCrnt)
                                        })
                            )
                            Text(Notes)
                            Text("Notes")
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp,4.dp,16.dp,16.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background((Color.White)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        Image(
                            painterResource(id = R.drawable.quiz),
                            contentDescription = "Localized description",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(35.dp),
                            colorFilter = ColorFilter.tint(Color(0xFFEAB308))
                        )

                        Column()
                        {
                            Text("Grand Quiz",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                            Text("Test Your Knowledge",
                                fontSize = 10.sp)
                        }

                        Box(modifier = Modifier
                            .size(75.dp,40.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Black),
                            contentAlignment = Alignment.Center)
                        {
                            Text("Start Now",
                                fontWeight = FontWeight.Black,
                                color = Color.White)
                        }
                    }
                }
            }
        }
    }
}