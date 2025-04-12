package com.mono.backend.event

import com.mono.backend.dataserializer.DataSerializer

data class Event<T : EventPayload>(
    val eventId: Long? = null,
    val type: EventType? = null,
    val payload: T? = null
) {

    fun toJson(): String? {
        return DataSerializer.serialize(this)
    }

    companion object {
        fun fromJson(json: String?): Event<EventPayload>? {
            val eventRaw: EventRaw? = DataSerializer.deserialize(json, EventRaw::class.java)
            return eventRaw?.let {
                Event(
                    eventId = it.eventId,
                    type = EventType.from(it.type),
                    payload = DataSerializer.deserialize(it.payload, EventType.from(it.type)?.payloadClass)
                )
            }
        }

        private data class EventRaw(
            val eventId: Long = 0,
            val type: String = "",
            val payload: Any? = null
        )
    }
}