package nana.study.comments.datasource

import aws.sdk.kotlin.runtime.auth.credentials.Credentials
import aws.sdk.kotlin.runtime.auth.credentials.CredentialsProvider
import aws.sdk.kotlin.runtime.endpoint.AwsEndpoint
import aws.sdk.kotlin.runtime.endpoint.AwsEndpointResolver
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nana.study.comments.model.Comment
import nana.study.comments.utils.CustomException
import nana.study.comments.utils.CustomExceptionType.ALREADY_EXIST
import nana.study.comments.utils.CustomExceptionType.NOT_EXIST
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Repository
import java.time.InstantSource
import java.util.*
import kotlin.time.ExperimentalTime

@Configuration
class DynamoDbConfiguration {
    @Bean
    fun dynamoDbClient() = DynamoDbClient {
        endpointResolver = AwsEndpointResolver { _, _ -> AwsEndpoint("http://localhost:8000") }
        region = "ap-north-east-1"
        credentialsProvider = object : CredentialsProvider {
            override suspend fun getCredentials() = Credentials("Dummy", "Dummy")
        }
    }
}

@ExperimentalTime
@Repository
class DynamoDbCommentsRepository @Autowired constructor(
    private val dynamoDbClient: DynamoDbClient,
    private val timeSource: InstantSource,
    private val commentsTableName: String = "Comments"
) : ICommentsRepository {
    override suspend fun create(comment: Comment): Comment {
        val now = timeSource.instant().toEpochMilli()
        with(
            comment.copy(
                id = comment.id ?: UUID.randomUUID().toString(),
                createdTime = now,
                modifiedTime = now
            )
        ) {
            val request = PutItemRequest {
                tableName = commentsTableName
                item = mapOf(
                    "id" to AttributeValue.S(id ?: UUID.randomUUID().toString()),
                    "userId" to AttributeValue.S(userId),
                    "parentId" to AttributeValue.S(parentId),
                    "content" to AttributeValue.S(content),
                    "createdTime" to AttributeValue.N(createdTime.toString()),
                    "modifiedTime" to AttributeValue.N(modifiedTime.toString())
                )
                conditionExpression = "attribute_not_exists(id)"
            }
            try {
                dynamoDbClient.putItem(request)
            } catch (ex: ConditionalCheckFailedException) {
                throw CustomException(ALREADY_EXIST, "Cannot create item that already exists. id=$id")
            }
            return this
        }
    }

    override suspend fun read(id: String): Comment? {
        var request = GetItemRequest {
            tableName = commentsTableName
            key = mapOf(
                "id" to AttributeValue.S(id)
            )
        }

        return dynamoDbClient.getItem(request).item?.toComment()
    }

    override suspend fun update(id: String, content: String) {
        try {
            dynamoDbClient.updateItem(UpdateItemRequest {
                tableName = commentsTableName
                key = mapOf("id" to AttributeValue.S(id))
                updateExpression = "SET content = :c, modifiedTime = :m"
                expressionAttributeValues =
                    mapOf(
                        ":c" to AttributeValue.S(content),
                        ":m" to AttributeValue.N(timeSource.instant().toEpochMilli().toString())
                    )
                conditionExpression = "attribute_exists(id)"
            })
        } catch (ex: ConditionalCheckFailedException) {
            throw CustomException(NOT_EXIST, "Cannot modify item that doesn't exist. id=$id")
        }
    }

    override suspend fun delete(id: String) {
        dynamoDbClient.deleteItem(DeleteItemRequest {
            tableName = commentsTableName
            key = mapOf("id" to AttributeValue.S(id))
        })
    }

    override suspend fun findByParentId(
        parentId: String,
        pageSize: Int,
        continuationToken: String?
    ): Pair<Collection<Comment>, String?> {
        @Serializable
        data class ContinuationToken(val id: String, val parentId: String, val createdTime: Long) {
            fun toMapOf(): Map<String, AttributeValue> = mapOf(
                "id" to AttributeValue.S(id),
                "parentId" to AttributeValue.S(this.parentId),
                "createdTime" to AttributeValue.N(createdTime.toString())
            )

            fun toBase64() = Base64.getEncoder().encodeToString(Json.encodeToString(this).toByteArray())
        }

        val startKey = continuationToken?.let {
            Json.decodeFromString<ContinuationToken>(String(Base64.getDecoder().decode(it))).toMapOf()
        }

        val request = QueryRequest {
            tableName = commentsTableName
            indexName = "CommentsByParentId"
            keyConditionExpression = "parentId = :p"
            expressionAttributeValues = mapOf(":p" to AttributeValue.S(parentId))
            limit = pageSize
            exclusiveStartKey = startKey
        }

        val response = dynamoDbClient.query(request)
        val items = response.items?.map { it.toComment() } ?: listOf()

        val continuationTokenBase64 = response.lastEvaluatedKey?.let {
            ContinuationToken(
                it["id"].getString(),
                it["parentId"].getString(),
                it["createdTime"].getLong()
            ).toBase64()
        }
        return Pair(items, continuationTokenBase64)
    }

    private fun Map<String, AttributeValue>.toComment() = Comment(
        this["id"]?.getString(),
        this["parentId"].getString(),
        this["content"].getString(),
        this["userId"].getString(),
        this["createdTime"].getLong(),
        this["modifiedTime"].getLong()
    )

    private fun AttributeValue?.getString() = (this as AttributeValue.S).value
    private fun AttributeValue?.getLong() = (this as AttributeValue.N).value.toLong()

}