package com.asthiseta.diyretrofit.networking.client

import com.asthiseta.diyretrofit.networking.parser.Parser
import java.net.HttpURLConnection
import java.net.URL

class Client {
    companion object {
        const val POST = "POST"
        const val GET = "GET"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
        fun isSuccessFull(code : Int) : Boolean {
            return code == 201 || code == 200
        }

    }

    private var httpURLConnection: HttpURLConnection? = null
    var innerParser : Parser? = null
    var url: URL? = null
    private var defaultRequestContent = "application/json"
    private var defaultRequestProperty = "Content-Type"

    inner class Builder {
        fun setUrl(url: String): Builder {
            this@Client.url = URL(url)
            return this
        }

        fun setParser(parser: Parser): Builder {
            innerParser = parser
            return this
        }
        fun build(): Client {
            val client = Client()
            return client
        }

    }

    fun <T> enqueue(endpoint:String, method: String, callback : ConnectionCalllback<T>) {
        Thread {
            try {
                val new_url = URL(url.toString() + endpoint)
                httpURLConnection = new_url.openConnection() as HttpURLConnection
                httpURLConnection?.requestMethod = method
                httpURLConnection?.setRequestProperty(defaultRequestProperty, defaultRequestContent)
                httpURLConnection?.doInput = true
                httpURLConnection?.doOutput = true
                httpURLConnection?.connect()
                val responseCode = httpURLConnection?.responseCode
                if (isSuccessFull(responseCode!!)) {
                    val inputStream = httpURLConnection?.inputStream
                    val response = inputStream?.bufferedReader().use { it?.readText() }
                    callback.onSuccess(response!!)
                } else {
                    callback.onError("Error")
                }
            } catch (e: Exception) {
                callback.onError(e.message!!)
            } finally {
                httpURLConnection?.disconnect()
            }
        }.start()
    }

}