package com.qdedu.autopage

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：自动参数注解
 * TYPE——接口、类、枚举、注解
 * FIELD——字段、枚举的常量
 * AnnotationRetention.BINARY 编译时保留
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.BINARY)
annotation class AutoPage