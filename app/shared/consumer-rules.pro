# The navigation keys, kept whole as androidx.annotation.Keep kept them while they were in :app.
# commonMain cannot carry that annotation: androidx.annotation declares Keep for the JVM only.
-keep class * implements org.leviathan941.retrodromcompanion.ui.navigation.AppDestination {
    *;
}
