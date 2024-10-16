package com.siral.utils

import kotlinx.serialization.Serializable

@Serializable
enum class Messages {
    INVALID_CREDENTIALS,
    ACCESS_DENIED,
    USER_NOT_FOUND,
    USER_DELETED_SUCCESSFULLY,
    SCHEDULE_ITEM_NOT_FOUND,
    SCHEDULE_ITEM_DELETED_SUCCESSFULLY,
    RESERVATION_CREATED_SUCCESSFULLY,
    RESERVATION_DELETED_SUCCESSFULLY,
    ALL_DONE,
    RESERVATION_NOT_FOUND,
    DATA_RETRIEVED_SUCCESSFULLY,
    USER_LOGGED_SUCCESSFULLY,
    USER_CREATED_SUCCESSFULLY,
    STUDENT_NOT_FOUND,
    DININGHALL_NOT_FOUND,
    DININGHALL_CREATED_SUCCESSFULLY,
    DININGHALL_UPDATED_SUCCESSFULLY,
    DININGHALL_DELETED_SUCCESSFULLY,
    VALIDATION_ERROR,
    MISSING_REQUIRED_FIELDS
}