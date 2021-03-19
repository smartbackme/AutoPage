package com.qdedu.autopage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.qdedu.autopage.data.MyData

class AllDataActivity : AppCompatActivity() {

    @AutoPage
    @JvmField
    var myData:MyData? = null
    @AutoPage
    @JvmField
    var message:String? = "this is default value"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_data)
        AutoJ.inject(this)

        Toast.makeText(this,myData?.toString()+message,Toast.LENGTH_LONG).show()
    }
}