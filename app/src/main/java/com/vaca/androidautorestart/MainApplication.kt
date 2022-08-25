package com.vaca.androidautorestart

import android.app.Application

class MainApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        initErrorHandle()
    }

    private fun initErrorHandle() {
        val mCrashExceptionHandler = CrashExceptionHandler(applicationContext, "Log", "Crash")
        Thread.setDefaultUncaughtExceptionHandler(mCrashExceptionHandler)
//        Thread.setUncaughtExceptionPreHandler(mCrashExceptionHandler);
    }
}