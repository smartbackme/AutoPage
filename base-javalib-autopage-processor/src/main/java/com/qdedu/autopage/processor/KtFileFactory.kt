package com.qdedu.autopage.processor

import com.qdedu.autopage.processor.utils.StringUtils
import com.squareup.kotlinpoet.*
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment


const val SUFFIX = "Ap"
const val INJECT = "inject"
const val DEFAULT_INSTANCE = "instance"
const val REQUEST_DATA_CLASS = "ArgsData"
const val REQUEST_DATA_FIELD_NAME = "tdata"
const val PARENT_CLASS_FIELD_NAME = "parent"
const val TAG_FIELD = "TAG"
const val INSTANCE_METHOD = "getInstance"
private const val GET_ARGSDATA_METHOD_NAME = "getArgsData"
const val REQUEST_TO_NEW_DATA_NAME = "toNewData"

/**
 * @author shidawei
 * 创建日期：2021/3/17
 * 描述：
 */
abstract class KtFileFactory (val processingEnv: ProcessingEnvironment, val autoPageData: AotoPageData){
    var pageName: String
    var isEmptyParams = true
    var isAbstract = true
    var generateClassName: TypeName
    var className : String
    init {
        this.isEmptyParams = autoPageData.list.size === 0
        this.isAbstract = autoPageData.clazzData.isAbstract
        pageName = autoPageData.clazzData.pageName
        className = SUFFIX+autoPageData.clazzData.simpleName
        generateClassName = ClassName(pageName, className)
    }

    /**
     * 创建基础类
     */
    protected fun generateTypeBuilder(): TypeSpec.Builder {
        return TypeSpec.classBuilder(className).addKdoc("This class is generated by annotation @AutoPage")
    }

    @Throws(IOException::class)
    protected fun build(type: TypeSpec) {
        // 输出kotlin文件到build
        FileSpec.builder(pageName,type.name!!)
            .addType(type)
            .addComment("The file is auto-generate by processorTool,do not modify!")
            .build().writeTo(processingEnv.filer)
    }

    abstract fun generateCode()

    open fun addFields(typeBuilder: TypeSpec.Builder){


        if (!isEmptyParams) {
            val dataClass = ClassName("",REQUEST_DATA_CLASS)
            typeBuilder.addProperty(
                PropertySpec.builder(REQUEST_DATA_FIELD_NAME,dataClass.copy(true), KModifier.PRIVATE)
                .initializer("null")
                .mutable(true)
                .addKdoc("临时存")
                .build())
            typeBuilder.addFunction(
                FunSpec.builder(REQUEST_TO_NEW_DATA_NAME)
//                .addParameter(paramsName,paramsType)
//                .addParameter("d",TypeName)

                    //LambdaTypeName
//                .addParameter(
//                    ParameterSpec.builder("d",
//                        LambdaTypeName.get(dataClass, listOf(), UNIT)).build())
                .addStatement("$REQUEST_DATA_FIELD_NAME = ${REQUEST_DATA_CLASS}()")
                .addStatement("return this")
                .returns(generateClassName).build()
            )
//            public fun toNewData(d : ArgsData.() -> Unit) :ApMainActivity2HelloWorld{
//                data = ArgsData().apply(d)
//                return this
//            }
        }

    }

