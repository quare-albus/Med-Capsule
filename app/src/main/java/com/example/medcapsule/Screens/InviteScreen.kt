package com.example.medcapsule.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.medcapsule.Quiz.MainApplication
import com.example.medcapsule.SharedPreferencesManager
import com.example.medcapsule.database.attemptSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun InviteScreen()
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(text = "Invite Screen")
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
    ){
        Text(text = "All Fine!")
        val _deb = MutableStateFlow("")
        val deb = _deb.asStateFlow()

        Text(SharedPreferencesManager.getString("something","Default"))

        Text(deb.collectAsState().value)
    }
}