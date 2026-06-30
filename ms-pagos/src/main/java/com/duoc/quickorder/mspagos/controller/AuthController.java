package com.duoc.quickorder.mspagos.controller;

import com.duoc.quickorder.mspagos.dto.LoginJWTDTO;
import com.duoc.quickorder.mspagos.dto.ResponseDTO;
import com.duoc.quickorder.mspagos.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginJWTDTO loginJWTDTO) {
        ResponseDTO response = authService.validator(loginJWTDTO);

        switch (response.getRespuestaInteger()) {
            case 0:
                return ResponseEntity.ok(response);
            case 1:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            case 2:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
