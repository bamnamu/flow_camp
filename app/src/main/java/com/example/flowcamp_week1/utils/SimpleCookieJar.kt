package com.example.flowcamp_week1.utils

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SimpleCookieJar : CookieJar {
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = HashMap()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        if (!cookieStore.containsKey(host)) {
            cookieStore[host] = mutableListOf()
        }
        cookieStore[host]?.addAll(cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        return cookieStore[host] ?: emptyList()
    }
}
