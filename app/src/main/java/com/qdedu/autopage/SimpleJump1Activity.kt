package com.qdedu.autopage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
@AutoPage
class SimpleJump1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_jump1)
    }
}