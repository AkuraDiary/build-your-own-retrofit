package com.asthiseta.diyretrofit

import com.asthiseta.diyretrofit.model.RestaurantResponseModel
import com.asthiseta.diyretrofit.networking.client.Client
import com.asthiseta.diyretrofit.networking.client.ConnectionCalllback
import com.asthiseta.diyretrofit.networking.parser.JsonParser
import com.asthiseta.diyretrofit.repo.RestoranRepo
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testClient(){
         val BASE_URL = "https://restaurant-api.dicoding.dev/"
        val client = Client.Builder()
            .setUrl(BASE_URL)
            .setParser(JsonParser())
            .build()

        client.enqueue(
            endpoint = "list",
            method = Client.GET,
//             headers = mapOf("Authorization" to "Bearer ayamgoyeng"),
             queryParams = mapOf("q" to "restaurant", "username" to "admin", "password" to "admin"), //{base url}/list?q=restaurant
//             requestBody = Client.buildRequestBody(
//                  Ayam("ayam", 10)
//             ),
            callback = object : ConnectionCalllback<RestaurantResponseModel?> {
                override fun onSuccess(response: RestaurantResponseModel?) {
                    RestoranRepo.restaurantResponseModel = response
                    println("Success")
                    assert(true)
                }

                override fun onError(error: String) {
                    println("Failed")
                    assert(false)
                }
            }
        )
    }


}