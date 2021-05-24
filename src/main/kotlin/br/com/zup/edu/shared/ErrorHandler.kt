package br.com.zup.edu.shared

import io.micronaut.aop.Around

@Around
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorHandler
