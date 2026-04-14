package com.cesae.dogbreeds

import android.app.Application
import com.cesae.dogbreeds.data.db.AppDatabase

class DogBreedsApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
