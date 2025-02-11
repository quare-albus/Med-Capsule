package com.example.medcapsule.Fetch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medcapsule.R
import com.example.medcapsule.firestore

data class Chapter(
    val name : String = "",
    val number : Int = 0
)

@Composable
fun fetchChaptersOf(collectionName: String) {
    val chapters = remember { mutableStateListOf<Chapter>() }
    val chapterCollection = firestore.collection(collectionName)

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
    LazyRow{
        items(chapters) { chapter ->
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
                shape = RoundedCornerShape(15.dp)
            ){
                Column(
                    modifier = Modifier.fillMaxSize()
                ){
                    Image(
                        painterResource(id = R.drawable.img),
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = chapter.name,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    Text(text = "Chapter ${chapter.number}",
                        modifier = Modifier.offset(x = 8.dp),
                        fontWeight = FontWeight.Thin)
                }

            }
        }
    }
}