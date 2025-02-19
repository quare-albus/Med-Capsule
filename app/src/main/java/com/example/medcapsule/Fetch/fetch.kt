package com.example.medcapsule.Fetch

import android.content.Intent
import android.graphics.Paint.Align
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.medcapsule.DiscoverScreenActivities.ChapterScreen
import com.example.medcapsule.Models.Chapter
import com.example.medcapsule.firestore
import com.google.firebase.Firebase
import com.google.firebase.storage.storage


@Composable
fun fetchChaptersOf(grade: Int) {
    val chapters = remember { mutableStateListOf<Chapter>() }
    val chapterCollection = firestore.collection("Chapters").whereEqualTo("Grade", grade)

    LaunchedEffect(Unit) {
        chapterCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val note = document.toObject(Chapter::class.java)
                    chapters.add(note)
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    displayItem(chapters)
}

@Composable
fun displayItem(chapters : List<Chapter>){


    val context = LocalContext.current
    LazyRow{
        items(chapters) { chapter ->
            var url by remember{mutableStateOf("")}
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 170.dp, height = 240.dp)
                    .padding(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xfffefce8), //Card background color
                    contentColor = Color.Black  //Card content color,e.g.text
                ),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    val intent = Intent(context, ChapterScreen::class.java)
//                    val b = Bundle()
//                    b.putString("ChapterName",chapter.name)
                    intent.putExtra("ChapterName",chapter.name)
                    intent.putExtra("ChapterimageUrl",url)
                    intent.putExtra("Nhylyts",chapter.Nhylyts)
                    intent.putExtra("Notes",chapter.Notes)
                    context.startActivity(intent)
                }
            ){

                Column(
                    modifier = Modifier.fillMaxSize()
                ){

                    var storage = Firebase.storage
                    var imageRef = storage.reference.child(chapter.imageUrl)

                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        url = uri.toString()
                    }

                    if (url == ""){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f),
                            contentAlignment = Alignment.Center){
                            CircularProgressIndicator()
                        }
                    }
                    else {
                        AsyncImage(
                            model = url,
                            contentDescription = "Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(3f)
                        )
                    }
                    Column(modifier = Modifier.weight(2f))
                    {
                        Text(
                            text = chapter.name,
                            modifier = Modifier
                                .padding(8.dp),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Chapter ${chapter.ChapterNumber}",
                            modifier = Modifier
                                .offset(x = 8.dp),
                            fontWeight = FontWeight.Thin
                        )
                    }
                }

            }
        }
    }
}