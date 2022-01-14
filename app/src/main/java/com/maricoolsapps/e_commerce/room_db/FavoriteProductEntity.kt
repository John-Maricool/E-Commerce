package com.maricoolsapps.e_commerce.room_db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteProductEntity")
data class FavoriteProductEntity(
    val brand: String,
   @PrimaryKey(autoGenerate = false) val id: String
)