package com.asthiseta.diyretrofit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asthiseta.diyretrofit.databinding.ActivityMainBinding
import com.asthiseta.diyretrofit.model.RestaurantResponseModel
import com.asthiseta.diyretrofit.networking.Config
import com.asthiseta.diyretrofit.networking.client.Client
import com.asthiseta.diyretrofit.networking.client.ConnectionCalllback

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getAllRestaurant()
    }

    private fun getAllRestaurant() {
        Config.client.enqueue(
            context = this@MainActivity,
            endpoint = "list",
            method = Client.GET,
            callback = object : ConnectionCalllback<RestaurantResponseModel> {
                override fun onSuccess(response: RestaurantResponseModel) {
                    runOnUiThread {
                        binding?.apply {
//                            textView?.text = response?.restaurants?.get(0)?.name
                            textView?.text = response?.count.toString()
                        }
                    }
                }

                override fun onError(error: String) {
                    // Handle the error
                    runOnUiThread {
                        binding?.apply {
                            textView?.text = error
                        }
                    }
                }
            }
        )
    }
}