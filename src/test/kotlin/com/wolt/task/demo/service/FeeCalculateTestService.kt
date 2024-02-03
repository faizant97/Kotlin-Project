package com.wolt.task.demo.service

import com.wolt.task.demo.model.FeeCalculateModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.format.DateTimeParseException

class FeeCalculateTestService {
    private val service = FeeCalculateService()

    /**
     * Positive Test Cases
     */

    //Test getSmallOrderSurcharge Method
    @Test
    fun `getSmallOrderSurcharge with cart value below threshold`() {
        val cartValue = 600
        assertEquals(400, service.getSmallOrderSurcharge(cartValue))
    }

    @Test
    fun `getSmallOrderSurcharge with cart value above threshold`() {
        val cartValue = 1000
        assertEquals(0, service.getSmallOrderSurcharge(cartValue))
    }

    // Test getDistanceBasedSurcharge Method
    @Test
    fun `getDistanceBasedSurcharge with distance within base fee range`() {
        assertEquals(200, service.getDistanceBasedSurcharge(800))
    }

    @Test
    fun `getDistanceBasedSurcharge with distance requiring additional fees`() {
        assertEquals(400, service.getDistanceBasedSurcharge(1501))
    }
    // Test getDeliveryFeeForNumOfItems Method
    @Test
    fun `getDeliveryFeeForNumOfItems with items below threshold`() {
        assertEquals(0, service.getDeliveryFeeForNumOfItems(3))
    }

    @Test
    fun `getDeliveryFeeForNumOfItems with items above threshold but below bulk`() {
        assertEquals(150, service.getDeliveryFeeForNumOfItems(7)) // Example calculation
    }

    @Test
    fun `getDeliveryFeeForNumOfItems with items in bulk range`() {
        assertEquals(620, service.getDeliveryFeeForNumOfItems(14)) // Example calculation
    }

    //Test applyFridayRushFee Method

    @Test
    fun `applyFridayRushFee during rush hours`() {
        val fee = 1000
        val rushTime = "2024-01-19T16:00:00Z"
        assertEquals(1200, service.applyFridayRushFee(fee, rushTime))
    }

    @Test
    fun `applyFridayRushFee outside rush hours`() {
        val fee = 1000
        val nonRushTime = "2024-01-19T10:00:00Z"
        assertEquals(1000, service.applyFridayRushFee(fee, nonRushTime))
    }

    // Integration Test for getFee Method

    @Test
    fun `getFee with all surcharges and during rush hour`() {
        val feeCalculateModel = FeeCalculateModel(
            cartValue = 1000,
            deliveryDistance = 1500,
            numberOfItems = 4,
            time = "2024-01-19T16:00:00Z"

        // Assuming Friday at 4 PM
        )

        val result = service.getFee(feeCalculateModel)
        val expectedFee = 360 // Calculate the expected fee based on your logic
        assertEquals(expectedFee, result.deliveryFee)
    }

    /**
     * Negative Test cases
     */

    // Test getSmallOrderSurcharge Method with Negative Input
    @Test
    fun `getSmallOrderSurcharge with negative cart value`() {
        val negativeCartValue = -500
        assertThrows<IllegalArgumentException> {
            service.getSmallOrderSurcharge(negativeCartValue)
        }
    }

    // Test getDistanceBasedSurcharge Method with Negative Distance
    @Test
    fun `getDistanceBasedSurcharge with negative distance`() {
        val negativeDistance = -100
        assertThrows<IllegalArgumentException> {
            service.getDistanceBasedSurcharge(negativeDistance)
        }
    }

    //Test getDeliveryFeeForNumOfItems Method with Negative Number of Items
    @Test
    fun `getDeliveryFeeForNumOfItems with negative number of items`() {
        val negativeItems = -3
        assertThrows<IllegalArgumentException> {
            service.getDeliveryFeeForNumOfItems(negativeItems)
        }
    }

    //Test applyFridayRushFee Method with Invalid Time Format
    @Test
    fun `applyFridayRushFee with invalid time format`() {
        val invalidTime = "not-a-time-string"
        assertThrows<DateTimeParseException> {
            service.applyFridayRushFee(1000, invalidTime)
        }
    }

    //Integration Test for getFee Method with Invalid Inputs
    @Test
    fun `getFee with negative values for cartValue, distance, and numberOfItems`() {
        val feeCalculateModel = FeeCalculateModel(
            cartValue = -500,
            deliveryDistance = -1000,
            numberOfItems = -5,
            time = ""
        )
        assertThrows<IllegalArgumentException> {
            service.getFee(feeCalculateModel)
        }
    }


}
