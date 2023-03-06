package com.example.websocketserver

import io.jsonwebtoken.Jwts
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class ChatHandler : TextWebSocketHandler() {
    companion object {
        val sessionsToUserId : ConcurrentMap<String, String> = ConcurrentHashMap()
        val userIdToSessions : ConcurrentMap<String, WebSocketSession> = ConcurrentHashMap()
    }
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val userId = extractUserId(session)
        if(userId!=null) {
            userIdToSessions[userId] = session
            sessionsToUserId[session.id] = userId
            session.sendMessage(TextMessage("Authenticated! UserId=${userId}"))
        }
        else{
            session.sendMessage(TextMessage("Invalid token!"))
            session.close(CloseStatus.BAD_DATA)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        extractUserId(session)?.apply {
            userIdToSessions.remove(this)
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println(message.payload)
        val splits = message.payload.split(' ')
        if(splits.count()>1){
            val userId = splits[0]
            val textMessage = message.payload.substring(splits[0].length+1)
            userIdToSessions[userId]?.let {
                it.sendMessage(TextMessage("From ${sessionsToUserId[session.id]}: $textMessage"))
            }?: run {
                session.sendMessage(TextMessage("Invalid UserId=$userId"))
            }
        }
    }

    private fun extractUserId(session: WebSocketSession) : String? {
        var jwt: String? = session.handshakeHeaders["Authorization"]?.firstOrNull()?.split(' ')?.let {
            if(it.count()>1)
                it[1]
            else
                null
        } ?: return null

        // Validate jwtToken
        return try {
            Jwts.parser().setSigningKey("ZmtkanNhZmRzYWY=").parseClaimsJws(jwt).body.subject
        }
        catch (ex : Exception){
            println(ex)
            null
        }
    }
}

@Configuration @EnableWebSocket
class WSConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(ChatHandler(), "api/chat")
    }
}