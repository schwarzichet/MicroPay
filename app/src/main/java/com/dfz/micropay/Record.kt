package com.dfz.micropay

data class Record(
        val payer:String,
        val payee:String,
        val time:String,
        val money:String
)