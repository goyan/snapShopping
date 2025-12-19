package com.snapshopping.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FoodCategoryTest {

    @Test
    fun `fromString returns correct category for valid input`() {
        assertThat(FoodCategory.fromString("dairy")).isEqualTo(FoodCategory.DAIRY)
        assertThat(FoodCategory.fromString("DAIRY")).isEqualTo(FoodCategory.DAIRY)
        assertThat(FoodCategory.fromString("Dairy")).isEqualTo(FoodCategory.DAIRY)
    }

    @Test
    fun `fromString returns OTHER for invalid input`() {
        assertThat(FoodCategory.fromString("invalid")).isEqualTo(FoodCategory.OTHER)
        assertThat(FoodCategory.fromString("")).isEqualTo(FoodCategory.OTHER)
        assertThat(FoodCategory.fromString("unknown_category")).isEqualTo(FoodCategory.OTHER)
    }

    @Test
    fun `fromString handles all valid categories`() {
        assertThat(FoodCategory.fromString("meat")).isEqualTo(FoodCategory.MEAT)
        assertThat(FoodCategory.fromString("vegetables")).isEqualTo(FoodCategory.VEGETABLES)
        assertThat(FoodCategory.fromString("fruits")).isEqualTo(FoodCategory.FRUITS)
        assertThat(FoodCategory.fromString("beverages")).isEqualTo(FoodCategory.BEVERAGES)
        assertThat(FoodCategory.fromString("condiments")).isEqualTo(FoodCategory.CONDIMENTS)
        assertThat(FoodCategory.fromString("leftovers")).isEqualTo(FoodCategory.LEFTOVERS)
        assertThat(FoodCategory.fromString("snacks")).isEqualTo(FoodCategory.SNACKS)
        assertThat(FoodCategory.fromString("frozen")).isEqualTo(FoodCategory.FROZEN)
        assertThat(FoodCategory.fromString("other")).isEqualTo(FoodCategory.OTHER)
    }
}
