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
- #### [ConnectionCallback.kt](https://github.com/AkuraDiary/build-your-own-retrofit/blob/main/app/src/main/java/com/asthiseta/diyretrofit/networking/client/ConnectionCallback.kt)
- ### [Parser.kt](https://github.com/AkuraDiary/build-your-own-retrofit/blob/main/app/src/main/java/com/asthiseta/diyretrofit/networking/parser/Parser.kt)






# Thanks
A contribution is really appreciated

