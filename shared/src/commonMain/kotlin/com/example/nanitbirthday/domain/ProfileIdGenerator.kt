package com.example.nanitbirthday.domain

import kotlin.uuid.Uuid

fun interface ProfileIdGenerator {
    fun next(): String
}

class UuidProfileIdGenerator : ProfileIdGenerator {
    override fun next(): String = Uuid.random().toString()
}
