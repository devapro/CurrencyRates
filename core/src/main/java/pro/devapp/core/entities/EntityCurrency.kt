package pro.devapp.core.entities

import androidx.annotation.DrawableRes

data class EntityCurrency(
    val code: String,
    val name: String?,
    @DrawableRes
    val flag: Int?,
    val rate: Double
) : Entity