package com.example.medcapsule.DiscoverScreenActivities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.rajat.pdfviewer.compose.PdfRendererViewCompose

class PdfViewer : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            intent.getStringExtra("ChapterName")?.let {
                                Text(
                                    it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
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
            ){innerPadding ->
                var url by remember { mutableStateOf("") }

                var storage = Firebase.storage
                var docRef = intent.getStringExtra("Url")?.let { storage.reference.child(it) }

                if (docRef != null) {
                    docRef.downloadUrl.addOnSuccessListener { uri ->
                        url = uri.toString()
                    }
                }
                Box(modifier = Modifier.padding(innerPadding)
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
    }
}
