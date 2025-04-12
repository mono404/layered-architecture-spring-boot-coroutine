package com.mono.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackendLayeredApplication

fun main(args: Array<String>) {
    runApplication<BackendLayeredApplication>(*args)
}
