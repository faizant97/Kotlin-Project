package com.wolt.task.demo.controller

import com.wolt.task.demo.model.CalculatedFeeModel
import com.wolt.task.demo.model.FeeCalculateModel
import com.wolt.task.demo.service.FeeCalculateService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/wolt")
class DeliveryFeeController (private val service: FeeCalculateService){



    @PostMapping("/calculateFee")
    fun calculateFee(@RequestBody feeCalculateModel: FeeCalculateModel) : CalculatedFeeModel {

        return service.getFee(feeCalculateModel)
    }
}