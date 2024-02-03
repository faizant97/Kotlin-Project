package com.wolt.task.demo.model

data class FeeCalculateModel(
    val cartValue: Int,
    val deliveryDistance: Int,
    val numberOfItems: Int,
    val time: String
)
