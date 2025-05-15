package com.example.myapplication.network

import retrofit2.http.GET
import com.example.myapplication.model.TodoItem

interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): List<TodoItem>
}