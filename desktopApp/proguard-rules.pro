# ProGuard configuration for Compose Desktop Release packaging

# Ignore unresolved references, notes and warnings from 3rd party libraries
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

# Keep Coroutines & Serialization
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.datetime.** { *; }

# Keep Koin
-keep class io.insert.koin.** { *; }
-keep class org.koin.** { *; }

# Keep Apache POI & XMLBeans
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.openxmlformats.schemas.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }

# Keep Database drivers and ORM (Exposed, HikariCP, PostgreSQL, H2)
-keep class org.jetbrains.exposed.** { *; }
-keep class com.zaxxer.hikari.** { *; }
-keep class org.postgresql.** { *; }
-keep class org.h2.** { *; }
-keep class java.sql.** { *; }
-keep class javax.sql.** { *; }

# Keep SLF4J / logging
-keep class org.slf4j.** { *; }
-keep class org.apache.commons.logging.** { *; }
