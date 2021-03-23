package com.qdedu.autopage.processor

import com.qdedu.autopage.processor.utils.StringUtils
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier

/**
 * @author shidawei
 * 创建日期：2021/3/17
 * 描述：
 */
const val BUILD_METHOD = "build"
const val GET_DATA_METHOD = "getArguments"
const val CREATE_BUNDLE_NAME = "createBundle"
const val BUNDLE_NAME = "android.os.Bundle"
class KtFragmentFactory(processingEnv: ProcessingEnvironment, autoPageData: AotoPageData):KtFileFactory(processingEnv,autoPageData) {

    override fun generateCode(){
        val typeBuilder = generateTypeBuilder()
        addFields(typeBuilder)
        createCompanion(typeBuilder)
        createBundle(typeBuilder)
        if (!isEmptyParams) {
            generateRequestData(typeBuilder)
//            createGetDataMethod(typeBuilder)
//            createInject(typeBuilder)
        }
        if (!isAbstract) {
            // add start activity method
            buildMethod(typeBuilder!!)
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


    private fun createInject() : FunSpec{
        val intent = ClassName(pageName,autoPageData.clazzData.simpleName)

        var func = FunSpec.builder(INJECT).addParameter("fragment",intent.copy(true)).addCode("""
        if (fragment == null) {
            return
        }
        val data = ${GET_DATA_METHOD}(fragment)
        if(data!=null) {
        
        """.trimIndent())

        for (data in autoPageData.list) {
            func.addCode(
                """
                          if(data.${
                    hasSetData+data.name}) fragment.${
                    data.name.toString()
                }=data.${data.name}!!
                
                """.trimIndent()
            )
        }


        func.addCode("}").addAnnotation(JvmStatic::class.java)
        return (func.build())
    }


    private fun createGetDataMethod() : FunSpec{
        val typeName = StringUtils.generateClassName(autoPageData.clzName)
        val requestData = ClassName("",REQUEST_DATA_CLASS)
        val params = "target"
        return (FunSpec.builder(GET_DATA_METHOD)
            .returns(requestData.copy(true))
            .addParameter(params,typeName)
            .addCode(
                """
            return target.arguments?.getParcelable<${REQUEST_DATA_CLASS}?>(${TAG_FIELD})
            """.trimIndent())
            .build())

    }

    private fun createBundle(typeBuilder: TypeSpec.Builder){
        val bundle = StringUtils.generateClassName(BUNDLE_NAME)

        val builder = FunSpec.builder(CREATE_BUNDLE_NAME)
            .addKdoc("Create bundle")
        if (!isEmptyParams) {
            builder.addStatement("val bundle = Bundle()")
            builder.addStatement("bundle.putParcelable(${TAG_FIELD}, ${REQUEST_DATA_FIELD_NAME})")
            builder.addStatement("return bundle")
        }else{
            builder.addStatement("return Bundle()")
        }
        builder.returns(bundle)
        typeBuilder.addFunction(builder.build())
    }

    private fun buildMethod(typeBuilder: TypeSpec.Builder){
        val clz = StringUtils.generateClassName(autoPageData.clzName)
        typeBuilder.addFunction(FunSpec.builder(BUILD_METHOD)
            .addKdoc("build fragment instance of $clz")
            .addStatement("val instance = ${autoPageData.clazzData.simpleName}()")
            .addStatement("val bundle = ${CREATE_BUNDLE_NAME}()")
            .addStatement("instance.arguments = bundle")
            .addStatement("return instance")
            .returns(clz).build())
    }

//    private fun buildMethod(): MethodSpec? {
//        val bundle = getTypeName(BUNDLE_NAME)
//        val clz = getTypeName(autoPageData.clzName)
//        val builder = MethodSpec.methodBuilder(BUILD_METHOD)
//            .addModifiers(Modifier.PUBLIC)
//            .addJavadoc("build fragment instance of \$L", clz)
//            .addStatement("\$T instance = new \$T()", clz, clz)
//            .addStatement(
//                "\$T bundle = \$L()",
//                bundle,
//                CREATE_BUNDLE_NAME
//            )
//            .addStatement("instance.setArguments(bundle)")
//            .addStatement("return instance")
//        return builder.build()
//    }


//    private val TAG: String = com.qdedu.autopage.ApMainActivity2HelloWorld::class.java.canonicalName
//
//    @JvmStatic
//    public fun getArguments(target: FragmentSimpleFragment): ArgsData? =
//        target.arguments?.getParcelable<ArgsData?>(TAG)
//
//    @JvmStatic
//    public fun inject(fragment: FragmentSimpleFragment?): Unit {
//        if (fragment == null) {
//            return
//        }
//        val data = getArguments(fragment)
//        if(data!=null) {
//            if(data.hasSetData_message) fragment.message=data.message
//        }
//    }

}