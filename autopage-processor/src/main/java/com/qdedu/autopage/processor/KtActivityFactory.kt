package com.qdedu.autopage.processor

import com.qdedu.autopage.processor.utils.StringUtils
import com.squareup.kotlinpoet.*
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

/**
 * @author shidawei
 * 创建日期：2021/3/17
 * 描述：
 */

const val hasSetData = "hasSetData_"

//const val INTENT = "Intent"
//const val CONTENT_PACKAGE = "android.content"
//const val CONTEXT = "Context"
//private const val ACTIVITY = "Activity"
//private const val FRAGMENT = "Fragment"
//const val FRAGMENT_PACKAGE = "androidx.fragment.app"
//const val ACTIVITY_PACKAGE = "android.app"

const val REQUEST_CODE_FIELD_NAME = "requestCode"

const val START_METHOD = "start"
const val CREATE_INTENT = "createIntent"
const val GETDATA_METHOD = "getArguments"

const val ACTIVITY_NAME = "android.app.Activity"
const val CONTEXT_NAME = "android.content.Context"
const val FRAGMENT_NAME = "androidx.fragment.app.Fragment"
const val INTENT_NAME = "android.content.Intent"

class KtActivityFactory( processingEnv: ProcessingEnvironment, autoPageData: AotoPageData) :KtFileFactory(processingEnv,autoPageData) {
//    var pageName: String
//    var isEmptyParams = true
//    var isAbstract = true
//    var generateClassName: TypeName
//    var className : String
//    init {
//        this.isEmptyParams = autoPageData.list.size === 0
//        this.isAbstract = autoPageData.clazzData.isAbstract
//        pageName = autoPageData.clazzData.pageName
//        //todo
//        className = SUFFIX+autoPageData.clazzData.simpleName+classnameTemp
//        generateClassName = ClassName(pageName, className)
//    }



    override fun generateCode(){
        val typeBuilder = generateTypeBuilder()
        addFields(typeBuilder)
        if (!isEmptyParams) {
            // add class RequestData
            generateRequestData(typeBuilder)
            // add get request data method
//            createGetDataMethod(typeBuilder)
//            typeBuilder!!.addMethod(createGetDataMethod())
//            // add get ArgsData
//            typeBuilder!!.addMethod(createGetArgsDataMethod())
//            //add inject
//            typeBuilder!!.addMethod(createInject())
            //add inject
//            createInject(typeBuilder)
        }
        createCompanion(typeBuilder)
        createIntentMethod(typeBuilder)
        if (!isAbstract) {
            // add start activity method
            createStartMethod(typeBuilder)
        }
        build(typeBuilder.build())

    }

    override fun companionObjectBuilder(): TypeSpec.Builder {
        return if (!isEmptyParams) {
            super.companionObjectBuilder().addFunction(
                createInject()
            ).addFunction(
                createGetDataMethod()
            )
        }else {
            super.companionObjectBuilder()
        }

    }

    private fun createInject(): FunSpec{
        val intent = ClassName(pageName,autoPageData.clazzData.simpleName)

        var func = FunSpec.builder(INJECT).addParameter("activity",intent.copy(true)).addCode("""
        if (activity == null) {
            return
        }
        val data = ${GETDATA_METHOD}(activity.intent)
        if(data!=null) {
        
        """.trimIndent())

        for (data in autoPageData.list) {
            func.addCode(
                """
                          if(data.${
                    hasSetData+data.name}) activity.${
                    data.name.toString()
                }=data.${data.name}!!
                
                """.trimIndent()
            )
        }
        func.addCode("}")
            .addAnnotation(JvmStatic::class.java)

        return func.build()
    }

    private fun createGetDataMethod() :FunSpec {
        val intent = StringUtils.generateClassName(INTENT_NAME)
        val requestData = ClassName("",REQUEST_DATA_CLASS)
        val paramsName = "data"
        return FunSpec.builder(GETDATA_METHOD)
            .returns(requestData.copy(true))
            .addParameter(paramsName,intent.copy(true))
            .addCode(
                """
            return data?.getParcelableExtra<${REQUEST_DATA_CLASS}?>(${TAG_FIELD})
            """.trimIndent())
            .addKdoc("receive passed data,get data from intent by tag : $TAG_FIELD")
            .addAnnotation(JvmStatic::class.java)
            .build()
//        /**
//         * receive passed data,get data from intent by tag : TAG
//         */
//        fun getArguments(data: Intent?): ArgsData? {
//            return if (data == null || data.getParcelableExtra<ArgsData?>(TAG) == null) {
//                null
//            } else {
//                data.getParcelableExtra<ArgsData?>(TAG)
//            }
//        }
    }





    private fun createStartMethod(typeBuilder: TypeSpec.Builder) {
        val paramsName = "target"
        val context = StringUtils.generateClassName(CONTEXT_NAME)
        val activity =  StringUtils.generateClassName(ACTIVITY_NAME)
        val fragment = StringUtils.generateClassName(FRAGMENT_NAME)

        val startByContext =
            createStartMethodBuilder(context, paramsName, paramsName)

        typeBuilder.addFunction(
            startByContext
                .addCode(
                    """
            if(target is ${ACTIVITY_NAME}){ 
                target.startActivityForResult(intent,${REQUEST_CODE_FIELD_NAME});
            }
            
            """.trimIndent())
                .addStatement("return this")
                .addKdoc("launcher a Activity by $CONTEXT_NAME")
                .build()
        )
        val startByActivity =
            createStartMethodBuilder(activity, paramsName, paramsName)
        typeBuilder.addFunction(
            startByActivity.addStatement(
                "target.startActivityForResult(intent,${REQUEST_CODE_FIELD_NAME})")
                .addStatement("return this")
                .addKdoc("launcher a Activity by $ACTIVITY_NAME")
                .build()
        )
        val startByFragment = createStartMethodBuilder(
            fragment, paramsName,
            "$paramsName.requireActivity()"
        )
        typeBuilder.addFunction(
            startByFragment.addStatement(
                "target.startActivityForResult(intent,${REQUEST_CODE_FIELD_NAME})")
                .addKdoc("launcher a Activity by $FRAGMENT_NAME")
                .addStatement("return this")
                .build()
        )
    }


    /**
     * 创建startmethod
     */
    private fun createStartMethodBuilder(
        paramsType: TypeName,
        paramsName: String,
        context: String
    ): FunSpec.Builder {
        return FunSpec.builder(START_METHOD)
            .addParameter(paramsName,paramsType)
            .addStatement("val intent = ${CREATE_INTENT}(${context})")
            .returns(generateClassName)
    }

    private fun createIntentMethod(typeBuilder: TypeSpec.Builder) {
        val intent = StringUtils.generateClassName(INTENT_NAME)

        val builder = FunSpec.builder(CREATE_INTENT)
            .addKdoc("Create intent")
            .addParameter("context",StringUtils.generateClassName(CONTEXT_NAME))

        if (!isEmptyParams) {
            builder.addStatement("val intent = Intent(context, ${autoPageData.clazzData.simpleName}::class.java)")
            builder.addStatement("intent.putExtra(${TAG_FIELD}, ${REQUEST_DATA_FIELD_NAME})")
            builder.addStatement("return intent")
        }else{
            builder.addStatement("return Intent(context, ${autoPageData.clazzData.simpleName}::class.java)")
        }
        builder.returns(intent)

        typeBuilder.addFunction(builder.build())
    }


    override fun addFields(typeBuilder: TypeSpec.Builder) {
        super.addFields(typeBuilder)
        typeBuilder.addProperty(PropertySpec.builder(REQUEST_CODE_FIELD_NAME, INT,KModifier.PUBLIC)
            .mutable(true)
            .initializer("-1")
            .addKdoc("request code")
            .build())
    }




}