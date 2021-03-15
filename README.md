# AutoPage
Android activity easy jump 

# must
1.androidx
2.kotlin & java

#####################################################use##########################################
project : build.gradle
buildscript {
    repositories {
        maven { url 'https://dl.bintray.com/297165331/AutoPage'}
    }

project : build.gradle
your module
############## kotlin kapt ###############################
apply plugin: 'kotlin-kapt'


    implementation 'com.kangaroo:autopage:1.0.2'
    kapt 'com.kangaroo:autopage-processor:1.0.2'



point @AutoPage in class or field

#########################for Activity usage#####################
example one
simple jump to activity

@AutoPage
class SimpleJump1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_jump1)
    }
}

then
  ApSimpleJump1Activity.getInstance().start(this)

point Ap suffix

example two
simple jump to activity and message

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

then
   ApMainActivity2.getInstance().setMessage("123").start(this)


kotlin point：
1. filed must have @JvmField and @AutoPage
2. onCreate method must have AutoJ.inject(this)

java point：
1. filed must have @AutoPage
2. onCreate method must have AutoJ.inject(this)

example three:
jump to activity and result

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

then
    ApSimpleJumpResultActivity.getInstance().requestCode(1).start(this)


#########################for Fragment usage#####################
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

then
ApFragmentSimpleFragment.getInstance().setMessage("134").build()