package ru.example.docmanager.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.char

object Utils {
    const val JVM_H2_JDBC = "jdbc:h2:~/ru.example.docmanager/documents;MODE=MySQL"
    const val ANDROID_H2_JDBC = "jdbc:h2:/data/data/ru.example.docmanager/documents;MODE=MySQL"
    const val DEFAULT_POSTGRES_JDBC = "jdbc:postgresql://localhost:5432/documents"

    const val H2_DRIVER = "org.h2.Driver"
    const val POSTGRES_DRIVER = "org.postgresql.Driver"

    val DATE_FORMAT = LocalDate.Format {
        day(); char('.'); monthNumber(); char('.'); year()
    }

    const val WIDE_BREAKPOINT = 840
}