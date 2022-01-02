package com.maricoolsapps.e_commerce.interfaces

import com.maricoolsapps.e_commerce.model.Product

interface onItemClickListener<T> {
    fun onItemClick(t: T)
    fun onButtonClick(t: T)
}