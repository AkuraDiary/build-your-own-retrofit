package com.asthiseta.diyretrofit.networking.client

import com.asthiseta.diyretrofit.networking.parser.Parser
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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

    private fun buildQueryString(params: Map<String, String>): String {
        val queryString = StringBuilder()
        queryString.append("?")
        for ((key, value) in params) {
            queryString.append(URLEncoder.encode(key, "UTF-8"))
            queryString.append("=")
            queryString.append(URLEncoder.encode(value, "UTF-8"))
            queryString.append("&")
        }
        queryString.deleteCharAt(queryString.length - 1) // Remove the last '&'
        return queryString.toString()
    }

    fun <T> enqueue(endpoint:String, method: String, requestBody: String? = null,
                    queryParams: Map<String, String>? = null, callback : ConnectionCalllback<T>) {
        Thread {
            try {
                var newUrl = URL(url.toString() + endpoint)
                // Add query parameters to the URL
                if (!queryParams.isNullOrEmpty()) {
                    val queryString = buildQueryString(queryParams)
                    newUrl = URL(newUrl.toString() + queryString)
                }

                httpURLConnection = newUrl.openConnection() as HttpURLConnection
                httpURLConnection?.requestMethod = method
                httpURLConnection?.setRequestProperty(defaultRequestProperty, defaultRequestContent)
                httpURLConnection?.doInput = true
                httpURLConnection?.doOutput = true

                // Set request body if present
                if (requestBody != null) {
                    val outputStream = httpURLConnection?.outputStream
                    val writer = OutputStreamWriter(outputStream)
                    writer.write(requestBody)
                    writer.flush()
                    writer.close()
                }

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