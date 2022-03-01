package com.maricoolsapps.e_commerce.utils

import android.net.Uri
import android.provider.MediaStore
import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Context.convertImageToByteArray(uri: Uri): ByteArray {
    val imgBmp = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    val opStream = ByteArrayOutputStream()
    imgBmp.compress(Bitmap.CompressFormat.JPEG, 90, opStream)
    return opStream.toByteArray()
}