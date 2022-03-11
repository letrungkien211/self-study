package nana.study.comments.streams

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.apache.kafka.clients.producer.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Configuration
class KafkaConfig(private val env: Environment) {
    @Bean
    fun kafkaProducer(): Producer<String, MyRecord> =
        DefaultKafkaProducerFactory<String, MyRecord>(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to env["spring.kafka.producer.bootstrap-servers"],
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.qualifiedName
            )
        ).createProducer()
}


@Serializable
data class MyRecord(val eventID: String, val eventName: String, val metadata: Map<String, @Contextual Any>)

suspend inline fun <reified K : Any, reified V : Any> Producer<K, V>.dispatch(record: ProducerRecord<K, V>) =
    kotlin.coroutines.suspendCoroutine<RecordMetadata> { continuation ->
        val callback = Callback { metadata, exception ->
            if (metadata == null) {
                continuation.resumeWithException(exception!!)
            } else {
                continuation.resume(metadata)
            }
        }
        this.send(record, callback)
    }