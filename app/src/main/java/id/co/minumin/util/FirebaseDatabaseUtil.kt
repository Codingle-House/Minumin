package id.co.minumin.util

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Created by pertadima on 16,February,2021
 */

object FirebaseDatabaseUtil {
    private val db = Firebase.database

    fun setSuggestionValue(suggestion: String) {
        val myRef = db.reference.child(System.currentTimeMillis().toString()).child("suggestion")
        myRef.setValue(suggestion)
    }
}