    open fun companionObjectBuilder() :TypeSpec.Builder{
        return TypeSpec.companionObjectBuilder()
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
                    .addStatement(if(!isEmptyParams)"return ${className}().${REQUEST_TO_NEW_DATA_NAME}()" else "return ${className}()")
                    .returns(generateClassName)
                    .build())
            .addProperty(PropertySpec.builder(TAG_FIELD,String::class, KModifier.PRIVATE)
                .addKdoc("tag是存储标签")
                .initializer("${className}::class.java.canonicalName")
                .build())
    }

    /**
     * 生成伴生对象
     */
    protected fun createCompanion(typeBuilder: TypeSpec.Builder) {
        typeBuilder.addType(companionObjectBuilder().build())
    }

    protected fun generateRequestData(typeBuilder: TypeSpec.Builder) {
        val parcelable = ClassName("android.os","Parcelable")
        val parcelize = ClassName("kotlinx.android.parcel","Parcelize")
        val builder = TypeSpec.classBuilder(REQUEST_DATA_CLASS)
            .addKdoc("inner class RequestData,contains all of the field define by annotation @Field")
            .addSuperinterface(parcelable)
            .addAnnotation(parcelize)


        var func = FunSpec.constructorBuilder()




        val fieldDatas: List<AotoPageFieldData> = autoPageData.list
        for (i in fieldDatas.indices) {
            val data: AotoPageFieldData = fieldDatas[i]
            createProperty(builder,func,data)
            createPropertyHasSetData(builder,func,data)
            createSetRequestBuilder(typeBuilder,data)
        }


        builder.primaryConstructor(func.build())


//        builder.primaryConstructor(FunSpec.constructorBuilder().addParameter(
//            ParameterSpec.builder("kk", STRING.copy(true)).defaultValue("null") .build()
//        ).addParameter(
//            ParameterSpec.builder("kk3", STRING).build()
//        ).build())
        typeBuilder.addType(builder.build())
    }

     fun createSetRequestBuilder(typeBuilder: TypeSpec.Builder, data: AotoPageFieldData){
         typeBuilder.addProperty(PropertySpec.builder(data.name,
             StringUtils.generateClassName(data.fieldType).copy(true))
             .initializer("null")
             .mutable(true)
            .setter(FunSpec.setterBuilder()
                .addParameter("value", StringUtils.generateClassName(data.fieldType))
                .addCode("""
                ${REQUEST_DATA_FIELD_NAME}!!.${hasSetData+data.name} = true
                ${REQUEST_DATA_FIELD_NAME}!!.${data.name} = value
            """.trimIndent())
                .build())
             .addKdoc(data.doc)
             .build())
     }

    //    /**
//     * inner class RequestData,contains all of the field define by annotation @Field
//     */
//    @Parcelize
//    class ArgsData(var hasSetData_message: Boolean = false,
//                   private var message: String? = null) : Parcelable {
//
//        fun setMessage(message: String?){
//            hasSetData_message = true
//            this.message = message
//        }
//
//        fun getMessage():String?{
//            return message
//        }
//
//
//    }
    private fun createPropertyHasSetData(typeBuilder: TypeSpec.Builder,func :FunSpec.Builder,data: AotoPageFieldData) {




        func.addParameter(ParameterSpec.builder(hasSetData+data.name, BOOLEAN)
            .defaultValue("false")
            .build())

    typeBuilder.addProperty(PropertySpec.builder(hasSetData+data.name, BOOLEAN)
        .initializer(hasSetData+data.name)
        .mutable(true)
        .addKdoc("create from ${data.name}")
        .build())
    }



    private fun createProperty(typeBuilder: TypeSpec.Builder,func :FunSpec.Builder,data: AotoPageFieldData) {


        func.addParameter(ParameterSpec.builder(data.name,
            StringUtils.generateClassName(data.fieldType).copy(true))
            .defaultValue("null")
            .build())
////            .setter(FunSpec.setterBuilder()
////                .addParameter("value", StringUtils.generateClassName(data.fieldType))
////                .addCode("""
////                ${hasSetData+data.name} = true
////                field = value
////            """.trimIndent())
////                .build())
//            .addKdoc(data.doc)
//            .build()

        typeBuilder.addProperty(PropertySpec.builder(data.name,
            StringUtils.generateClassName(data.fieldType).copy(true))
            .initializer(data.name)
            .mutable(true)
//            .setter(FunSpec.setterBuilder()
//                .addParameter("value", StringUtils.generateClassName(data.fieldType))
//                .addCode("""
//                ${hasSetData+data.name} = true
//                field = value
//            """.trimIndent())
//                .build())
            .addKdoc(data.doc)
            .build())
    }



}