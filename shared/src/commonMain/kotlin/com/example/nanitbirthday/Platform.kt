package com.example.nanitbirthday

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform