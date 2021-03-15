package com.qdedu.autopage.processor

import com.qdedu.autopage.processor.utils.StringUtils
import com.qdedu.autopage.processor.utils.reflect.Reflect
import com.squareup.javapoet.*
import java.io.IOException
import java.io.Serializable
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：
 */
const val SUFFIX = "Ap"
const val INJECT = "inject"
const val DEFAULT_INSTANCE = "instance"
const val REQUEST_DATA_CLASS = "ArgsData"
const val REQUEST_DATA_FIELD_NAME = "data"
const val PARENT_CLASS_FIELD_NAME = "parent"
const val TAG_FIELD = "TAG"
const val INSTANCE_METHOD = "getInstance"
private const val GET_ARGSDATA_METHOD_NAME = "getArgsData"

abstract class FileFactory(val processingEnv: ProcessingEnvironment, val autoPageData: AotoPageData) {
    var pageName: String
    var isEmptyParams = true
    var isAbstract = true
    var generateClassName: TypeName? = null

    init {
        this.isEmptyParams = autoPageData.list.size === 0
        this.isAbstract = autoPageData.clazzData.isAbstract
        pageName = autoPageData.clazzData.pageName
        generateClassName = getTypeName(getSuffix() + autoPageData.clazzData.simpleName)
    }

    private fun getSuffix() = SUFFIX

    @Throws(IOException::class)
    abstract fun generateCode()

    open fun getTypeName(clzName: String): TypeName {
        return Reflect.on(TypeName::class.java).create(clzName).get()
    }

    /**
     * create inner class RequestData,contains all of the field define by annotation @Field
     */
    open fun generateRequestData(): TypeSpec? {
        val builder: TypeSpec.Builder = TypeSpec.classBuilder(REQUEST_DATA_CLASS)
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .addJavadoc("inner class RequestData,contains all of the field define by annotation @Field")
            .addSuperinterface(TypeName.get(Serializable::class.java))
        val fieldDatas: List<AotoPageFieldData> = autoPageData.list
        for (i in fieldDatas.indices) {
            val data: AotoPageFieldData = fieldDatas[i]
            builder.addField(createField(data))
            builder.addMethod(createSetRequestBuilder(data))
            builder.addMethod(createGetRequestBuilder(data))
        }
        return builder.build()
    }

    open fun createGetRequestBuilder(data: AotoPageFieldData): MethodSpec? {
        val getMethodName: String = StringUtils.getGetMethodName(data.name)
        return MethodSpec.methodBuilder(getMethodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(getTypeName(data.fieldType))
            .addStatement("return this.\$L", data.name)
            .addJavadoc(data.doc)
            .build()
    }

    open fun createSetRequestBuilder(data: AotoPageFieldData): MethodSpec? {
        val setMethodName: String = StringUtils.getSetMethodName(data.name)
        return MethodSpec.methodBuilder(setMethodName)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(getTypeName(data.fieldType), data.name)
            .returns(getTypeName(REQUEST_DATA_CLASS))
            .addStatement("this.\$L = \$L", data.name, data.name)
            .addStatement("return this")
            .addJavadoc(data.doc)
            .build()
    }

    open fun createField(data: AotoPageFieldData): FieldSpec? {
        val builder: FieldSpec.Builder =
            FieldSpec.builder(getTypeName(data.fieldType), data.name, Modifier.PRIVATE)
                .addJavadoc(data.doc)
        return builder.build()
    }

    open fun createGetArgsDataMethod(): MethodSpec? {
        val builder = MethodSpec.methodBuilder(GET_ARGSDATA_METHOD_NAME)
            .addModifiers(Modifier.PUBLIC)
            .returns(getTypeName(REQUEST_DATA_CLASS))
            .addJavadoc("get args you has already set")
            .addStatement("return \$L", REQUEST_DATA_FIELD_NAME)
        return builder.build()
    }

    @Throws(IOException::class)
    open fun build(typeBuilder: TypeSpec.Builder) {
        val javaBuilder = JavaFile.builder(pageName, typeBuilder.build())
        javaBuilder.addFileComment("The file is auto-generate by processorTool,do not modify!")
        javaBuilder.build().writeTo(processingEnv.filer)
    }


    /**
     * generate static create method
     */
    open fun createMethod(): MethodSpec? {
        val createBuilder = MethodSpec.methodBuilder(INSTANCE_METHOD)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .beginControlFlow("if(instance==null)")
            .addStatement("instance = new \$T()", generateClassName)
            .endControlFlow()
        if (!isEmptyParams) {
            createBuilder.addStatement(
                "instance.\$L = new \$L()",
                REQUEST_DATA_FIELD_NAME,
                REQUEST_DATA_CLASS
            )
        }
        return createBuilder.addStatement("return instance")
            .returns(generateClassName)
            .build()
    }

    open fun addParamsSetMethod(typeBuilder: TypeSpec.Builder) {
        val fieldList: List<AotoPageFieldData> = autoPageData.list
        for (i in fieldList.indices) {
            val data: AotoPageFieldData = fieldList[i]
            createSetMethod(data, typeBuilder)
        }
    }

    open fun createParentSetMethod(data: AotoPageFieldData): MethodSpec? {
        val setMethodName: String = StringUtils.getSetMethodName(data.name)
        return MethodSpec.methodBuilder(setMethodName)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(getTypeName(data.fieldType), data.name)
            .returns(generateClassName)
            .addStatement(
                "\$L.\$L(\$L)",
                PARENT_CLASS_FIELD_NAME,
                setMethodName,
                data.name
            )
            .addStatement("return this")
            .addJavadoc("parent argument:" + data.doc)
            .build()
    }

    open fun createSetMethod(data: AotoPageFieldData, typeBuilder: TypeSpec.Builder) {
        val setMethodName: String = StringUtils.getSetMethodName(data.name)
        val realType = getTypeName(data.fieldType)
        val setMethod: MethodSpec = MethodSpec.methodBuilder(setMethodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(generateClassName)
            .addParameter(realType, data.name)
            .addStatement(
                "this.\$L.\$L(\$L)",
                REQUEST_DATA_FIELD_NAME,
                setMethodName,
                data.name
            )
            .addStatement("return this")
            .addJavadoc(data.doc)
            .build()
        typeBuilder.addMethod(setMethod)
    }

    /**
     * create private constructor method
     */
    open fun createPrivateConstructor(): MethodSpec? {
        val builder = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
        return builder.build()
    }
}