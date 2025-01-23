package com.example.worldover


data class LocalCountry(
    val name: LocalName,
    val capital: List<String>?, // Capital is a list in the JSON
    val flags: LocalFlags,
    val translations: Map<String, Translation>?,
    val population: Long?,
    val continents: List<String>?,
    val languages: Map<String, String>?,
    val currencies: Map<String, Currency>?,
    val difficulty: String
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

data class Currency(
    val name: String?,
    val symbol: String?

)