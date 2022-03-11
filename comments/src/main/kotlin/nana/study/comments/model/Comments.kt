package nana.study.comments.model

data class Comment internal constructor(
    val id: String?,
    val parentId: String,
    val content: String,
    val userId: String,
    val createdTime: Long? = null,
    val modifiedTime: Long? = null,
    val numReplies: Long? = null
)