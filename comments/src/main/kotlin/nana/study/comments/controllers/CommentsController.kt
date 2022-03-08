package nana.study.comments.controllers

import nana.study.comments.datasource.ICommentsRepository
import nana.study.comments.model.Comment
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentsController constructor(private val repository: ICommentsRepository) {
    @GetMapping("/{id}")
    suspend fun getComment(@PathVariable id: String): Comment? = repository.read(id)

    @PostMapping
    suspend fun createComment(@RequestBody comment: Comment) = repository.create(comment)

    @PutMapping("/{id}")
    suspend fun updateComment(@PathVariable id: String, @RequestBody request: UpdateCommentRequest) {
        repository.update(id, request.text)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteComment(@PathVariable id: String) = repository.delete(id)

    @GetMapping("/parent/{parentId}")
    suspend fun findByParentId(
        @PathVariable parentId: String,
        @RequestParam pageSize: Int?,
        @RequestParam continuationToken: String?
    ) = repository.findByParentId(parentId, pageSize ?: 20, continuationToken)

    data class UpdateCommentRequest(
        val text: String
    )
}