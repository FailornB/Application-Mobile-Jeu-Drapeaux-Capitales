package com.example.worldover

data class LocalCountry(
    val name: LocalName,
    val capital: List<String>?, // Capital is a list in the JSON
    val flags: LocalFlags,
    val translations: Map<String, Translation>?,
    val population: Int?,
    val continents: List<String>?
)

data class LocalName(
    val common: String,
    val official: String
)

data class LocalFlags(
    val png: String?,
    val svg: String?
)

data class Translation(
    val official: String,
    val common: String
)