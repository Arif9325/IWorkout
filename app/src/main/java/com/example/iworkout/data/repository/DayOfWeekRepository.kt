package com.example.iworkout.data.repository

import com.example.iworkout.data.model.DayOfWeek
import com.google.firebase.firestore.FirebaseFirestore

class DayOfWeekRepository {

    private val db = FirebaseFirestore.getInstance()

    fun addDays(days: List<DayOfWeek>, onComplete: (Boolean) -> Unit) {
        val batch = db.batch()
        days.forEach { day ->
            val doc = db.collection("days").document(day.dayId)
            batch.set(doc, day)
        }
        batch.commit()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getDays(onResult: (List<DayOfWeek>) -> Unit) {
        db.collection("days")
            .get()
            .addOnSuccessListener { documents ->
                val days = documents.map { it.toObject(DayOfWeek::class.java) }
                onResult(days)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
