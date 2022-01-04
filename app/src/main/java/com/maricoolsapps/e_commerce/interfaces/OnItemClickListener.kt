package com.maricoolsapps.e_commerce.interfaces

import com.maricoolsapps.e_commerce.model.Product

interface OnItemClickListener<T> {
    fun onItemClick(t: T)
   }