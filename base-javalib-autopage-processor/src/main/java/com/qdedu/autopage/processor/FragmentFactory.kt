package com.qdedu.autopage.processor

import com.qdedu.autopage.processor.utils.StringUtils
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：
 */
const val BUILD_METHOD = "build"
const val GET_DATA_METHOD = "getArguments"
const val CREATE_BUNDLE_NAME = "createBundle"

const val BUNDLE_NAME = "android.os.Bundle"
class FragmentFactory(processingEnv: ProcessingEnvironment, autoPageData: AotoPageData) :FileFactory(
    processingEnv,
    autoPageData
){

    override fun generateCode() {
        val typeBuilder = generateTypeBuilder()
        if (!isEmptyParams) {
            // create inner data class method
            typeBuilder!!.addType(generateRequestData())
            // create get data method
            typeBuilder!!.addMethod(createGetDataMethod())
            // add get ArgsData
            typeBuilder!!.addMethod(createGetArgsDataMethod())
            //add inject
            typeBuilder!!.addMethod(createInject())
        }
        // create filed
        // create filed
        createFields(typeBuilder!!)
        // create private constructor method
        // create private constructor method
        typeBuilder!!.addMethod(createPrivateConstructor())
        // add create bundle method
        // add create bundle method
        typeBuilder!!.addMethod(createBundle())
        // add create method
        // add create method
        typeBuilder!!.addMethod(createMethod())
        // create set params method
        // create set params method
        addParamsSetMethod(typeBuilder!!)
        if (!isAbstract) {
            // create build method
            typeBuilder!!.addMethod(buildMethod())
        }
        build(typeBuilder!!)
    }

    private fun createInject(): MethodSpec? {
        val intent = getTypeName(autoPageData.clazzData.simpleName)
        val builder = MethodSpec.methodBuilder(INJECT)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(intent, "fragment")
            .beginControlFlow("if(fragment==null)")
            .addStatement("return")
            .endControlFlow()
        builder.addCode("ArgsData data=getArguments(fragment);\n")
        for (data in autoPageData.list) {
            builder.addCode(
                """
                fragment.${
                    data.name.toString()
                }=data.${StringUtils.getGetMethodName(data.name).toString()}();
                
                """.trimIndent()
            )
        }
        return builder.build()
    }

    private fun createBundle(): MethodSpec? {
        val bundle = getTypeName(BUNDLE_NAME)
        val builder =
            MethodSpec.methodBuilder(CREATE_BUNDLE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .returns(bundle)
                .addStatement("\$T bundle = new \$T()", bundle, bundle)
        if (!isEmptyParams) {
            builder.addStatement(
                "bundle.putSerializable(\$L,\$L)",
                TAG_FIELD,
                REQUEST_DATA_FIELD_NAME
            )
        }
        builder.addStatement("return bundle")
        return builder.build()
    }

    private fun createGetDataMethod(): MethodSpec? {
        val params = "target"
        val typeName = getTypeName(autoPageData.clzName)
        return MethodSpec.methodBuilder(GET_DATA_METHOD)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(getTypeName(REQUEST_DATA_CLASS))
            .addParameter(typeName, params)
            .addStatement(
                "return (\$L)\$L.getArguments().getSerializable(\$L)",
                REQUEST_DATA_CLASS,
                params,
                TAG_FIELD
            )
            .build()
    }

    private fun buildMethod(): MethodSpec? {
        val bundle = getTypeName(BUNDLE_NAME)
        val clz = getTypeName(autoPageData.clzName)
        val builder = MethodSpec.methodBuilder(BUILD_METHOD)
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("build fragment instance of \$L", clz)
            .returns(getTypeName(autoPageData.clzName))
            .addStatement("\$T instance = new \$T()", clz, clz)
            .addStatement(
                "\$T bundle = \$L()",
                bundle,
                CREATE_BUNDLE_NAME
            )
            .addStatement("instance.setArguments(bundle)")
            .addStatement("return instance")
        return builder.build()
    }

    private fun createFields(typeBuilder: TypeSpec.Builder) {
        // add tag
        typeBuilder.addField(
            FieldSpec.builder(
                TypeName.get(String::class.java),
                TAG_FIELD,
                Modifier.PRIVATE,
                Modifier.FINAL,
                Modifier.STATIC
            )
                .initializer("\$L.class.getCanonicalName()", autoPageData.clzName).build()
        )
        if (!isEmptyParams) {
            // add RequestData filed
            typeBuilder.addField(
                FieldSpec.builder(
                    getTypeName(REQUEST_DATA_CLASS),
                    REQUEST_DATA_FIELD_NAME,
                    Modifier.PUBLIC
                ).build()
            )
        }
        var clzName: String = autoPageData.clazzData.simpleName
        clzName = SUFFIX + clzName
        typeBuilder.addField(
            FieldSpec.builder(
                getTypeName(clzName),
                DEFAULT_INSTANCE,
                Modifier.PRIVATE,
                Modifier.STATIC
            ).build()
        )
    }

    /**
     * create generate class builder
     */
    private fun generateTypeBuilder(): TypeSpec.Builder? {
        var clzName: String = autoPageData.clazzData.simpleName
        clzName = SUFFIX + clzName
        return TypeSpec.classBuilder(clzName)
            .addModifiers(Modifier.PUBLIC)
    }
}