/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {
    val snackbarHostState = SnackbarHostState()

}