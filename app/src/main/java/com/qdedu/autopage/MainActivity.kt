package com.qdedu.autopage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            ApMainActivity2.getInstance().setMessage("123").start(this)
        }
        findViewById<Button>(R.id.button2).setOnClickListener {
            ApSimpleJump1Activity.getInstance().start(this)
        }
        findViewById<Button>(R.id.button3).setOnClickListener {
            ApFragmentSimpleActivity.getInstance().start(this)
        }
    }
}