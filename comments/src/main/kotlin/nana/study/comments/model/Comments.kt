package nana.study.comments.model

data class Comment internal constructor(
    val id: String?,
    val parentId: String,
    val content: String,
    val userId: String,
    val createdTime: Long?,
    val modifiedTime: Long?
)

data class ParentStats(val parentId: String, val numComments: Long)