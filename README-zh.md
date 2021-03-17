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
ApMainActivity.getInstance().start(this);
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
	ApMainActivity.getInstance().setMessage("123").start(this);
	//接收
	AutoJ.inject(this);
```

# AutoPage
Android 容易的跳转工具
# 注意事项：必须有如下两个要求
1. androidx
2. kotlin & java

#########使用#########

project : build.gradle 项目的gradle配置
```
buildscript {
    repositories {
        maven { url 'https://dl.bintray.com/297165331/AutoPage'}
    }
```
在你的每个需要做容易跳转的模块添加如下配置
你的项目必须要支持 kapt
kotlin kapt
```
apply plugin: 'kotlin-kapt'

    implementation 'com.kangaroo:autopage:1.0.2'
    kapt 'com.kangaroo:autopage-processor:1.0.2'
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
ApSimpleJump1Activity.getInstance().start(this)
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
ApMainActivity2.getInstance().setMessage("123").start(this)
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
ApSimpleJumpResultActivity.getInstance().requestCode(1).start(this)
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
ApFragmentSimpleFragment.getInstance().setMessage("134").build()
```

# License

```
                    GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.
```

