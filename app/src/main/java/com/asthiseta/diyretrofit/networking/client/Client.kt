package com.asthiseta.diyretrofit.networking.client


import android.content.Context
import android.database.sqlite.SQLiteQueryBuilder.buildQueryString
import android.util.Log
import com.asthiseta.diyretrofit.networking.parser.Parser
import org.json.JSONObject
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
        fun isSuccessFull(code: Int): Boolean {
            return code in 200..299
        }

        fun log(message: String) {
            // Replace this with your desired logging mechanism
            Log.d("[ Rip-troffit Log : ]", message)
        }

        fun errorLog(message: String) {
            // Replace this with your desired logging mechanism
            Log.e("[ Rip-troffit Error : ]", message, Throwable())
        }

    }

    var httpURLConnection: HttpURLConnection? = null
    var innerParser: Parser? = null
    var url: URL? = null
    var defaultRequestContent = "application/json"
    var defaultRequestProperty = "Content-Type"

    class Builder {
        private val client = Client()
        fun setUrl(url: String): Builder {
            client.url = URL(url)
            return this
        }

        fun setParser(parser: Parser): Builder {
            client.innerParser = parser
            return this
        }

        fun build(): Client {
            return this.client
        }
    }

     fun buildQueryString(params: Map<String, String>): String {
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

    fun <T> buildRequestBody(data : T) : String{
        val jsonObject = JSONObject()
        val properties = data!!::class.java.declaredFields

        for (prop in properties) {
            prop.isAccessible = true
            jsonObject.put(prop.name, prop.get(data))
        }

        return jsonObject.toString()
    }

    inline fun <reified T> enqueue(
        endpoint: String,
        method: String,
        requestBody: String? = null,
        headers: Map<String, String>? = null, // New parameter for headers
        queryParams: Map<String, String>? = null,
        callback: ConnectionCalllback<T>
    ) {
        Thread {
            try {
                var newUrl = URL(url.toString() + endpoint)
                // Add query parameters to the URL
                if (!queryParams.isNullOrEmpty()) {
                    val queryString = buildQueryString(queryParams)
                    newUrl = URL(newUrl.toString() + queryString)
                }

                log("Sending $method request to: $newUrl")

                httpURLConnection?.requestMethod = method
                httpURLConnection?.setRequestProperty(defaultRequestProperty, defaultRequestContent)

                // Set headers if present
                headers?.forEach { (key, value) ->
                    httpURLConnection?.setRequestProperty(key, value)
                }

                httpURLConnection?.doInput = true
                httpURLConnection?.doOutput = true

                // Set request body if present
                if (requestBody != null) {
                    log("Request Body: $requestBody")
                    val outputStream = httpURLConnection?.outputStream
                    val writer = OutputStreamWriter(outputStream)
                    writer.write(requestBody)
                    writer.flush()
                    writer.close()
                }

                httpURLConnection = newUrl.openConnection() as HttpURLConnection

                httpURLConnection?.connect()

                val responseCode = httpURLConnection?.responseCode
                log("Response Code: $responseCode")
                log("Response Message: ${httpURLConnection?.responseMessage}")
                log("Response code: $responseCode")
                if (isSuccessFull(responseCode!!)) {
                    val inputStream = httpURLConnection?.inputStream
                    val response = inputStream?.bufferedReader().use { it?.readText() }
                    log("Response: $response")

                    val modelResponse = innerParser!!.parse(response!!, T::class.java)
                    callback.onSuccess(modelResponse as T?)

                } else {
                    val error =
                        httpURLConnection?.errorStream?.bufferedReader().use { it?.readText() }
                            ?: "Error occurred"
                    errorLog(error)

                    callback.onError(error)
                }
            } catch (e: Exception) {
                errorLog(e.message!!)
                callback.onError(e.message!!)
            } finally {
                log("Closing Connection")
                httpURLConnection?.disconnect()
            }
        }.start()
    }

}