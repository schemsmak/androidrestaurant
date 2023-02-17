package com.example.bistrot.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Price (

    @SerializedName("price") val price: String,
    @SerializedName("id") val id: Int,
    @SerializedName("size") val size: String
): Serializable