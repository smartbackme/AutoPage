Version number v1.0.2

Fast jump tool supporting kotlin & Java

1. Support serializable large object transfer

2. Support multi process activity jump

Document address:


Version number v1.0.3

Update content: (quick jump tool specially designed for kotlin. If your project only supports Java language, please do not use this version)

1. The code adopts kotlin syntax

2. Support default value function

3. The serializable data transmission is no longer supported, but the Parcelable large object transmission with better performance is used instead

4. Support multi process activity jump

5. Reduce the memory occupation and improve the recyclable memory

# AutoPage
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

#########use#########

project : build.gradle
```
buildscript {
    repositories {
        maven { url 'https://dl.bintray.com/297165331/AutoPage'}
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


    implementation 'com.kangaroo:autopage:1.0.3'
    kapt 'com.kangaroo:autopage-processor:1.0.3'
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

# License

```
                    GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.
```

