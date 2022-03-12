package nana.study.comments.controllers

import nana.study.comments.model.Comment
import nana.study.comments.service.CommentsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentsController constructor(private val service: CommentsService) {
    @GetMapping("/{id}")
    suspend fun getComment(@PathVariable id: String): Comment? = service.read(id)

    @PostMapping
    suspend fun createComment(@RequestBody comment: Comment) = service.create(comment)

    @PutMapping("/{id}")
    suspend fun updateComment(@PathVariable id: String, @RequestBody request: UpdateCommentRequest) {
        service.update(id, request.content)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteComment(@PathVariable id: String) = service.delete(id)

    @GetMapping("/parent/{parentId}")
    suspend fun findByParentId(
        @PathVariable parentId: String,
        @RequestParam pageSize: Int?,
        @RequestParam continuationToken: String?
    ) = service.findByParentId(parentId, pageSize ?: 20, continuationToken)

    data class UpdateCommentRequest(
        val content: String
    )
}