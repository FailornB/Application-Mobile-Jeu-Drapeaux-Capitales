package com.example.worldover

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
/*
object ApiClient {
    private const val BASE_URL = "https://restcountries.com/v3.1/"


    private val okHttpClient = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1)) // Forcer HTTP/1.1
        .connectTimeout(60, TimeUnit.SECONDS) // Temps de connexion
        .readTimeout(60, TimeUnit.SECONDS)    // Temps de lecture
        .writeTimeout(60, TimeUnit.SECONDS)   // Temps d'écriture
        .retryOnConnectionFailure(true)       // Réessayer en cas d'échec de connexion
        .build()

    val api: CountriesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Utilise le client OkHttp configuré
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountriesApi::class.java)
    }
}*/



object ApiClient {
    private const val BASE_URL = "https://api.countrylayer.com/v2/"
    private const val ACCESS_KEY = "b6bd86dcb3f364b1b0c558454492f270" // Remplacez par votre clé API

    // Configure OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    val api: CountriesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountriesApi::class.java)
    }
}



/*
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object LocalApiClient {
    fun getAllCountries(context: Context): List<Country> {
        // Ouvrir le fichier `all.json` depuis les assets
        val inputStream = context.assets.open("all.json")
        val reader = InputStreamReader(inputStream)
        // Convertir le contenu JSON en une liste de `Country`
        val type = object : TypeToken<List<Country>>() {}.type
        return Gson().fromJson(reader, type)
    }
}*/

