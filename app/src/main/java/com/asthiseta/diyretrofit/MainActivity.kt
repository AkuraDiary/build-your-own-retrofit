package com.asthiseta.diyretrofit

//import com.asthiseta.diyretrofit.model.RawListWrapper
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.asthiseta.diyretrofit.databinding.ActivityMainBinding
import com.asthiseta.diyretrofit.model.CustomerReviewRequest
import com.asthiseta.diyretrofit.model.RestaurantModel
import com.asthiseta.diyretrofit.model.RestaurantModelList
import com.asthiseta.diyretrofit.networking.glidealternative.ImageLoader
import com.asthiseta.diyretrofit.networking.parser.JsonParser
import com.asthiseta.diyretrofit.repo.RestoranRepo

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
//        getAllRestaurant()

        val jsonString = """
            
            [
            {
          "id": "rqdv5juczeskfw1e867",
          "name": "Melting Pot",
          "description": "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. ...",
          "pictureId": "14",
          "city": "Medan",
          "rating": 4.2
      },
      {
          "id": "s1knt6za9kkfw1e867",
          "name": "Kafe Kita",
          "description": "Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. ...",
          "pictureId": "25",
          "city": "Gorontalo",
          "rating": 4
      }
  ]
"""

        parseResponse<RestaurantModelList>(jsonString)

//        binding?.textView?.text = parsed.toString()

//        val ayam = Ayam("ayam", 1)
//        Log.d("Ayam", Client.buildRequestBody(ayam))

//        sendReviewRestaurant()

//        doLogin()
        ImageLoader(
            "https://restaurant-api.dicoding.dev/images/small/14",
            binding?.imageView!!
        ).loadImage()
    }

    private inline fun <reified T> parseResponse(jsonString: String) {
        val parsed = JsonParser().parse(jsonString, T::class.java) as T
        binding?.textView?.text = parsed.toString()
    }

    private fun doLogin() {
        RestoranRepo.login("admin", "admin", {
            runOnUiThread {
                binding?.textView?.text = it.toString()
            }
        },
            {
                runOnUiThread {
                    binding?.textView?.text = it.toString()
                }
            }
        )
    }

    private fun sendReviewRestaurant() {

        RestoranRepo.sendReview(
            review = CustomerReviewRequest(
                "rqdv5juczeskfw1e867",
                "ayamgoyeng",
                "mantap"
            ),
            successCallback = { response ->

                runOnUiThread {
                    // update the UI here
                    response?.let {
                        binding?.textView?.text = it.message.toString()
                    }
                }

            },

            errorCallback = {
                runOnUiThread {
                    // update the UI here or show toast
                    binding?.textView?.text = it
                }
            }
        )
    }

    private fun getAllRestaurant() {
        RestoranRepo.getRestaurants(
            successCallback = { response ->

                runOnUiThread {
                    // update the UI here
                    response?.let {
                        binding?.textView?.text = it.toString()
                    }
                }

            },

            errorCallback = {
                runOnUiThread {
                    // update the UI here or show toast
                    binding?.textView?.text = it
                }
            }
        )
    }
}