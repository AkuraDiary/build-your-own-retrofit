package com.asthiseta.diyretrofit.networking.client


import android.util.Log
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
        fun isSuccessFull(code: Int): Boolean {
            return code == 201 || code == 200
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

     fun log(message: String) {
        // Replace this with your desired logging mechanism
        Log.d("[ Rip-troffit Log : ]", message)
    }

     fun errorLog(message: String) {
        // Replace this with your desired logging mechanism
        Log.e("[ Rip-troffit Error : ]", message)
    }

    inline fun <reified T> enqueue(
        endpoint: String, method: String, requestBody: String? = null,
        queryParams: Map<String, String>? = null, callback: ConnectionCalllback<T>
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

                httpURLConnection = newUrl.openConnection() as HttpURLConnection
                httpURLConnection?.requestMethod = method
                httpURLConnection?.setRequestProperty(defaultRequestProperty, defaultRequestContent)
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

                httpURLConnection?.connect()

                val responseCode = httpURLConnection?.responseCode
                log("Response Code: $responseCode")
                if (isSuccessFull(responseCode!!)) {

                    val inputStream = httpURLConnection?.inputStream
                    val response = inputStream?.bufferedReader().use { it?.readText() }
                    log("Response: $response")

                    val modelResponse = innerParser!!.parse(response!!, T::class.java)

                    callback.onSuccess(modelResponse)
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