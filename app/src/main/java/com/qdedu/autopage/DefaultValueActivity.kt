package com.qdedu.autopage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DefaultValueActivity : AppCompatActivity() {

    @AutoPage
    @JvmField
    var message:String? = "this is default value"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_value)
        AutoJ.inject(this)
//        var args = intent.getParcelableExtra<ArgsData>("123")
        findViewById<Button>(R.id.button6).text = message
    }
}