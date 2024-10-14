package com.siral.utils

object Access {
    val students: List<String> = listOf(UserRole.STUDENT.name)
    val admin: List<String> = listOf(UserRole.ADMIN.name)
    val siteManagers: List<String> = listOf(UserRole.SITE_MANAGER.name)
    val schedulers: List<String> = listOf(UserRole.SCHEDULER.name)
    val administration: List<String> = listOf(UserRole.ADMIN.name, UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name)
}