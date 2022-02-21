package com.maricoolsapps.e_commerce.data.model

class Report(val id: String, val shortDesc: String, val mainDesc: String) {
    constructor(): this("", "", "")
}