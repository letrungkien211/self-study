package nana.study.comments.streams

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class MyRecord(val eventID: String, val eventName: String, val metadata: Map<String, @Contextual Any>)