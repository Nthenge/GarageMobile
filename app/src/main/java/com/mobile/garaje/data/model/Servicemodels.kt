package com.mobile.garaje.data.model

data class ServiceResponse(
    val id: Long?,
    val serviceName: String?,
    val description: String?,
    val price: Double?,
    val avgDuration: Double?,
    val garageId: Long?,
    val garageName: String?,
    val categoryId: Long?,
    val categoryName: String?
)

data class ServicesApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: List<ServiceResponse>?
)

data class CategoryResponse(
    val id: Long?,
    val name: String?,
    val description: String?
)

data class CategoriesApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: List<CategoryResponse>?
)

data class GarageServicesApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: List<ServiceResponse>?
)


data class ServiceActionResponse(
    val success: Boolean?,
    val message: String?
)