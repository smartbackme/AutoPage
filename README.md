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
ApMainActivity.getInstance().start(this);
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
	ApMainActivity.getInstance().setMessage("123").start(this);
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
```
apply plugin: 'kotlin-kapt'

    implementation 'com.kangaroo:autopage:1.0.2'
    kapt 'com.kangaroo:autopage-processor:1.0.2'
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
ApSimpleJump1Activity.getInstance().start(this)
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
ApMainActivity2.getInstance().setMessage("123").start(this)
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
ApSimpleJumpResultActivity.getInstance().requestCode(1).start(this)
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

