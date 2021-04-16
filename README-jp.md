![](https://img.shields.io/badge/JitPack-1.0.8-green)
![](https://img.shields.io/badge/JCenter-1.0.4-yellowgreen)
![](https://img.shields.io/badge/code-kotlin-red)
![](https://img.shields.io/badge/Android%20Arsenal-AutoPage-yellow)
![](https://img.shields.io/badge/author-smartbackme-blue)
# AutoPage
番号 v1.0.2
kotlin & java　をサポートします
1. Serializable をサポートします

code address： [Version number v1.0.2](https://github.com/smartbackme/AutoPage/tree/v1.0.2)

説明書： [Version number v1.0.2](https://github.com/smartbackme/AutoPage/blob/v1.0.2/README-zh.md)


番号 v1.0.8
更新内容：（特にkotlinのために）
1. kotlin の　利用
2. default　value
3.  Parcelable をサポートします
4. 性能を向上させる

# AutoPage v1.0.8

いいと思ったら　starをお願いします

# 比較：
1.ジャンプ方式の比較

```bash
 Intenti=new Intent(this,MainActivity.class);
 startActivity(i);
```
vs

```bash
ApMainActivity.newInstance().start(this)
```

```bash
    //送信
    Intenti=new Intent(this,MainActivity.class);
    Bundle bundle = new Bundle();
    bundle.putInt("message", "123");
    i.putExtra("Bundle", bundle);
    startActivity(i);
    //受信
	String  s=bundle.getString("message","");

```
vs

```bash
	//送信
	ApMainActivity.newInstance().apply {
                    message = "123"
                } .start(this)
	//受信
	AutoJ.inject(this);
```

model
```bash
	//送信
	 ApAllDataActivity.newInstance().apply {
                    message = "123"
                    myData = MyData("hfafas",true,21)
                } .start(this)
	//受信
	AutoJ.inject(this);
```

# AutoPage
Android の簡単なツール　
# 注意事項：次の二つの要求が必要です
1. androidx
2. kotlin & java

# タイプ

以下のタイプがサポートされていますが、タイプが以下のタイプでないと，kapt　が　error発生する可能性があります

```bash

    :Parcelable

    String

    Long

    Int

    Boolean

    Char

    Byte

    Float

    Double

    Short

    CharSequence

    CharArray

    IntArray

    LongArray

    BooleanArray

    DoubleArray

    FloatArray

    ByteArray

    ArrayList<Int>

    ArrayList<String>

    ArrayList<CharSequence>

    ArrayList<:Parcelable>

    Array<:Parcelable>
```


#########使用#########

プロジェクト : build.gradle
```
buildscript {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
```
モジュール : build.gradle

```
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
androidExtensions {
        experimental = true
    }
}

    kapt com.github.smartbackme.AutoPage:autopage-processor:1.0.8
    implementation com.github.smartbackme.AutoPage:autopage:1.0.8
```

**重点**

*kotlin：*
1.  @JvmField  @AutoPage
2. onCreate 中 AutoJ.inject(this)　を追加します

*java：*
1. @AutoPage
2. onCreate 中 AutoJ.inject(this)　を追加します


######### Activity 中使用#########
## 例1
簡単なジャンプ

```
@AutoPage
class SimpleJump1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_jump1)
    }
}
```
後で

```
ApSimpleJump1Activity.newInstance().start(this)
```

## 例2
簡単なジャンプとパラメータ付き
```
class MainActivity2 : AppCompatActivity() {

    @AutoPage
    @JvmField
    var message:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        AutoJ.inject(this)
        findViewById<TextView>(R.id.text).text = message
    }
}
```
後で

```
            ApMainActivity2.newInstance().apply {
                message = "123"
            } .start(this)
```

## 例3:
resultがある

```
@AutoPage
class SimpleJumpResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_jump_result)
    }

    override fun onBackPressed() {
        var intent = Intent()
        intent.putExtra("message","123")
        setResult(RESULT_OK,intent)
        super.onBackPressed()
    }
}
```
後で

```
            ApSimpleJumpResultActivity.newInstance().apply {
                requestCode = 1
            }.start(this)
```

## 例4:
model転送

'''
@Parcelize
data class MyData(var message:String,var hehehe: Boolean,var temp :Int):Parcelable
'''

```
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
```
後で

```
            ApAllDataActivity.newInstance().apply {
                message = "123"
                myData = MyData("hfafas",true,21)
```

## 例5:
default valur

```
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
```
後で

```
            ApDefaultValueActivity.newInstance().apply {
            } .start(this)
```

####### fragment 中使用 #########

```
class FragmentSimpleFragment : Fragment() {


    @AutoPage
    @JvmField
    var message:String? = null

    companion object {
        fun newInstance() = FragmentSimpleFragment()
    }

    private lateinit var viewModel: SimpleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.simple_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AutoJ.inject(this)
        viewModel = ViewModelProvider(this).get(SimpleViewModel::class.java)
        view?.findViewById<TextView>(R.id.message)?.text = message

    }

}

```

後で

```
ApFragmentSimpleFragment.newInstance().apply {
                    message = "123"
                }.build()
```

# Document

Wait...

# License

```
                  MIT License

Copyright (c) 2021 zhonghua

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```

