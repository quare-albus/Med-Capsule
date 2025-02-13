package com.example.medcapsule.Models

data class Chapter(
    val name: String = "",
    val number: Int = 0,
    val imageUrl: String = "https://upload.wikimedia.org/wikipedia/commons/d/d1/Image_not_available.png",
    val parts: List<Part>? = null
)