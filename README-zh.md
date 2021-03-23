版本号 v1.0.2
支持kotlin & java的快速跳转工具
1. 支持 Serializable 大对象传输
2. 支持多进程activity 跳转
文档地址：

代码地址： [Version number v1.0.2](https://github.com/smartbackme/AutoPage/tree/v1.0.2)

文档地址： [Version number v1.0.2](https://github.com/smartbackme/AutoPage/blob/v1.0.2/README-zh.md)


版本号 v1.0.4
更新内容：（专门为kotlin设计的快速跳转工具，如果你的项目只支持java语言请不要用该版本）
1. 代码采用kotlin 语法糖
2. 支持默认值功能
3. 不再支持Serializable数据传输，改为性能更好的 Parcelable 大对象传输
4. 支持多进程activity 跳转
5. 降低内存占用，可回收内存提升

# AutoPage v1.0.4

如果觉得不错 给个star

activity 或者 fragment 每次跳转传值的时候，你是不是都很厌烦那种，参数传递。
那么如果数据极其多的情况下，你的代码将苦不堪言，即使在很好的设计下，也会很蛋疼。那么今天我给大家推荐一个工具
和咱原生跳转进行比较
# 比较：
1.跳转方式比较

```bash
 Intenti=new Intent(this,MainActivity.class);
 startActivity(i);
```
vs

```bash
ApMainActivity.newInstance().start(this)
```

```bash
    //发送
    Intenti=new Intent(this,MainActivity.class);
    Bundle bundle = new Bundle();
    bundle.putInt("message", "123");
    i.putExtra("Bundle", bundle);
    startActivity(i);
    //接收
	String  s=bundle.getString("message","");

```
vs

```bash
	//发送
	ApMainActivity.newInstance().apply {
                    message = "123"
                } .start(this)
	//接收
	AutoJ.inject(this);
```

实体发送
```bash
	//发送
	 ApAllDataActivity.newInstance().apply {
                    message = "123"
                    myData = MyData("hfafas",true,21)
                } .start(this)
	//接收
	AutoJ.inject(this);
```

# AutoPage
Android 容易的跳转工具
# 注意事项：必须有如下两个要求
1. androidx
2. kotlin & java

# 支持传输类型

bundle 支持的基本类型都支持（除ShortArray）
以下类型都支持，如果类型不是如下类型，可能会报kapt错误

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

project : build.gradle 项目的gradle配置
```
buildscript {
    repositories {
        maven { url 'https://dl.bintray.com/297165331/AutoPage'}
    }
```
在你的每个需要做容易跳转的模块添加如下配置
1. 你的项目必须要支持 kapt
2. kotlin kapt
3. 你的项目必须支持 @Parcelize 注解 也就是必须添加 apply plugin: 'kotlin-android-extensions'
```
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
androidExtensions {
        experimental = true
    }
}

    implementation 'com.kangaroo:autopage:1.0.4'
    kapt 'com.kangaroo:autopage-processor:1.0.4'
```

**重点**

 1. @AutoPage 只能在字段或者类上标注
 2. Ap 作为前缀，为你快速跳转


*kotlin：*
1. 字段必须标注 @JvmField 和 @AutoPage
2. onCreate 中 在你的需要跳转的页面加入 AutoJ.inject(this)

*java：*
1. 字段必须标注 @AutoPage
2. onCreate 中 在你的需要跳转的页面加入 AutoJ.inject(this)


######### Activity 中使用#########
## 例1
简单的跳转

```
@AutoPage
class SimpleJump1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_jump1)
    }
}
```
之后调用

```
ApSimpleJump1Activity.newInstance().start(this)
```

## 例2
简单的跳转并且带参数

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
之后调用

```
            ApMainActivity2.newInstance().apply {
                message = "123"
            } .start(this)
```

## 例3:
跳转带有result

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
之后调用

```
            ApSimpleJumpResultActivity.newInstance().apply {
                requestCode = 1
            }.start(this)
```

## 例4:
实体传输

实体
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
之后调用

```
            ApAllDataActivity.newInstance().apply {
                message = "123"
                myData = MyData("hfafas",true,21)
```

## 例5:
默认值

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
之后调用

```
            ApDefaultValueActivity.newInstance().apply {
            } .start(this)
```

####### 在 fragment 中使用 #########

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

之后调用

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

