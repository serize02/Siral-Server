package com.siral.utils

import kotlinx.serialization.Serializable

@Serializable
enum class ResponseMessage(val message: String) {
    INVALID_CREDENTIALS("Invalid Credentials"),
    INVALID_STUDENT("Invalid Student"),
    SOMETHING_WENT_WRONG("Something Went Wrong"),
    ROLE_NOT_FOUND_IN_TOKEN("Role Not Found in Token"),
    ACCESS_DENIED("Access Denied"),
    MISSING_REQUIRED_FIELDS("Missing Required Fields"),
    DINNING_HALL_NOT_FOUND("Dinning Hall Not Found"),
    INVALID_ROLE("Invalid Role"),
    USER_ALREADY_HAS_ROLE("This User Has Already a Role Associated"),
    USER_INSERTED_SUCCESSFULLY("User Inserted Successfully"),
    MISSING_EMAIL("Missing Email"),
    USER_NOT_FOUND("User Not Found"),
    USER_DELETED_SUCCESSFULLY("User Deleted Successfully"),
    MISSING_DINNING_HALL_NAME("Missing Dinning Hall Name"),
    DINNING_HALL_ALREADY_EXISTS("Dinning Hall Already Exists"),
    DINNING_HALL_INSERTED_SUCCESSFULLY("Dinning Hall Inserted Successfully"),
    DINNING_HALL_DELETED_SUCCESSFULLY("Dinning Hall Deleted Successfully"),
    ID_NOT_FOUND_IN_TOKEN("ID Not Found in Token"),
    MISSING_SCHEDULE_ITEM_ID("Missing Schedule Item ID"),
    MEAL_ALREADY_RESERVED("You Have Already Reserved This Meal"),
    SCHEDULE_ITEM_NOT_FOUND("Schedule Item Not Found"),
    SCHEDULE_ITEM_NOT_AVAILABLE("Schedule Item Not Available"),
    RESERVATION_MADE("Reservation Made"),
    MISSING_RESERVATION_ID("Missing Reservation Id"),
    RESERVATION_DELETED("Reservation Deleted"),
    INVALID_DATE("Invalid Date"),
    ALL_DONE("All Done"),
}