package com.example.getitdone.add_update_task

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class GeofenceReceiver:BroadcastReceiver() {

    lateinit var key:String
    lateinit var message:String

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context!=null){
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofenceTransition = geofencingEvent.geofenceTransition

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
                if (intent!=null){
                    key = intent.getStringExtra("key")!!
                    message = intent.getStringExtra("message")!!
                }

                val firebase = Firebase.database
                val reference = firebase.getReference("reminders")
                val reminderLister = object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reminder = snapshot.getValue<Reminder>()
                        if (reminder!=null){
                            MapActivity.showNotification(
                                context.applicationContext,
                                message
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("ReminderISCanceled ${error.details}")
                    }

                }

                val child = reference.child(key)
                child.addValueEventListener(reminderLister)

            }

        }

    }


}