package nana.study.comments.service

import nana.study.comments.datasource.ICommentsRepository
import nana.study.comments.model.Comment
import nana.study.comments.streams.MyRecord
import nana.study.comments.streams.dispatch
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommentsService(
    private val commentsDataSource: ICommentsRepository,
    private val kafkaProducer: Producer<String, MyRecord>
) {
    suspend fun create(comment: Comment) {
        val created = commentsDataSource.create(comment)
        kafkaProducer.dispatch(
            ProducerRecord(
                "foo",
                MyRecord(UUID.randomUUID().toString(), "CREATED", mapOf("newImage" to created))
            )
        )
    }

    suspend fun read(id: String) = commentsDataSource.read(id)

    suspend fun update(id: String, content: String) {
        commentsDataSource.update(id, content)
        kafkaProducer.dispatch(
            ProducerRecord(
                "foo",
                MyRecord(UUID.randomUUID().toString(), "UPDATED", mapOf("id" to id, "content" to content))
            )
        )
    }

    suspend fun delete(id: String) {
        commentsDataSource.delete(id)
        kafkaProducer.dispatch(
            ProducerRecord(
                "foo",
                MyRecord(UUID.randomUUID().toString(), "DELETED", mapOf("id" to id))
            )
        )
    }

    suspend fun findByParentId(parentId: String, pageSize: Int, continuationToken: String?) =
        commentsDataSource.findByParentId(parentId, pageSize, continuationToken)
}