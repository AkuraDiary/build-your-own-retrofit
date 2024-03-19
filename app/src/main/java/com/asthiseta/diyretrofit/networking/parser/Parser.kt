package com.asthiseta.diyretrofit.networking.parser

import com.asthiseta.diyretrofit.networking.client.Client
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType

interface Parser {
    fun <T> parse(jsonString: String, clazz: Class<T>): Any?
}


class JsonParser : Parser {
    override fun <T> parse(jsonString: String, clazz: Class<T>): Any? {
        val jsonObject = JSONObject(jsonString)
        val instance = clazz.newInstance()
        val declaredFields = clazz.declaredFields
        for (field in declaredFields) {
            field.isAccessible = true
            val fieldName = field.name
            val fieldValue = jsonObject.opt(fieldName)
            if (fieldValue != null) {
                if (field.type == List::class.java) {

                    // Handle list type
                    val genericType = field.genericType as ParameterizedType
                    val listType = genericType.actualTypeArguments[0] as Class<*>
                    val itemList = parseList(fieldValue.toString(), listType)
//                    Client.log("$fieldName: $itemList")
                    field.set(instance, itemList)
                } else if (field.type == JSONObject::class.java) {
                    // Handle JSONObject type
                    val item = parse(fieldValue.toString(), field.type)
//                    Client.log("$fieldName: $item")
                    field.set(instance, item)
                } else {
                    // Handle other types
//                    Client.log("$fieldName: $fieldValue")
                    field.set(instance, fieldValue)
                }
            }
        }
        return instance
    }

    private fun parseList(toString: String, listType: Class<*>): Any? {
        val jsonArray = JSONArray(toString)
        val list = mutableListOf<Any>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray[i]
            val parsedItem = parse(item.toString(), listType)
            list.add(parsedItem!!)
        }
        return list
    }


}
