# Project's Background
In response to the restrictions of using third-party libraries in a competition (LKS) even if it's a standardized and well-known library such as retrofit, and even live-data.
The competition's rules restricted the usage of other libraries besides the built-in and added in the initial project setup. Even last year judges explicitly said not to use retrofit

And I think it's stupid, so why don't we make the `retrofit` ourselves

# Explanations And Usage

### Libraries Used
Here I'm using the already included library such as
```
org.json.JSONObject
java.io.OutputStreamWriter
java.net.HttpURLConnection
java.net.URL
java.net.URLEncoder
```
which should not violate the rules

### Important Files
The main functionality consists of some files and interfaces inside the [`networking`](https://github.com/AkuraDiary/build-your-own-retrofit/tree/main/app/src/main/java/com/asthiseta/diyretrofit/networking) package, mainly are : 

- #### [Client.kt](https://github.com/AkuraDiary/build-your-own-retrofit/blob/main/app/src/main/java/com/asthiseta/diyretrofit/networking/client/Client.kt)
Contains the config for the HTTP client and its builder with automatic logging and parsing
  
- #### [ConnectionCallback.kt](https://github.com/AkuraDiary/build-your-own-retrofit/blob/main/app/src/main/java/com/asthiseta/diyretrofit/networking/client/ConnectionCallback.kt)
Interface of connection callback that would be called upon request

  
- #### [Parser.kt](https://github.com/AkuraDiary/build-your-own-retrofit/blob/main/app/src/main/java/com/asthiseta/diyretrofit/networking/parser/Parser.kt)
General implementation of the parser, mainly for parsing the JSON string into usable data class
#### Though should be noted
I'm trying to use the generalization and abstraction for the parser so that it can be used easily and support list and nested object parsing. There are some rules to make in creating the data classes `models` to achieve the desired results
  - The attribute names should be identical / reflecting the field name in the JSON response
  - The attribute should be using `var` instead of `val`
  - The attribute should be Nullable ie 
  - The attribute should be initiated as null

```kotlin
// from the criteria above
// An example of the model data class that's supported by the parser should look like this

data class Ayam(
 var name: String? = null,
 var age: Int? = null
)

// the reason lies in my current implementation of the Parser

interface Parser {
    fun <T> parse(jsonString: String, clazz: Class<T>): Any?
}


class JsonParser : Parser {
    override fun <T> parse(jsonString: String, clazz: Class<T>): Any? {
        val jsonObject = JSONObject(jsonString)


        // HERE
        val instance = clazz.newInstance() 
        /*
        here I'm using the newInstance() which creates a new object/instance of the class first
        the problem is, it uses a no-parameter constructor (default constructor)
        you could do it with regular classes, but with data classes, you have to initiate the attribute upon creation
        thus why we initiate the attribute with null in our data classes
        */

        /* rest of the code */

        return instance
    }

    private fun parseList(toString: String, listType: Class<*>): Any? {
        /* rest of the code */
    }


}

```

## Using it
Though I said it's a DIY retrofit implementation, it's not fully the same as retrofit 
> mainly because I don't quite understand Annotations

### This is only the basic usage of the program (If you have a better way to do it, feel free to contribute)

For starter, you need to create the client instance first, like so we called the `retrofitConfig`
in your `Config.kt` should be looking similar to this
```kotlin

//Config.kt

object Config {

    const val BASE_URL = "put your URL here"

    val client :Client = Builder()
        .setUrl(BASE_URL)
        .setParser(JsonParser())
        .build()

}
```
Then we skip the `ApiService` class and create a method calls the method directly from your Repository like this : 

```kotlin
object RestoranRepo {

    var restaurantResponseModel: RestaurantResponseModel? = null
    fun getRestaurants(
        successCallback: (RestaurantResponseModel?) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        Config.client.enqueue(
            endpoint = "list",
            method = Client.GET,
            // here are some example if you need to use some parameters
            // headers = mapOf("Authorization" to "Bearer ${Config.token here}") 
            // queryParams = mapOf("q" to "restaurant"), //{base url}/list?q=restaurant
            // requestBody = Client.buildRequestBody(
            //      Ayam("ayam", 10)
            // ),
            callback = object : ConnectionCalllback<RestaurantResponseModel?> {
                override fun onSuccess(response: RestaurantResponseModel?) {
                    restaurantResponseModel = response
                    successCallback(response)
                }

                override fun onError(error: String) {
                    errorCallback(error)
                }
            }
        )
    }
}
```




# Thanks
A contribution is really appreciated

