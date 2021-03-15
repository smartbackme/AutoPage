package com.qdedu.autopage

import android.app.Activity
import androidx.fragment.app.Fragment
import java.lang.reflect.Method

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：自动跳转工具
 */
const val SUFFIX = "Ap"

object AutoJ {

    fun inject(activity: Activity) {
        try {
            val clazz: Class<*> = activity.javaClass
            val injectorClazz = Class.forName(clazz.`package`.name +"." + SUFFIX + clazz.simpleName)
            val method: Method = injectorClazz.getMethod("inject", clazz)
            method.invoke(null,activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun inject(fragment: Fragment) {
        try {
            val clazz: Class<*> = fragment.javaClass
            val injectorClazz = Class.forName(clazz.`package`.name +"." + SUFFIX + clazz.simpleName)
            val method: Method = injectorClazz.getMethod("inject", clazz)
            method.invoke(null,fragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}