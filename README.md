![](https://img.shields.io/badge/JitPack-1.0.7-green)
![](https://img.shields.io/badge/JCenter-1.0.4-yellowgreen)
![](https://img.shields.io/badge/code-kotlin-red)
![](https://img.shields.io/badge/Android%20Arsenal-AutoPage-yellow)
![](https://img.shields.io/badge/author-smartbackme-blue)
# AutoPage
Version number v1.0.2

Fast jump tool supporting kotlin & Java

1. Support serializable large object transfer

2. Support multi process activity jump

code address: [Version number v1.0.2](https://github.com/smartbackme/AutoPage/tree/v1.0.2)

Document address: [Version number v1.0.2](https://github.com/smartbackme/AutoPage/blob/v1.0.2/README.md)

chinese Document address: [中文文档地址 版本 v1.0.2](https://github.com/smartbackme/AutoPage/blob/v1.0.2/README-zh.md)


Version number v1.0.7

Update content: (quick jump tool specially designed for kotlin. If your project only supports Java language, please do not use this version)

1. The code adopts kotlin syntax

2. Support default value function

3. The serializable data transmission is no longer supported, but the Parcelable large object transmission with better performance is used instead

4. Support multi process activity jump

5. Reduce the memory occupation and improve the recyclable memory

# AutoPage v1.0.7
If you think it's good, gives me a star

Android activity easy jump
[中文说明](https://github.com/smartbackme/AutoPage/blob/master/README-zh.md)

Every time activity or fragment jumps to pass value, are you tired of parameter passing.

If you have too much data, your code will be miserable, even in a good design. So today I recommend a tool for you

Compare with the original jump

# Comparison:

1. Comparison of jump modes

```
 Intenti=new Intent(this,MainActivity.class);
 startActivity(i);
```
vs

```
ApMainActivity.newInstance().start(this)

```

```
	//send
    Intenti=new Intent(this,MainActivity.class);
    Bundle bundle = new Bundle();
    bundle.putInt("message", "123");
    i.putExtra("Bundle", bundle);
    startActivity(i);
	//back
	String  s=bundle.getString("message","");

```
vs

```
	//send
	ApMainActivity.newInstance().apply {
                    message = "123"
                } .start(this)
	//back
	AutoJ.inject(this);
```

Parcelable send
```bash
	//send
	 ApAllDataActivity.newInstance().apply {
                    message = "123"
                    myData = MyData("hfafas",true,21)
                } .start(this)
	//back
	AutoJ.inject(this);
```

# AutoPage
Android activity easy jump

# must
1. androidx
2. kotlin & java

# Support transport type

All basic types supported by bundles (except ShortArray)

All of the following types are supported. If the type is not the following, a kapt error may be reported

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


#########use#########

Since jcenter service will be abandoned after May 1, the project will be migrated to jitpack, and the version number will be changed to 1.0.7 at the same time

project : build.gradle
```
buildscript {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
```
config
your module
kotlin kapt
Your project must support @Parcelize annotation, that is, you must add application plugin: 'kotlin Android extensions'
```
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
androidExtensions {
        experimental = true
    }
}


    kapt com.github.smartbackme.AutoPage:autopage-processor:1.0.7
    implementation com.github.smartbackme.AutoPage:autopage:1.0.7
```

**point**

 1. @AutoPage in class or field
 2. Ap suffix


*kotlin：*
1. filed must have @JvmField and @AutoPage
2. onCreate method must have AutoJ.inject(this)

*java：*
1. filed must have @AutoPage
2. onCreate method must have AutoJ.inject(this)


#########for Activity usage#########
## example one
simple jump to activity

```
@AutoPage
class SimpleJump1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_jump1)
    }
}
```
then

```
ApSimpleJump1Activity.newInstance().start(this)

```

## example two
simple jump to activity and message

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
then

```
            ApMainActivity2.newInstance().apply {
                message = "123"
            } .start(this)
```

## example three:
jump to activity and result

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
then

```
            ApSimpleJumpResultActivity.newInstance().apply {
                requestCode = 1
            }.start(this)
```

## example four:
Parcelable

pojo
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
then

```
            ApAllDataActivity.newInstance().apply {
                message = "123"
                myData = MyData("hfafas",true,21)
```

## example five:
default value

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
then

```
            ApDefaultValueActivity.newInstance().apply {
            } .start(this)
```

#########for Fragment usage#########

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

then

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

