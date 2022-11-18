package com.leos.superplugin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.leos.superplugin.R
import com.leos.superplugin.app.LargeMyApplication

class LargeMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = LargeMyApplication.TEXT
    }
}