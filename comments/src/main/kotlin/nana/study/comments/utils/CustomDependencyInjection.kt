package nana.study.comments.utils

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.InstantSource

@Configuration
class CustomDependencyInjection {
    @Bean
    fun instanceSource(): InstantSource = InstantSource.system()
}