package com.asthiseta.diyretrofit

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asthiseta.diyretrofit.databinding.ActivityMainBinding
import com.asthiseta.diyretrofit.model.Ayam
import com.asthiseta.diyretrofit.model.RestaurantResponseModel
import com.asthiseta.diyretrofit.networking.Config
import com.asthiseta.diyretrofit.networking.client.Client
import com.asthiseta.diyretrofit.networking.client.ConnectionCalllback
import com.asthiseta.diyretrofit.repo.RestoranRepo

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getAllRestaurant()

        val ayam = Ayam("ayam", 1)
        Log.d("Ayam", Config.client.buildRequestBody(ayam))
    }

    private fun getAllRestaurant() {
        RestoranRepo.getRestaurants(
            successCallback = { response ->

                runOnUiThread{
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