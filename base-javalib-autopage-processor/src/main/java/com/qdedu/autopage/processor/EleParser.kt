package com.qdedu.autopage.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import java.io.IOException
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.tools.Diagnostic
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：
 */

private const val ACT_NAME = "android.app.Activity"
private const val FRAG_NAME = "androidx.fragment.app.Fragment"
private const val SPLIT = "@@"

private const val ACTIVITY = "ACTIVITY"
private const val FRAGMENT = "FRAGMENT"

class EleParser {

    var processingEnv: ProcessingEnvironment? = null
    fun parser(processingEnv: ProcessingEnvironment, element: Element, isClass: Boolean) {
        this.processingEnv = processingEnv
        /**
         * 写代码
         */
        parserAnmation(element, isClass)
    }
    var fieldMap: MutableMap<String, MutableList<VariableElement>> = HashMap()
    var clazzMap: MutableMap<String, ClazzData> = HashMap()

    /**
     * 构建基本数据
     */
    private fun parserAnmation(element: Element, isClass: Boolean) {
        val typeElement = if (!isClass) {
            (element as VariableElement).enclosingElement as TypeElement
        } else {
            element as TypeElement
        }
        var fields: MutableList<VariableElement>? = null
        var key = typeElement.qualifiedName.toString()
        val type = getElementType(typeElement)
        key = key + SPLIT + type
        fields = fieldMap[key]
        if (fields == null) {
            fieldMap[key] = ArrayList<VariableElement>().also { fields = it }
            val data = ClazzData(
                isAbstract(typeElement),
                getPkgName(typeElement),
                typeElement.simpleName.toString()
            )
            clazzMap[key] = data
        }
        if (!isClass) {
            fields!!.add(element as VariableElement)
        }
    }

    private fun getElementType(typeElement: TypeElement): String {
        return when {
            checkIsSubClass(typeElement, ACT_NAME) -> {
                ACTIVITY
            }
            checkIsSubClass(typeElement, FRAG_NAME) -> {
                FRAGMENT
            }
            else -> {
                throw IllegalArgumentException(
                    String.format(
                        "类 %s 必须继承于 %s or %s",
                        typeElement.qualifiedName,
                        ACT_NAME,
                        FRAG_NAME
                    )
                )
            }
        }
    }

    /**
     * 检测是否是该类父类
     */
    private fun checkIsSubClass(typeElement: TypeElement, target: String): Boolean {
        var typeElement: TypeElement? = typeElement
        while (true) {
            if (typeElement == null) {
                return false
            } else if (target == typeElement.qualifiedName.toString()) {
                return true
            }
            typeElement = getParentClass(typeElement)
        }
    }

    private fun getParentClass(child: TypeElement): TypeElement? {
        return processingEnv!!.typeUtils.asElement(child.superclass) as TypeElement?
    }

    private fun isAbstract(typeElement: TypeElement): Boolean {
        val modifiers = typeElement.modifiers
        return modifiers.contains(Modifier.ABSTRACT)
    }

    private fun getPkgName(typeElement: TypeElement): String {
        val pkgElement: PackageElement =
            processingEnv!!.elementUtils.getPackageOf(typeElement)
        return if (pkgElement.isUnnamed) "" else pkgElement.qualifiedName.toString()
    }

    fun build() {
        parserData()
    }

    private fun parserData() {
        for (key in fieldMap.keys) {
            val type: String = key.split(SPLIT).toTypedArray()[1]
            val clzName: String = key.split(SPLIT).toTypedArray()[0]
            val list: List<VariableElement>? = fieldMap[key]
            val qtFieldDatas: MutableList<AotoPageFieldData> = ArrayList<AotoPageFieldData>()
            list?.let {
                for (element in list) {
                    qtFieldDatas.add(AotoPageFieldData(element.simpleName.toString(),"create from AutoPage",element.javaToKotlinType()?.toString() ?: element.asType().toString()))
                }
            }
            clazzMap[key]?.let {
                val qtData = AotoPageData(qtFieldDatas,clzName,type,clazzMap[key]!!)
                try {
                    processingEnv!!.messager.printMessage(Diagnostic.Kind.NOTE, "begin-----------------")
                    if (type == ACTIVITY) {
//                        ActivityFactory(processingEnv!!,qtData).generateCode()
                        KtActivityFactory(processingEnv!!,qtData).generateCode()
                    } else {
//                        FragmentFactory(processingEnv!!,qtData).generateCode()
                        KtFragmentFactory(processingEnv!!,qtData).generateCode()

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }
    }

    companion object {
        val instance: EleParser by lazy {
            EleParser()
        }
    }
}

/**
 * 获取需要把java类型映射成kotlin类型的ClassName  如：java.lang.String 在kotlin中的类型为kotlin.String 如果是空则表示该类型无需进行映射
 */
fun Element.javaToKotlinType(): ClassName? {
    val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(this.asType().toString()))?.asSingleFqName()?.asString()
    return if (className == null) {
        null
    } else {
        ClassName.bestGuess(className)
    }
}