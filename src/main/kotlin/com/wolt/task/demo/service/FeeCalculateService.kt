package com.wolt.task.demo.service

import com.wolt.task.demo.model.CalculatedFeeModel
import com.wolt.task.demo.model.FeeCalculateModel
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import kotlin.math.ceil
import kotlin.math.min

@Service
class FeeCalculateService {
    /**
     * Main function for the fee calculation
     * @author: Faizan Tahir
     * @Param {FeeCalculateModel} Model for request Parameters
     * @return Calculated Deliver Fee @Param CalculateFeeModel
     */
    fun getFee(feeCalculateModel: FeeCalculateModel): CalculatedFeeModel {
        validateFeeCalculateModel(feeCalculateModel)

        if (feeCalculateModel.cartValue >= 20000) {
            return CalculatedFeeModel(deliveryFee = 0)
        }

        var fee: Int = getSmallOrderSurcharge(feeCalculateModel.cartValue) +
                getDistanceBasedSurcharge(feeCalculateModel.deliveryDistance) +
                getDeliveryFeeForNumOfItems(feeCalculateModel.numberOfItems)

        fee = applyFridayRushFee(fee, feeCalculateModel.time)
        val cappedFee = min(fee, 1500)

        return CalculatedFeeModel(deliveryFee = cappedFee)
    }

    /**
     * Validation for the inputs
     */
    private fun validateFeeCalculateModel(model: FeeCalculateModel) {
        require(model.cartValue >= 0) { "Cart value must be non-negative." }
        require(model.deliveryDistance >= 0) { "Delivery distance must be non-negative." }
        require(model.numberOfItems >= 0) { "Number of items must be non-negative." }
        requireNotNull(model.time) { "Time must not be null." }
    }

    /**
     * Small order Surcharge function
     */
    fun getSmallOrderSurcharge(cartValue: Int): Int {
        require(cartValue >= 0) { "Cart value must be non-negative." }
        return if (cartValue < 1000) 1000 - cartValue else 0
    }

    /**
     * Distance based surcharge calculation
     */
    fun getDistanceBasedSurcharge(distance: Int): Int {
        require(distance >= 0) { "Distance must be non-negative." }
        val baseFee = 200
        val additionalFee = 100
        val baseDistance = 1000
        val additionalDistance = 500

        return when {
            distance <= 500 -> 100
            distance <= baseDistance -> baseFee
            else -> {
                val extraDistance = distance - baseDistance
                val additionalDistanceSegments = ceil(extraDistance.toDouble() / additionalDistance).toInt()
                baseFee + (additionalDistanceSegments * additionalFee)
            }
        }
    }

    /**
     * Function Calculating delivery fee on the basis of number of items
     */
    fun getDeliveryFeeForNumOfItems(numOfItems: Int): Int {
        require(numOfItems >= 0) { "Number of items must be non-negative." }
        val surchargeForItemOverFour = 50
        val bulkFee = 120
        val bulkFeeThreshold = 12

        return when {
            numOfItems < 5 -> 0
            numOfItems <= bulkFeeThreshold -> (numOfItems - 4) * surchargeForItemOverFour
            else -> (bulkFeeThreshold - 4) * surchargeForItemOverFour + bulkFee + ((numOfItems - bulkFeeThreshold) * surchargeForItemOverFour)
        }
    }

    /**
     * Surcharge for the Friday rush hours
     */
    fun applyFridayRushFee(fee: Int, time: String): Int {
        require(fee >= 0) { "Fee must be non-negative." }
        try {
            val currentTime = ZonedDateTime.ofInstant(Instant.parse(time), ZoneId.of("UTC"))
            val isFridayRush = currentTime.dayOfWeek.value == 5 && currentTime.hour in 15..18
            return if (isFridayRush) (fee * 1.2).toInt() else fee
        } catch (e: DateTimeParseException) {
            throw DateTimeParseException("Format Exception", time, 0)
        }
    }
}
