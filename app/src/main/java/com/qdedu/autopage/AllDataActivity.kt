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
    @AutoPage
    @JvmField
    var workId = 1L
    @AutoPage
    @JvmField
    var moduleType = 1
    @AutoPage
    @JvmField
    var workIdL :Long? = 1L
    @AutoPage
    @JvmField
    var moduleTypeI:Int? = 1
    @AutoPage
    @JvmField
    var canBack = true

    @AutoPage
    @JvmField
    var canBackB:Boolean? = true


    @AutoPage
    @JvmField
    var csd = 'c'

    @AutoPage
    @JvmField
    var csdC:Char? = 'c'


    @AutoPage
    @JvmField
    var bytetB:Byte? = 1

    @AutoPage
    @JvmField
    var bytet = 1.toByte()


    @AutoPage
    @JvmField
    var csdf = 1.0f

    @AutoPage
    @JvmField
    var csdF:Float? = 1.0f



    @AutoPage
    @JvmField
    var doublee= 123.5e10

    @AutoPage
    @JvmField
    var doubleeD:Double? = 1.0


    @AutoPage
    @JvmField
    var shortf = 1.toShort()

    @AutoPage
    @JvmField
    var shortfS:Short? = 1

    @AutoPage
    @JvmField
    var charS:CharSequence? = null

    @AutoPage
    @JvmField
    var charAr:CharArray? = null

    @AutoPage
    @JvmField
    var intAr:IntArray? = intArrayOf(1,2,3,4,5)

    @AutoPage
    @JvmField
    var longAr:LongArray? = null

    @AutoPage
    @JvmField
    var booleanAr:BooleanArray? = null

    @AutoPage
    @JvmField
    var doubleAr:DoubleArray? = null

    @AutoPage
    @JvmField
    var floatAr:FloatArray? = null

    @AutoPage
    @JvmField
    var byteAr:ByteArray? = null


    @AutoPage
    @JvmField
    var arrayList:ArrayList<Int>? = null


    @AutoPage
    @JvmField
    var arrayStringList:ArrayList<String>? = null


    @AutoPage
    @JvmField
    var arrayCharSequenceList:ArrayList<CharSequence>? = null


    @AutoPage
    @JvmField
    var abData:ArrayList<MyData>? = null

    @AutoPage
    @JvmField
    var abDataArry:Array<MyData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_data)
        AutoJ.inject(this)

        Toast.makeText(this,myData?.toString()+message,Toast.LENGTH_LONG).show()
    }
}