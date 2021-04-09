package com.qdedu.autopage.processor.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：
 */
object StringUtils {
    fun isEmpty(data: CharSequence?): Boolean {
        return data == null || data.isEmpty()
    }

    fun getSetMethodName(fieldName: String): String {
        checkName(fieldName)
        val sb = getUpperMethod(fieldName)
        return "set$sb"
    }

    private fun getUpperMethod(fieldName: String): StringBuffer {
        val charArray = fieldName.toCharArray()
        val sb = StringBuffer()
        for (i in charArray.indices) {
            if (i == 0) {
                sb.append(Character.toUpperCase(charArray[i]))
            } else {
                sb.append(charArray[i])
            }
        }
        return sb
    }

    fun getGetMethodName(fieldName: String): String {
        checkName(fieldName)
        val sb = getUpperMethod(fieldName)
        return "get$sb"
    }

    private fun checkName(fieldName: String) {
        require(!isEmpty(fieldName)) { "method name is null" }
    }

    fun getIsMethodName(fieldName: String): String? {
        checkName(fieldName)
        val sb = getUpperMethod(fieldName)
        return "is$sb"
    }

    fun spliteFieldName(methodName: String): String? {
        var methodName = methodName
        if (isEmpty(methodName)) {
            return methodName
        }
        if (methodName.startsWith("is")) {
            methodName = methodName.replaceFirst("is".toRegex(), "")
        } else if (methodName.startsWith("set")) {
            methodName = methodName.replaceFirst("set".toRegex(), "")
        } else if (methodName.startsWith("get")) {
            methodName = methodName.replaceFirst("get".toRegex(), "")
        }
        return getUpperMethod(methodName).toString()
    }

    fun getAddMethodName(name: String): String? {
        checkName(name)
        val sb = getUpperMethod(name)
        return "add$sb"
    }



    fun generateClassName(str: String): TypeName {
        when(str){
            "int[]" -> return ClassName("kotlin","IntArray")
            "long[]" -> return ClassName("kotlin","LongArray")
            "boolean[]" -> return ClassName("kotlin","BooleanArray")
            "double[]" -> return ClassName("kotlin","DoubleArray")
            "float[]" -> return ClassName("kotlin","FloatArray")
            "char[]" -> return ClassName("kotlin","CharArray")
            "byte[]" -> return ClassName("kotlin","ByteArray")
            "short[]" -> return ClassName("kotlin","ShortArray")
            else -> {
                var end = str.indexOf("[]")
                if(end>0){
                    var inner = str.subSequence(0,end).toString()
                    var list =  ClassName("kotlin", "Array")
                    return list.parameterizedBy(innerGenerateClassName(inner))
                }

            }
        }

        if(str.indexOf("ArrayList")>0){
            var start = str.indexOf("<")+1
            var end = str.indexOf(">")
            var inner = str.subSequence(start,end).toString()

            val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(inner))?.asSingleFqName()?.asString()
            var kclassName = if (className == null) {
                innerGenerateClassName(inner)
            } else {
                ClassName.bestGuess(className)
            }

            var list =  ClassName("kotlin.collections","ArrayList")

            return list.parameterizedBy(kclassName)
        }

        return innerGenerateClassName(str)

    }

    private fun innerGenerateClassName(str: String): ClassName {
        val lastIndex = str.lastIndexOf(".")
        return if(lastIndex!=-1){
            val pack = str.substring(0, lastIndex)
            val simpleName = str.substring(lastIndex + 1)
//            print(pack+simpleName)
            ClassName(pack,simpleName)
        }else{
            ClassName("",str)
        }
    }

}

