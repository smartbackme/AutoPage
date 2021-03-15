package com.qdedu.autopage.processor

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：
 */

class ClazzData(var isAbstract: Boolean = false, var pageName: String, var simpleName: String)

class AotoPageData(
    var list: List<AotoPageFieldData>,
    var clzName: String,
    var elementType: String,
    var clazzData: ClazzData
)

class AotoPageFieldData ( var name: String, var doc: String, var fieldType: String )
