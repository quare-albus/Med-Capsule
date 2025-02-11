package com.example.medcapsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.greetingcard.NavBarButton
import com.example.medcapsule.Screens.CourseScreen
import com.example.medcapsule.Screens.DiscoverScreen
import com.example.medcapsule.Screens.InviteScreen
import com.example.medcapsule.Screens.LibraryScreen
import com.example.medcapsule.Screens.ProfileScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.proto.TargetGlobal

val firestore = FirebaseFirestore.getInstance()

class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Scaffold (
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        navigationIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo_Replacable",
                                modifier = Modifier
                                    .size(60.dp)
                            )
                        },
                        actions = {
                            IconButton(onClick = { /* do something */ }) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Localized description"
                                )
                            }
                        },
                        title = {
                            Text(text = "MedCapsule")
                        }
                    )
                },
                bottomBar = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        NavBarButton(R.drawable.house,"Discover",navController)
                        NavBarButton(R.drawable.play,"Course",navController)
                        NavBarButton(R.drawable.openbook,"Library",navController)
                        NavBarButton(R.drawable.profile,"Profile",navController)
                        NavBarButton(R.drawable.invite,"Invite",navController)
                    }
                }
            ){ innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "Discover",
                    modifier = Modifier.padding(innerPadding)
                        .fillMaxSize()
                ) {
                    composable("Discover") {
                        DiscoverScreen()
                    }
                    composable("Course") {
                        CourseScreen()
                    }
                    composable("Library") {
                        LibraryScreen()
                    }
                    composable("Profile") {
                        ProfileScreen()
                    }
                    composable("Invite") {
                        InviteScreen()
                    }
                }

            }

        }
    }
}