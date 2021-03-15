
# AutoPage
Android 容易的跳转工具
# 注意事项：必须有如下两个要求
1. androidx
2. kotlin & java

#########使用#########
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

