package com.fiap.order.domain.entities

import com.fiap.order.domain.valueobjects.ProductCategory
import java.math.BigDecimal

data class Product(
    val number: Long? = null,
    val orderNumber: Long? = null,
    val name: String,
    val price: BigDecimal,
    val description: String,
    val category: ProductCategory,
    val minSub: Int = 0,
    val maxSub: Int = Int.MAX_VALUE,
    val subItems: List<Product>? = arrayListOf(),
    val components: List<Component>? = arrayListOf(),
) {
    fun update(newProduct: Product): Product =
        copy(
            name = newProduct.name,
            price = newProduct.price,
            description = newProduct.description,
            category = newProduct.category,
            subItems = newProduct.subItems,
            maxSub = newProduct.maxSub,
            minSub = newProduct.minSub,
            components = newProduct.components,
        )

    fun isLogicalItem() = components?.isEmpty()
}
