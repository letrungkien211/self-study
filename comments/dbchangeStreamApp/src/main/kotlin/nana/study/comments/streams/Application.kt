package nana.study.comments.streams

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener

@SpringBootApplication
class Application {
    @Bean
    fun commandLineRunner() = CommandLineRunner {
        println("Hello World")
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
