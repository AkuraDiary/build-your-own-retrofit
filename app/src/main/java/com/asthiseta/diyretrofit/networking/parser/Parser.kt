package com.asthiseta.diyretrofit.networking.parser

import org.json.JSONObject

interface Parser {
    fun <T> parse(json: String, clazz: Class<T>): T

}

class JsonParser : Parser {
    override fun <T> parse(json: String, clazz: Class<T>): T {
        val jsonObject = JSONObject(json)
        val constructor = clazz.getDeclaredConstructor()
        val instance = constructor.newInstance()

        for (field in clazz.declaredFields) {
            field.isAccessible = true
            val fieldName = field.name
            if (jsonObject.has(fieldName)) {
                val value = jsonObject[fieldName]
                field.set(instance, value)
            }
        }
        return instance
    }
}