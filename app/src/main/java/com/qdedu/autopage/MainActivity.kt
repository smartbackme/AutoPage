package com.qdedu.autopage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.Toast
import com.qdedu.autopage.data.MyData
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.simple_fragment.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            ApMainActivity2.newInstance().apply {
                message = "123"
            } .start(this)
//            val intent = Intent(this, MainActivity2::class.java)
//            intent.putExtra("123", ArgsData().apply { setMessage("123") })
//            startActivity(intent)
        }
        findViewById<Button>(R.id.button2).setOnClickListener {
            ApSimpleJump1Activity.newInstance().start(this)
        }
        findViewById<Button>(R.id.button3).setOnClickListener {
            ApFragmentSimpleActivity.newInstance().start(this)
        }
        findViewById<Button>(R.id.button4).setOnClickListener {
            ApSimpleJumpResultActivity.newInstance().apply {
                requestCode = 1
            }.start(this)
        }

        findViewById<Button>(R.id.button5).setOnClickListener {
            ApDefaultValueActivity.newInstance().apply {
//                message = "123"
            } .start(this)
        }
        findViewById<Button>(R.id.button6).setOnClickListener {
            ApAllDataActivity.newInstance().apply {
                message = "123"
                myData = MyData("hfafas",true,21)
            } .start(this)
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

///**
// * inner class RequestData,contains all of the field define by annotation @Field
// */
//@Parcelize
//class ArgsData(var hasSetData_message: Boolean = false,
//               private var message: String? = null) : Parcelable {
//
//    fun setMessage(message: String?){
//        hasSetData_message = true
//        this.message = message
//    }
//
//    fun getMessage():String?{
//        return message
//    }
//
//
//}