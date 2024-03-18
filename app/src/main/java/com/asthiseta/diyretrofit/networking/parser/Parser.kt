package com.asthiseta.diyretrofit.networking.parser

import org.json.JSONArray
import org.json.JSONObject

interface Parser {
    fun <T> parse(json: String, clazz: Class<T>): T

}


class JsonParser : Parser {
    override fun <T> parse(json: String, clazz: Class<T>): T {
        return if (List::class.java.isAssignableFrom(clazz)) {
            parseList(json, clazz)
        } else {
            parseObject(json, clazz)
        }
    }

    private fun <T> parseObject(json: String, clazz: Class<T>): T {
        val jsonObject = JSONObject(json)
        val constructor = clazz.getDeclaredConstructor()
        val instance = constructor.newInstance()

        for (field in clazz.declaredFields) {
            field.isAccessible = true
            val fieldName = field.name
            if (jsonObject.has(fieldName)) {
                val value = jsonObject[fieldName]
                // Handle nested objects recursively
                if (field.type.isAssignableFrom(JSONObject::class.java)) {
                    field.set(instance, value)
                } else {
                    field.set(instance, value)
                }
            }
        }
        return instance
    }

    private fun <T> parseList(json: String, clazz: Class<T>): T {
        val jsonArray = JSONArray(json)
        val list = mutableListOf<Any>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray[i]
            // Handle nested objects recursively
            if (item is JSONObject) {
                val nestedObject = parseObject(item.toString(), clazz)
                list.add(nestedObject!!)
            } else {
                list.add(item)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return list as T
    }
}
