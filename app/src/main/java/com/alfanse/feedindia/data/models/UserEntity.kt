package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class UserEntity(
    @SerializedName("id")
    val userId: String?,
    @SerializedName("firebaseId")
    val firebaseId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("userType")
    val userType: String?,
    @SerializedName("isAdmin")
    val isAdmin: Boolean,
    @SerializedName("needItems")
    val needItems: String?,
    @SerializedName("donateItems")
    val donateItems: String?,
    @SerializedName("donorVisibility")
    val donorVisibility: Boolean?,
    @SerializedName("location_address")
    val address: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("lng")
    val lng: String?,
    @SerializedName("groupName")
    val groupName: String?,
    @SerializedName("groupId")
    val groupId: String?
)