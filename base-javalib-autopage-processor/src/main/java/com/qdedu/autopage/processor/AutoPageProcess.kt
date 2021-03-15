package com.qdedu.autopage.processor

import com.google.auto.service.AutoService
import com.qdedu.autopage.AutoPage
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * @author shidawei
 * 创建日期：2021/3/12
 * 描述：
 */
@AutoService(Processor::class)
class AutoPageProcess : AbstractProcessor() {
    private var isFirst = false

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
    }
    /**
     *该方法用于表明该处理器处理那些注解
     */
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(AutoPage::class.java.canonicalName)
    }

    /**
     *该方法用于处理注解，并生成代码
     */
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "-----------------------------")
        if (isFirst) {
            return false
        }
        isFirst = true
        roundEnv?.let {
            //获取所有被AutoPage注解的元素
            val elememts: Set<Element> = roundEnv.getElementsAnnotatedWith(
                AutoPage::class.java
            )
            /**
             * 操作数据与生成代码
             */
            for (ele in elememts) {
                if (ele.kind == ElementKind.FIELD) {
                    EleParser.instance.parser(processingEnv, ele, false)
                } else if (ele.kind == ElementKind.CLASS) {
                    EleParser.instance.parser(processingEnv, ele, true)
                }
            }
            EleParser.instance.build()
            return true
        }
        return false
    }



}

