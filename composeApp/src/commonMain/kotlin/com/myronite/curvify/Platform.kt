package com.myronite.curvify

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform