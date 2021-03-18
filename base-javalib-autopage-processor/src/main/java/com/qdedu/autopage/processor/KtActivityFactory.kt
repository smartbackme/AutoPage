package com.qdedu.autopage.processor

import com.qdedu.autopage.processor.utils.StringUtils
import com.squareup.javapoet.MethodSpec
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

const val classnameTemp = "HelloWorld"
const val hasSetData = "hasSetData_"
const val REQUEST_TO_NEW_DATA_NAME = "toNewData"

//const val INTENT = "Intent"
//const val CONTENT_PACKAGE = "android.content"
//const val CONTEXT = "Context"
//private const val ACTIVITY = "Activity"
//private const val FRAGMENT = "Fragment"
//const val FRAGMENT_PACKAGE = "androidx.fragment.app"
//const val ACTIVITY_PACKAGE = "android.app"

class KtActivityFactory(val processingEnv: ProcessingEnvironment,val autoPageData: AotoPageData) {
    var pageName: String
    var isEmptyParams = true
    var isAbstract = true
    var generateClassName: TypeName
    var className : String
    init {
        this.isEmptyParams = autoPageData.list.size === 0
        this.isAbstract = autoPageData.clazzData.isAbstract
        pageName = autoPageData.clazzData.pageName
        //todo
        className = SUFFIX+autoPageData.clazzData.simpleName+classnameTemp
        generateClassName = ClassName(pageName, className)
    }

    /**
     * 创建基础类
     */
    private fun generateTypeBuilder(): TypeSpec.Builder {
        return TypeSpec.classBuilder(className).addKdoc("This class is generated by annotation @AutoPage")
    }

    fun generateCode(){
        val typeBuilder = generateTypeBuilder()
        addFields(typeBuilder)
        if (!isEmptyParams) {
            // add class RequestData
            generateRequestData(typeBuilder)
            // add get request data method
            createGetDataMethod(typeBuilder)
//            typeBuilder!!.addMethod(createGetDataMethod())
//            // add get ArgsData
//            typeBuilder!!.addMethod(createGetArgsDataMethod())
//            //add inject
//            typeBuilder!!.addMethod(createInject())
            //add inject
            createInject(typeBuilder)
        }
        createCompanion(typeBuilder)
        createIntentMethod(typeBuilder)
        if (!isAbstract) {
            // add start activity method
            createStartMethod(typeBuilder)
        }
        build(typeBuilder.build())

    }

    private fun createInject(typeBuilder: TypeSpec.Builder) {
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
                }=data.${data.name}
                
