package com.example.medcapsule.Models

data class Chapter(
    val name: String = "",
    val ChapterNumber: Int = 0,
    val imageUrl: String = "https://upload.wikimedia.org/wikipedia/commons/d/d1/Image_not_available.png",
    val Parts: List<Part>? = null,
    val Grade : Int = 0,
    val Nhylyts : String = "",
    val Notes : String = ""
)