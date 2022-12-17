package com.leos.superplugin.leoactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.leos.superplugin.R
import com.leos.superplugin.leoapp.LeoLeoLargeMyApplication

class LeoLeoLargeMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leo_ta_activity_main)
        val text = LeoLeoLargeMyApplication.TEXT
    }
}