                """.trimIndent()
            )
        }
        func.addCode("}")
        typeBuilder.addFunction(func.build())
    }

    private fun createGetDataMethod(typeBuilder: TypeSpec.Builder) {
        val intent = StringUtils.generateClassName(INTENT_NAME)
        val requestData = ClassName("",REQUEST_DATA_CLASS)
        val paramsName = "data"
        typeBuilder.addFunction(FunSpec.builder(GETDATA_METHOD)
            .returns(requestData.copy(true))
            .addParameter(paramsName,intent.copy(true))
            .addCode(
                """
            return if (${paramsName} == null || data.getParcelableExtra<${REQUEST_DATA_CLASS}?>(${TAG_FIELD}) == null) {
                null
            } else {
                data.getParcelableExtra<${REQUEST_DATA_CLASS}?>(${TAG_FIELD})
            }
            """.trimIndent())
            .addKdoc("receive passed data,get data from intent by tag : $TAG_FIELD")
            .build())
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

    private fun generateRequestData(typeBuilder: TypeSpec.Builder) {
        val parcelable = ClassName("android.os","Parcelable")
        val parcelize = ClassName("kotlinx.android.parcel","Parcelize")
        val builder = TypeSpec.classBuilder(REQUEST_DATA_CLASS)
            .addKdoc("inner class RequestData,contains all of the field define by annotation @Field")
            .addSuperinterface(parcelable)
            .addAnnotation(parcelize)
        val fieldDatas: List<AotoPageFieldData> = autoPageData.list
        for (i in fieldDatas.indices) {
            val data: AotoPageFieldData = fieldDatas[i]
            builder.addProperty(createProperty(data))
            builder.addProperty(createPropertyHasSetData(data))
        }
        typeBuilder.addType(builder.build())
    }

    private fun createPropertyHasSetData(data: AotoPageFieldData): PropertySpec {
        return PropertySpec.builder(hasSetData+data.name, BOOLEAN,KModifier.PUBLIC)
            .initializer("false")
            .mutable(true)
            .addKdoc("create from ${data.name}")
            .build()
    }



    private fun createProperty(data: AotoPageFieldData): PropertySpec {
        return PropertySpec.builder(data.name,StringUtils.generateClassName(data.fieldType).copy(true),KModifier.PUBLIC)
            .initializer("null")
            .mutable(true)
            .setter(FunSpec.setterBuilder()
                .addParameter("value", StringUtils.generateClassName(data.fieldType))
                .addCode("""
                ${hasSetData+data.name} = true
                field = value
            """.trimIndent())
                .build())
            .addKdoc(data.doc)
            .build()
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
            builder.addStatement("val intent = Intent(context, ${className}::class.java)")
            builder.addStatement("intent.putExtra(${TAG_FIELD}, ${REQUEST_DATA_FIELD_NAME})")
            builder.addStatement("return intent")
        }else{
            builder.addStatement("return Intent(context, ${className}::class.java)")
        }
        builder.returns(intent)

        typeBuilder.addFunction(builder.build())
    }


    private fun addFields(typeBuilder: TypeSpec.Builder) {

        typeBuilder.addProperty(PropertySpec.builder(TAG_FIELD,String::class,KModifier.PRIVATE)
            .addKdoc("tag是存储标签")
            .initializer("${className}::class.java.canonicalName")
            .build())

        if (!isEmptyParams) {
            val dataClass = ClassName("",REQUEST_DATA_CLASS)
            typeBuilder.addProperty(PropertySpec.builder(REQUEST_DATA_FIELD_NAME,dataClass.copy(true),KModifier.PRIVATE)
                .initializer("null")
                .mutable(true)
                .addKdoc("临时存")
                .build())
            typeBuilder.addFunction(FunSpec.builder(REQUEST_TO_NEW_DATA_NAME)
//                .addParameter(paramsName,paramsType)
//                .addParameter("d",TypeName)
                .addParameter(ParameterSpec.builder("d",LambdaTypeName.get(dataClass, listOf(),UNIT)).build())
                .addStatement("$REQUEST_DATA_FIELD_NAME = ${REQUEST_DATA_CLASS}().apply(d)")
                .addStatement("return this")
                .returns(generateClassName).build()
            )
//            public fun toNewData(d : ArgsData.() -> Unit) :ApMainActivity2HelloWorld{
//                data = ArgsData().apply(d)
//                return this
//            }
        }

        typeBuilder.addProperty(PropertySpec.builder(REQUEST_CODE_FIELD_NAME, INT,KModifier.PUBLIC)
            .mutable(true)
            .initializer("-1")
            .addKdoc("request code")
            .build())
    }


        /**
     * 生成伴生对象
     */
    fun createCompanion(typeBuilder: TypeSpec.Builder) {
            typeBuilder.addType( TypeSpec.companionObjectBuilder()
            .addProperty(
                PropertySpec.builder("instance", generateClassName)
                    .addAnnotation(AnnotationSpec.builder(Deprecated::class.java).addMember("\"单例会随着时间增加内存使用，建议用newInstance\"").build())
                    .addAnnotation(JvmStatic::class.java)
                    .delegate("lazy{ ${className}() }")
                    .addKdoc("单例")
                    .build())
                .addFunction(
                FunSpec.builder("newInstance")
                    .addAnnotation(JvmStatic::class.java)
                    .addKdoc("创建新对象")
                    .addStatement("return ${className}()")
                    .returns(generateClassName)
                    .build())
            .build())
    }

    @Throws(IOException::class)
    open fun build(type: TypeSpec) {
        // 输出kotlin文件到build
        FileSpec.builder(pageName,type.name!!)
            .addType(type)
            .addComment("The file is auto-generate by processorTool,do not modify!")
            .build().writeTo(processingEnv.filer)
    }
}