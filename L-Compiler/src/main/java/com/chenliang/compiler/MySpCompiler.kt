package com.chenliang.compiler

import com.chenliang.annotation.MyApiService
import com.chenliang.annotation.MyRoute
import com.chenliang.annotation.MySpConfig
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.jvm.jvmDefault
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


/**
 *
 * @Project: MVVM-Component
 * @Package: com.chenliang.compiler
 * @author: chenliang
 * @date: 2021/07/28
 */


/**
 * 路由全局自动生成器
 * @property mFiler Filer
 * @property mModuleName String?
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = ["com.chenliang.annotation.MySpConfig"])
class MySpCompiler : AbstractProcessor() {
    private lateinit var mFiler: Filer
    private var mModuleName: String? = null

    init {
//        System.out.println("MySpCompiler Processor Init--------------------------------------------")
    }

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        mFiler = p0!!.filer
        mModuleName = p0.options["AROUTER_MODULE_NAME"]
//        System.out.println("MySpCompiler Processor  mModuleName-------------------------------------------- $mModuleName")
    }

    override fun process(ms: MutableSet<out TypeElement>?, en: RoundEnvironment?): Boolean {

        if (en == null) {
//            return false
        }
        var mySp = TypeSpec.objectBuilder("MySp")
        ms!!.forEach {

            var annos = en!!.getElementsAnnotatedWith(it)
            annos.forEach {
                var anno = it.getAnnotation(MySpConfig::class.java)
                var hasId = anno.hasId
                println(
                    "asType:${
                        it.asType().toString()
                    }----------------------------------------------------------------------------"
                )
                var fieldName = it.simpleName.toString()
                when {
                    it.asType().toString() == "boolean" -> {
                        fieldName = fieldName.replace("is", "")
                        var funName =
                            "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var setFun = FunSpec
                            .builder(funName)

                        if (hasId) {
                            setFun.addParameter("id", String::class)
                            setFun.addParameter(fieldName.toLowerCase(), Boolean::class)
                            setFun.addStatement(
                                "%T.putBoolean(%S+id, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString(),
                            )
                        } else {
                            setFun.addParameter(fieldName.toLowerCase(), Boolean::class)
                            setFun.addStatement(
                                "%T.putBoolean(%S, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }


                        mySp.addFunction(setFun.build())

                        var getFunctionName =
                            "is" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )


                        var getFun = FunSpec
                            .builder(getFunctionName)
                            .returns(Boolean::class)



                        if (hasId) {
                            getFun.addParameter("id", String::class)
                            getFun.addStatement(
                                "return %T.getBoolean(%S+id)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        } else {
                            getFun.addStatement(
                                "return %T.getBoolean(%S)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }
                        mySp.addFunction(getFun.build())


                    }
                    it.asType().toString() == "java.lang.String" -> {
                        fieldName = fieldName.replace("is", "")
                        var funName =
                            "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var setFun = FunSpec
                            .builder(funName)


                        if (hasId) {
                            setFun.addParameter("id", String::class)
                            setFun.addParameter(fieldName.toLowerCase(), String::class)
                            setFun.addStatement(
                                "%T.putString(%S+id, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString(),
                            )
                        } else {
                            setFun.addParameter(fieldName.toLowerCase(), String::class)
                            setFun.addStatement(
                                "%T.putString(%S, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }


                        mySp.addFunction(setFun.build())

                        var getFunctionName =
                            "get" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var getFun = FunSpec
                            .builder(getFunctionName)
                            .returns(String::class)
                        if (hasId) {
                            getFun.addParameter("id", String::class)
                            getFun.addStatement(
                                "return %T.getString(%S+id)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        } else {
                            getFun.addStatement(
                                "return %T.getString(%S)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }


                        mySp.addFunction(getFun.build())

                    }
                    it.asType().toString() == "int" -> {
                        fieldName = fieldName.replace("is", "")
                        var funName =
                            "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var setFun = FunSpec
                            .builder(funName)


                        if (hasId) {
                            setFun.addParameter("id", String::class)
                            setFun.addParameter(fieldName.toLowerCase(), Int::class)
                            setFun.addStatement(
                                "%T.putInt(%S+id, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString(),
                            )
                        } else {
                            setFun.addParameter(fieldName.toLowerCase(), Int::class)
                            setFun.addStatement(
                                "%T.putInt(%S, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }




                        mySp.addFunction(setFun.build())

                        var getFunctionName =
                            "get" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var getFun = FunSpec
                            .builder(getFunctionName)
                            .returns(Int::class)
                        if (hasId) {
                            getFun.addParameter("id", String::class)
                            getFun.addStatement(
                                "return %T.getInt(%S+id)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        } else {
                            getFun.addStatement(
                                "return %T.getInt(%S)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(getFun.build())
                    }
                    it.asType().toString() == "long" -> {
                        fieldName = fieldName.replace("is", "")
                        var funName =
                            "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var setFun = FunSpec
                            .builder(funName)

                        if (hasId) {
                            setFun.addParameter("id", String::class)
                            setFun.addParameter(fieldName.toLowerCase(), Long::class)
                            setFun.addStatement(
                                "%T.putLong(%S+id, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString(),
                            )
                        } else {
                            setFun.addParameter(fieldName.toLowerCase(), Long::class)
                            setFun.addStatement(
                                "%T.putLong(%S, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(setFun.build())

                        var getFunctionName =
                            "get" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var getFun = FunSpec
                            .builder(getFunctionName)
                            .returns(Long::class)

                        if (hasId) {
                            getFun.addParameter("id", String::class)
                            getFun.addStatement(
                                "return %T.getLong(%S+id)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        } else {
                            getFun.addStatement(
                                "return %T.getLong(%S)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(getFun.build())
                    }
                    it.asType().toString() == "float" -> {
                        fieldName = fieldName.replace("is", "")
                        var funName =
                            "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var setFun = FunSpec
                            .builder(funName)

                        if (hasId) {
                            setFun.addParameter("id", String::class)
                            setFun.addParameter(fieldName.toLowerCase(), Float::class)
                            setFun.addStatement(
                                "%T.putFloat(%S+id, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString(),
                            )
                        } else {
                            setFun.addParameter(fieldName.toLowerCase(), Float::class)
                            setFun.addStatement(
                                "%T.putFloat(%S, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(setFun.build())

                        var getFunctionName =
                            "get" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var getFun = FunSpec
                            .builder(getFunctionName)
                            .returns(Float::class)

                        if (hasId) {
                            getFun.addParameter("id", String::class)
                            getFun.addStatement(
                                "return %T.getFloat(%S+id)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        } else {
                            getFun.addStatement(
                                "return %T.getFloat(%S)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(getFun.build())
                    }
                    it.asType().toString() == "double" -> {
                        fieldName = fieldName.replace("is", "")
                        var funName =
                            "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var setFun = FunSpec
                            .builder(funName)

                        if (hasId) {
                            setFun.addParameter("id", String::class)
                            setFun.addParameter(fieldName.toLowerCase(), Double::class)
                            setFun.addStatement(
                                "%T.putDouble(%S+id, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString(),
                            )
                        } else {
                            setFun.addParameter(fieldName.toLowerCase(), Double::class)
                            setFun.addStatement(
                                "%T.putDouble(%S, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(setFun.build())

                        var getFunctionName =
                            "get" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var getFun = FunSpec
                            .builder(getFunctionName)
                            .returns(Double::class)

                        if (hasId) {
                            getFun.addParameter("id", String::class)
                            getFun.addStatement(
                                "return %T.getDouble(%S+id)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        } else {
                            getFun.addStatement(
                                "return %T.getDouble(%S)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(getFun.build())
                    }
                    it.asType().toString() == "java.lang.Object" -> {
                        fieldName = fieldName.replace("is", "")
                        var funName =
                            "set" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var setFun = FunSpec
                            .builder(funName)

                        if (hasId) {
                            setFun.addParameter("id", String::class)
                            setFun.addParameter(fieldName.toLowerCase(), Any::class)
                            setFun.addStatement(
                                "%T.putObject(%S+id, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString(),
                            )
                        } else {
                            setFun.addParameter(fieldName.toLowerCase(), Any::class)
                            setFun.addStatement(
                                "%T.putObject(%S, ${fieldName.toLowerCase()})",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(setFun.build())

                        var getFunctionName =
                            "get" + (fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(
                                1
                            )

                        var getFun = FunSpec
                            .builder(getFunctionName)
                            .returns(Any::class)

                        if (hasId) {
                            getFun.addParameter("id", String::class)
                            getFun.addStatement(
                                "return %T.getObject(%S+id)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        } else {
                            getFun.addStatement(
                                "return %T.getObject(%S)",
                                ClassName("com.mtjk.utils", "MySpUtis"),
                                it.simpleName.toString()
                            )
                        }

                        mySp.addFunction(getFun.build())
                    }
                }
            }


        }
        val file =
            FileSpec.builder("com.chenliang.processor." + mModuleName!!.replace("-", ""), "MySp")
                .addType(mySp.build()).build()

        try {
            file.writeTo(mFiler)
        } catch (e: Exception) {

        }


        return true
    }

}

