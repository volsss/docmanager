# ProGuard configuration for Compose Desktop Release packaging

-dontwarn **
-dontnote **
-ignorewarnings

# Keep attributes needed for Kotlin reflection, annotations, serialization, etc.
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod,SourceFile,LineNumberTable,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations

# Keep application code
-keep class ru.example.docmanager.** { *; }

# Keep Compose & Skiko
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }
-keep class org.jetbrains.skiko.** { *; }

# Keep Coroutines
-keep class kotlinx.datetime.** { *; }

# Keep Koin
-keep class org.koin.** { *; }

# Keep Apache POI
-keep class org.apache.poi.** { *; }

# Keep Database drivers and ORM (Exposed, PostgreSQL, H2)
-keep class org.jetbrains.exposed.** { *; }
-keep class org.postgresql.** { *; }
-keep class org.h2.** { *; }
-keep class java.sql.** { *; }
-keep class javax.sql.** { *; }

# Keep SLF4J / logging
-keep class org.slf4j.** { *; }
-keep class org.apache.commons.logging.** { *; }
