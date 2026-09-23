package id.bubakangreen.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class BubakanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase offline persistence safely if Firebase is initialized
        try {
            if (FirebaseApp.getApps(this).isNotEmpty()) {
                val firestore = FirebaseFirestore.getInstance()
                val settings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(
                        PersistentCacheSettings.newBuilder()
                            .setSizeBytes(100 * 1024 * 1024) // 100MB offline cache
                            .build()
                    )
                    .build()
                firestore.firestoreSettings = settings
            }
        } catch (_: Exception) {
            // Firebase not yet configured via google-services.json
        }
    }
}
