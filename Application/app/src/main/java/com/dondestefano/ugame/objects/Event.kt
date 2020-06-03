package com.dondestefano.ugame.objects

import com.dondestefano.ugame.data_managers.EventDataManager
import java.io.Serializable
import java.util.*

data class Event(var name: String? = null,
                 var date: Date? = null,
                 var attend: Boolean? = null,
                 var new: Boolean? = null,
                 var keyName: String? = null,
                 var host: String? = null,
                 var invitedUsers: MutableList<String>? = null ): Serializable {

    fun changeAttend(status: Boolean?) {
        // Permanently set the event as no longer new.
        new = false

        if (status != null) {
            attend = status
        } else { attend = !attend!! }

        // Change attend
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }

    ///////// CHECK IF I NEED THESE
    fun changeName(name : String) {
        this.name = name
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }

    fun changeDate(date : Date) {
        this.date = date
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }
}