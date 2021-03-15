package com.qdedu.autopage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

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
        findViewById<Button>(R.id.button4).setOnClickListener {
            ApSimpleJumpResultActivity.getInstance().requestCode(1).start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
           when(requestCode){
               1 -> Toast.makeText(this,data?.getStringExtra("message"),Toast.LENGTH_LONG).show()
           }
        }
    }
}