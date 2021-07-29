package com.indialone.rxjavamultithreadingandadvanced

import java.lang.Exception
import kotlin.random.Random

class NetworkClient {

    private val random = java.util.Random()

    fun fetchUser(username: String): User {
        randomSleep()
        return User(username = username)
    }

    private fun randomSleep() {
        try {
            Thread.sleep((random.nextInt(3) * 1000).toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}