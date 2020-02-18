package uo246008.quizflags.service

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import uo246008.quizflags.model.Logs

interface LogService {
    @POST("/api/logs")
    suspend fun sendLogs(@Body logs: Logs)

    companion object {
        private const val BASE_URL = "https://quizflags.herokuapp.com"
        val instance by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            retrofit.create(LogService::class.java)
        }
    }
}