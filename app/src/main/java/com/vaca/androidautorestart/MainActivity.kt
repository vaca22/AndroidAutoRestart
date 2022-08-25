package com.vaca.androidautorestart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {
    lateinit var gaga:IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread{
            sleep(3000)
            val a=gaga[0]
        }.start()

    }
}