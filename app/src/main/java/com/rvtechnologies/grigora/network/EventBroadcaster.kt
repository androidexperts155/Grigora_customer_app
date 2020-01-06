package com.rvtechnologies.grigorahq.network

interface EventBroadcaster {
    fun broadcast(code: Int, data: Any?)
}