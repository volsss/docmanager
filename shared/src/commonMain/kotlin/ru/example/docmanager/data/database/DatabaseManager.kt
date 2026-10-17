/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.database

interface DatabaseManager {

    fun init(jdbcUrl: String, driver: String, user: String?, password: String?)

    fun disconnect()

    fun isInitialized(): Boolean

}