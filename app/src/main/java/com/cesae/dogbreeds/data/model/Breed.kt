package com.cesae.dogbreeds.data.model

import com.google.gson.annotations.SerializedName

data class Breed(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("breed_group") val breedGroup: String? = null,
    @SerializedName("life_span") val lifeSpan: String? = null,
    val temperament: String? = null,
    val origin: String? = null,
    @SerializedName("bred_for") val bredFor: String? = null,
    val weight: Measure? = null,
    val height: Measure? = null,
    val image: BreedImage? = null
)

data class Measure(
    val imperial: String? = null,
    val metric: String? = null
)

data class BreedImage(
    val url: String? = null
)
