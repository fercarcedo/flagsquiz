package uo246008.quizflags

import android.app.Application

class Quizflags : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        lateinit var app: Quizflags
    }
}