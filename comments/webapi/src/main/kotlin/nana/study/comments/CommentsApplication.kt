package nana.study.comments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@SpringBootApplication
class CommentsApplication

fun main(args: Array<String>) {
	runApplication<CommentsApplication>(*args)
}
