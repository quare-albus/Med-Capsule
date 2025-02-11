package com.example.medcapsule.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medcapsule.Fetch.fetchChaptersOf
import com.example.medcapsule.R

@Composable
fun DiscoverScreen()
{
    Column(
        modifier = Modifier
            .fillMaxSize(0.5f)
            .background(color = Color(0xffeff6ff))
            .verticalScroll(rememberScrollState())
    ) {
        Text(text="Hi Vansh,",
            fontSize = 40.sp,
            color = Color(0xFF1F2937),
            modifier = Modifier
                .padding(10.dp))

        Text(text="Good Evening",
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(8.dp))

        Text(text="Category",
            modifier = Modifier.padding(8.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        category()

        Text(text="11th Bio",
            modifier = Modifier.padding(8.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        fetchChaptersOf("Chapters11")

        Text(text="12th Bio",
            modifier = Modifier.padding(8.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        fetchChaptersOf("Chapters12")
    }
}

@Composable
fun category(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImageHolder(
            id = R.drawable.bio11,
            contentDescription = "11th Bio",
        )
        ImageHolder(
            id = R.drawable.bio12,
            contentDescription = "12th Bio"
        )
        ImageHolder(
            id = R.drawable.parttest,
            contentDescription = "Partwise\n    test"
        )
        ImageHolder(
            id = R.drawable.fulltest,
            contentDescription = "      Full\n Syllabus\n     test"
        )
    }
}

@Composable
fun ImageHolder(id: Int, contentDescription: String,modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box( modifier = Modifier
            .clip(CutCornerShape(10.dp))
            .background(color = Color(0xfffefce8))
        )
        {
            Image(
                painterResource(id = id),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(70.dp)
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = contentDescription,
            fontWeight = FontWeight.SemiBold)
    }
}
