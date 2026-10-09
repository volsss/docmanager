/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.di

import androidx.compose.ui.graphics.Color
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg

fun getWindowsAccentColor(): Color? {
    val os = System.getProperty("os.name").lowercase()
    if (!os.contains("win")) return null

    val dwmPath = "SOFTWARE\\Microsoft\\Windows\\DWM"
    val themePath = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Accent"
    val explorerAccentPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Accent"

    return try {
        if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, explorerAccentPath, "AccentPalette")) {
            val paletteBytes = Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, explorerAccentPath, "AccentPalette")
            if (paletteBytes.size >= 4) {
                val r = paletteBytes[0].toInt() and 0xFF
                val g = paletteBytes[1].toInt() and 0xFF
                val b = paletteBytes[2].toInt() and 0xFF
                return Color(red = r, green = g, blue = b, alpha = 255)
            }
        }

        val rawColor = when {
            Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, themePath, "AccentColor") -> {
                Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, themePath, "AccentColor")
            }
            Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, dwmPath, "ColorizationColor") -> {
                Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, dwmPath, "ColorizationColor")
            }
            Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, dwmPath, "AccentColor") -> {
                Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, dwmPath, "AccentColor")
            }
            else -> return null
        }

        val r = rawColor and 0xFF
        val g = (rawColor shr 8) and 0xFF
        val b = (rawColor shr 16) and 0xFF
        val a = (rawColor shr 24) and 0xFF

        println("Accent color: $r, $g, $b, $a")
        Color(red = r, green = g, blue = b, alpha = a)
    } catch (e: Exception) {
        println("Accent not found")
        println(e.stackTraceToString())
        null
    }
}