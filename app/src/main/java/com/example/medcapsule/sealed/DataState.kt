package com.example.medcapsule.sealed

import com.example.medcapsule.models.User

sealed class DataState {
    class Success(val data: MutableList<User>) : DataState()
    class Failure(val message: String) : DataState()
    data object Loading : DataState()
    data object Empty : DataState()
}