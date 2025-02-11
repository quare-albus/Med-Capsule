package com.example.greetingcard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NavBarButton(imageVector: Int, name: String, navController: NavController)
{
    TextButton(
        onClick = {
            navController.navigate(name)
        }
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painterResource(id = imageVector),
                contentDescription = "Localized description",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(20.dp))
            Text(text = name)
        }
    }
}