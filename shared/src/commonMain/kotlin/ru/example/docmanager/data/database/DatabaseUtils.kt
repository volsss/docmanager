/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun <T> dbQuery(block: Transaction.() -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}