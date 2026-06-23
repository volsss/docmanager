package tech.ilug.documentmanager

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.platform.LocalContext
import org.koin.android.ext.koin.androidContext
import org.koin.compose.getKoin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import tech.ilug.documentmanager.di.sharedModule
import tech.ilug.documentmanager.ui.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(0, 0)
        )

        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            if (GlobalContext.getOrNull() == null) {
                startKoin {
                    androidContext(this@MainActivity)
                    modules(sharedModule)
                }
            }

            val useDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

            val colorScheme = when {
                useDynamicColor && isSystemInDarkTheme() -> dynamicDarkColorScheme(context)
                useDynamicColor -> dynamicLightColorScheme(context)
                else -> MaterialTheme.colorScheme
            }

            MaterialTheme (colorScheme) {
                App()
            }
        }
    }
}