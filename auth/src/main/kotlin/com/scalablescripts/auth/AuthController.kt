package com.scalablescripts.auth

import com.scalablescripts.auth.dtos.LoginDTO
import com.scalablescripts.auth.dtos.MessageDTO
import com.scalablescripts.auth.dtos.RegisterDTO
import com.scalablescripts.auth.models.User
import com.scalablescripts.auth.services.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api")
class AuthController(private val userService: UserService) {
    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<User> {
        val user = User()
        user.name = body.name
        user.email = body.email
        user.password = body.password
        return ResponseEntity.ok(userService.save(user))
    }

    @PostMapping("login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse) : ResponseEntity<Any> {
        val user = userService.findByEmail(body.email)
                ?: return ResponseEntity.badRequest().body(MessageDTO("User not found."))
        if(!user.comparePassword(body.password)){
            return ResponseEntity.badRequest().body(MessageDTO("Invalid password."))
        }

        val issuer = user.id.toString()
        val jwt = Jwts.builder().setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 3600*24*1000)) // 1 day
                .signWith(SignatureAlgorithm.HS512, "ZmtkanNhZmRzYWY=").compact()
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return ResponseEntity.ok(MessageDTO("Success"))
    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String?) : ResponseEntity<Any> {
        if(jwt==null){
            return ResponseEntity.status(401).body(MessageDTO("Unauthenticated"))
        }

        val body = Jwts.parser().setSigningKey("ZmtkanNhZmRzYWY=").parseClaimsJws(jwt).body

        return ResponseEntity.ok(this.userService.getById(body.issuer.toInt()));
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any>{
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity.ok(MessageDTO("Success"))
    }
}