package com.siral.utils

enum class Actions(val type: String) {
    LOGIN("LOGIN"),
    INSERT_NEW_ROLE("INSERT_NEW_ROLE"),
    DELETE_ROLE("DELETE_ROLE"),
    INSERT_DINNING_HALL("INSERT_DINNING_HALL"),
    DELETE_DINNING_HALL("DELETE_DINNING_HALL"),
    MAKE_RESERVATION("MAKE_RESERVATION"),
    DELETE_RESERVATION("DELETE_RESERVATION"),
    INSERT_SCHEDULE_ITEM("INSERT_SCHEDULE_ITEM"),
    DELETE_SCHEDULE_ITEM("DELETE_SCHEDULE_ITEM"),
}