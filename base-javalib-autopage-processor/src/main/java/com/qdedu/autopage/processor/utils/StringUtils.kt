package com.qdedu.autopage.processor.utils

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
}