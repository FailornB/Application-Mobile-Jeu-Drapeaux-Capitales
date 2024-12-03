package com.example.worldover


import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object ApiLocal {
    fun getAllCountries(context: Context): List<Country> {
        val inputStream = context.assets.open("all.json")
        val reader = InputStreamReader(inputStream)

        val type = object : TypeToken<List<LocalCountry>>() {}.type
        val localCountries: List<LocalCountry> = Gson().fromJson(reader, type)

        val parsedCountries = localCountries.map { localCountry ->
            val country = Country(
                name = localCountry.name.common,
                capital = localCountry.capital?.firstOrNull(),
                flags = localCountry.flags?.png,
                translations = localCountry.translations?.mapValues { it.value.common } ?: emptyMap(),
                population = localCountry.population ?: 0,
                continent = localCountry.continents?.firstOrNull()
            )
            println("Country: $country") // Log pour vérifier chaque pays
            country
        }

        return parsedCountries
    }
}


