package com.duoc.quickorder.msclientes.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.duoc.quickorder.msclientes.dto.LoginJWTDTO;
import com.duoc.quickorder.msclientes.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class AuthService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.time}")
    private long expirationTime;

    public ResponseDTO validator(LoginJWTDTO loginJWTDTO) {
        ResponseDTO response = new ResponseDTO();

        try {
            if ("admin".equals(loginJWTDTO.getUsername()) && "1234".equals(loginJWTDTO.getPassword())) {

                String token = JWT.create()
                        .withSubject(loginJWTDTO.getUsername())
                        .withClaim("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN"))
                        .withIssuedAt(new Date())
                        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                        .sign(Algorithm.HMAC256(secretKey));

                response.setRespuestaText(token);
                response.setRespuestaInteger(0);

            } else {
                response.setRespuestaText("Credenciales inválidas");
                response.setRespuestaInteger(2);
            }

        } catch (Exception e) {
            response.setRespuestaText("Error interno del servidor: " + e.getMessage());
            response.setRespuestaInteger(1);
        }

        return response;
    }
}
