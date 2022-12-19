package com.leos.superplugin.leoapp

import android.app.Application

/**
 * @author: Leo
 * @time: 2022/11/17
 * @desc:
 */
class LeoLeoLeoLargeMyApplication  : Application(){

    companion object{
        const val TEXT = "text"
    }

    override fun onCreate() {
        super.onCreate()
        LeoApp().test()
    }
}
