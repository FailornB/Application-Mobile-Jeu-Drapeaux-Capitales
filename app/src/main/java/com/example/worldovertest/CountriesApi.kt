package com.example.worldovertest

import retrofit2.http.GET
import retrofit2.http.Query


data class Country(
    val name: String,
    val capital: String?,
    val flags: String?,
    val translations: Map<String, String>, // Ajout pour gérer les traductions
    val population: Long?,
    val continent: String?,
    val languages: List<String>?,
    val currencies: Map<String, Currency>?,
    val difficulty: String
)

interface CountriesApi {
    @GET("all")
    suspend fun getAllCountries(
        @Query("access_key") accessKey: String = "0e2ed87c85591617a4cc75f1e33a77fe" // Ajout de la clé API
    ): List<Country>
}