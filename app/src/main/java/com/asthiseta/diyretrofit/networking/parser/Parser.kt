package com.asthiseta.diyretrofit.networking.parser

import android.util.Log
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.asthiseta.diyretrofit.networking.client.Client
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType

interface Parser {
    fun <T> parse(jsonString: String, clazz: Class<T>): T?
}


@Suppress("UNCHECKED_CAST")
class JsonParser : Parser {
    override fun <T> parse(jsonString: String, clazz: Class<T>): T? {

        val isRawList = clazz.isAssignableFrom(List::class.java)
        val jsonObject =
            if (isRawList) {

                // If jsonString starts with '[', it indicates it's an array, so wrap it in a JSONObject
                val jsonArray = JSONArray(jsonString)
                JSONObject().apply { put("arrayData", jsonArray) }
            } else {
                JSONObject(jsonString)
            }

        @Suppress("IMPLICIT_CAST_TO_ANY")
        val instance =
            if (isRawList) ListHolder::class.java.newInstance() else clazz.newInstance()
        val declaredFields =
            if (isRawList) ListHolder::class.java.declaredFields else clazz.declaredFields

        for (field in declaredFields) {
            field.isAccessible = true
            val fieldName = field.name
            val fieldValue = jsonObject.opt(fieldName)
            if (fieldValue != null) {
                when (field.type) {
                    List::class.java -> {

                        // Handle list type
                        val genericType = field.genericType as ParameterizedType
                        val listType = genericType.actualTypeArguments[0] as Class<*>
                        val itemList = parseList(fieldValue.toString(), listType)
                        field.set(instance, itemList)
                    }

                    JSONObject::class.java -> {
                        // Handle JSONObject type
                        val item = parse(fieldValue.toString(), field.type)
                        field.set(instance, item)
                    }

                    else -> {
                        field.set(instance, fieldValue)
                    }
                }
            }
        }

        val returnInstance =
            if (isRawList) (instance as ListHolder<T>).arrayData else instance as T?
        return returnInstance
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
