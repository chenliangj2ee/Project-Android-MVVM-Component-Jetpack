package com.chenliang.annotation

/**
 *配置生成器注解
 */

@Target(AnnotationTarget.FIELD)
annotation class MySpConfig(val hasId: Boolean=false)

