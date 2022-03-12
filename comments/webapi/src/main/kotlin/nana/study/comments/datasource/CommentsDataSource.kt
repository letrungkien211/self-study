package nana.study.comments.datasource

import nana.study.comments.model.Comment

interface ICommentsRepository  {
    suspend fun create(comment: Comment): Comment
    suspend fun read(id: String): Comment?
    suspend fun update(id: String, content: String)
    suspend fun delete(id: String)
    suspend fun findByParentId(parentId: String, pageSize: Int = 20, startId: String? = null): Pair<Collection<Comment>, String?>
}

interface IStatsRepository {
    suspend fun getCommentsCount(parentId: String) : Int
}