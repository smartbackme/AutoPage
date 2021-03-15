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

const val REQUEST_CODE_FIELD_NAME = "requestCode"

const val START_METHOD = "start"
const val CREATE_INTENT = "createIntent"
const val GETDATA_METHOD = "getArguments"

const val ACTIVITY_NAME = "android.app.Activity"
const val CONTEXT_NAME = "android.content.Context"
const val FRAGMENT_NAME = "androidx.fragment.app.Fragment"
const val INTENT_NAME = "android.content.Intent"


class ActivityFactory(processingEnv: ProcessingEnvironment, autoPageData: AotoPageData) :FileFactory(
    processingEnv,
    autoPageData
) {

    override fun generateCode() {
        val typeBuilder = generateTypeBuilder()
        // add field
        // add field
        addFields(typeBuilder!!)

        if (!isEmptyParams) {
            // add class RequestData
            typeBuilder!!.addType(generateRequestData())
            // add get request data method
            typeBuilder!!.addMethod(createGetDataMethod())
            // add get ArgsData
            typeBuilder!!.addMethod(createGetArgsDataMethod())
            //add inject
            typeBuilder!!.addMethod(createInject())
        }
        // create private constructor method
        // create private constructor method
        typeBuilder!!.addMethod(createPrivateConstructor())
        // add create method
        // add create method
        typeBuilder!!.addMethod(createMethod())
        // add setter method
        // add setter method
        addParamsSetMethod(typeBuilder!!)
        // add request code method
        // add request code method
        addRequestCodeMethod(typeBuilder!!)
        // add create intent method
        // add create intent method
        addCreateIntentMethod(typeBuilder!!)
        if (!isAbstract) {
            // add start activity method
            addStartMethod(typeBuilder!!)
        }
        build(typeBuilder!!)
    }

    private fun createInject(): MethodSpec? {
        val intent = getTypeName(autoPageData.clazzData.simpleName)
        val builder = MethodSpec.methodBuilder(INJECT)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(intent, "activity")
            .beginControlFlow("if(activity==null)")
            .addStatement("return")
            .endControlFlow()
        builder.addCode("ArgsData data=getArguments(activity.getIntent());\n")
        for (data in autoPageData.list) {
            builder.addCode(
                """
                activity.${
                    data.name.toString()
                }=data.${StringUtils.getGetMethodName(data.name).toString()}();
                
                """.trimIndent()
            )
        }
        return builder.build()
    }


    private fun createGetDataMethod(): MethodSpec? {
        val intent = getTypeName(INTENT_NAME)
        val requestData = getTypeName(REQUEST_DATA_CLASS)
        val paramsName = "data"
        return MethodSpec.methodBuilder(GETDATA_METHOD)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(getTypeName(REQUEST_DATA_CLASS))
            .addParameter(intent, paramsName)
            .beginControlFlow("if (data == null || data.getSerializableExtra(TAG) == null)")
            .addStatement("return new \$T()", requestData)
            .endControlFlow()
            .addJavadoc("receive passed data,get data from intent by tag : \$L", TAG_FIELD)
            .beginControlFlow("else")
            .addStatement("return (\$T) data.getSerializableExtra(TAG)", requestData)
            .endControlFlow()
            .build()
    }

    private fun addCreateIntentMethod(typeBuilder: TypeSpec.Builder) {
        val intent = getTypeName(INTENT_NAME)
        val builder = MethodSpec.methodBuilder(CREATE_INTENT)
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("Create intent,put the instance of RequestData into it,and parent class request data")
            .returns(getTypeName(INTENT_NAME))
            .addParameter(getTypeName(CONTEXT_NAME), "context")
            .addStatement(
                "\$T intent = new \$T(\$L,\$L.class)",
                intent, intent, "context", autoPageData.clazzData.simpleName
            )
        if (!isEmptyParams) {
            builder.addStatement("intent.putExtra(\$L,\$L)", TAG_FIELD, REQUEST_DATA_FIELD_NAME)
        }
        builder.addStatement("return intent")
        typeBuilder.addMethod(builder.build())
    }

    private fun addStartMethod(typeBuilder: TypeSpec.Builder) {
        val paramsName = "target"
        val startByContext =
            createStartMethodBuilder(CONTEXT_NAME, paramsName, paramsName)
        typeBuilder.addMethod(
            startByContext
                .addCode(
                    """
            if(target instanceof ${ACTIVITY_NAME}){ 
            ((${ACTIVITY_NAME})target).startActivityForResult(intent,${"$"}L);
            }
            
            """.trimIndent(), REQUEST_CODE_FIELD_NAME
                )
                .addStatement("return this")
                .addJavadoc("launcher a Activity by \$L", CONTEXT_NAME)
                .build()
        )
        val startByActivity =
            createStartMethodBuilder(ACTIVITY_NAME, paramsName, paramsName)
        typeBuilder.addMethod(
            startByActivity.addStatement(
                "target.startActivityForResult(intent,\$L)",
                REQUEST_CODE_FIELD_NAME
            )
                .addStatement("return this")
                .addJavadoc("launcher a Activity by \$L", ACTIVITY_NAME)
                .build()
        )
        val startByFragment = createStartMethodBuilder(
            FRAGMENT_NAME, paramsName,
            "$paramsName.getActivity()"
        )
        typeBuilder.addMethod(
            startByFragment.addStatement(
                "target.startActivityForResult(intent,\$L)",
                REQUEST_CODE_FIELD_NAME
            )
                .addJavadoc("launcher a Activity by \$L", FRAGMENT_NAME)
                .addStatement("return this")
                .build()
        )
//        val startByV4Fragment = createStartMethodBuilder(
//            V4FRAGMENT_NAME, paramsName,
//            "$paramsName.getActivity()"
//        )
//        typeBuilder.addMethod(
//            startByV4Fragment.addStatement(
//                "target.startActivityForResult(intent,\$L)",
//                REQUEST_CODE_FIELD_NAME
//            )
//                .addJavadoc("launcher a Activity by \$L", V4FRAGMENT_NAME)
//                .addStatement("return this")
//                .build()
//        )
    }

    private fun createStartMethodBuilder(
        paramsType: String,
        paramsName: String,
        context: String
    ): MethodSpec.Builder {
        val intent = getTypeName(INTENT_NAME)
        return MethodSpec.methodBuilder(START_METHOD)
            .addModifiers(Modifier.PUBLIC)
            .returns(generateClassName)
            .addParameter(getTypeName(paramsType), paramsName)
            .addStatement("\$T intent = \$L(\$L)", intent, CREATE_INTENT, context)
    }

    private fun addRequestCodeMethod(typeBuilder: TypeSpec.Builder) {
        val build = MethodSpec.methodBuilder(REQUEST_CODE_FIELD_NAME)
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("Set request code,use -1 if not defined")
            .returns(generateClassName)
            .addParameter(TypeName.INT, REQUEST_CODE_FIELD_NAME)
            .addStatement(
                "this.\$L = \$L",
                REQUEST_CODE_FIELD_NAME,
                REQUEST_CODE_FIELD_NAME
            )
            .addStatement("return this")
            .build()
        typeBuilder.addMethod(build)
    }


    private fun addFields(typeBuilder: TypeSpec.Builder) {
        //
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
        // add tag
        typeBuilder.addField(
            FieldSpec.builder(
                TypeName.get(String::class.java),
                TAG_FIELD,
                Modifier.PRIVATE,
                Modifier.FINAL,
                Modifier.STATIC
            )
                .addJavadoc("The tag to pass data")
                .initializer("\$L.class.getCanonicalName()", autoPageData.clazzData.simpleName)
                .build()
        )
        if (!isEmptyParams) {
            // add RequestData filed
            typeBuilder.addField(
                FieldSpec.builder(
                    getTypeName(REQUEST_DATA_CLASS),
                    REQUEST_DATA_FIELD_NAME,
                    Modifier.PRIVATE
                )
                    .addJavadoc("The instance of RequestData that is the container of whole filed")
                    .build()
            )
        }
        // add request code field
        typeBuilder.addField(
            FieldSpec.builder(
                TypeName.INT,
                REQUEST_CODE_FIELD_NAME,
                Modifier.PRIVATE
            )
                .initializer("-1")
                .build()
        )
    }

    /**
     * create generate class builder
     */
    private fun generateTypeBuilder(): TypeSpec.Builder? {
        var clzName: String = autoPageData.clazzData.simpleName
        clzName = SUFFIX + clzName
        return TypeSpec.classBuilder(clzName)
            .addJavadoc("This class is generated by annotation @AutoPage")
            .addModifiers(Modifier.PUBLIC)
    }
}