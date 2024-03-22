package com.asthiseta.diyretrofit.networking.client

import android.util.Log
import com.asthiseta.diyretrofit.networking.parser.Parser
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Client {
    companion object {

        const val MINUTE = 60000
        const val SECOND = 1000
        const val MILLISECOND = 1

        const val POST = "POST"
        const val GET = "GET"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
        fun isSuccessFull(code: Int): Boolean {
            return code in 200..299
        }

        fun log(message: String) {
            // Replace this with your desired logging mechanism
            Log.d("[ Rip-troffit Log ]", message)
        }

        fun errorLog(message: String) {
            // Replace this with your desired logging mechanism
            Log.e("[ Rip-troffit Error ]", message, Throwable())
        }

        fun <T> buildRequestBody(data: T): String {
            val jsonObject = JSONObject()
            val properties = data!!::class.java.declaredFields

            for (prop in properties) {
                prop.isAccessible = true
                jsonObject.put(prop.name, prop.get(data))
                log("")
                log("Request Body:")
                log("Property Name: ${prop.name}")
                log("Property Type: ${prop.type}")
                log("Property Value: ${prop.get(data)}")
                log("")
            }

            return jsonObject.toString()
        }
    }

    var httpURLConnection: HttpURLConnection? = null
    var innerParser: Parser? = null
    var url: URL? = null
    var connectTimeout = 15000
    var readTimeout = 15000
    var defaultRequestContent = "application/json"
    var defaultRequestProperty = "Content-Type"

    class Builder {
        private val client = Client()
        fun setUrl(url: String): Builder {
            client.url = URL(url)
            return this
        }

        fun setConnectTimeout(timeout: Int, timeUnit : Int): Builder {
            client.connectTimeout = timeout * timeUnit
            return this
        }

        fun setReadTimeout(timeout: Int, timeUnit: Int): Builder {
            client.readTimeout = timeout * timeUnit
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
                log("Query Param is Present ${!queryParams.isNullOrEmpty()}")
                if (!queryParams.isNullOrEmpty()) {
                    log("Query Params: $queryParams")
                    val queryString = buildQueryString(queryParams)
                    log("Query string: $queryString")
                    newUrl = URL(newUrl.toString() + queryString)
                }

                httpURLConnection = newUrl.openConnection() as HttpURLConnection

                httpURLConnection?.setRequestProperty(defaultRequestProperty, defaultRequestContent)

                httpURLConnection?.doInput = true
                httpURLConnection?.doOutput = (method == POST)

                // Set headers if present
                headers?.forEach { (key, value) ->
                    log("Request Headers Property: $key : $value")
                    httpURLConnection?.setRequestProperty(key, value)
                }

                log("Request Properties: ${httpURLConnection?.requestProperties}")

                // Set request body if present
                log("Request Body is Present ${!requestBody.isNullOrEmpty()}")
                if (requestBody != null) {
                    log("Request Body: $requestBody")
                    val outputStream = httpURLConnection?.outputStream
                    val writer = OutputStreamWriter(outputStream)
                    writer.write(requestBody)
                    writer.flush()
                    writer.close()
                }


                httpURLConnection?.requestMethod = method
//                httpURLConnection?.setReq = method
                log("Sending ${httpURLConnection?.requestMethod} request to: $newUrl")

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