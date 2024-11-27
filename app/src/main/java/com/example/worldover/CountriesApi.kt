package com.example.worldover

import retrofit2.http.GET

data class Country(
    val name: Name,
    val capital: List<String>?, // Certaines réponses peuvent ne pas avoir de capitale
    val flags: Flags
)

data class Name(
    val common: String,  // Utilisez "common" pour obtenir le nom commun du pays
    val official: String
)

data class Flags(
    val png: String
)

interface CountriesApi {
    @GET("all")
    suspend fun getAllCountries(): List<Country>
}
