package com.duoc.quickorder.mspedidos.controller;

import com.duoc.quickorder.mspedidos.dto.LoginJWTDTO;
import com.duoc.quickorder.mspedidos.dto.ResponseDTO;
import com.duoc.quickorder.mspedidos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Operaciones de inicio de sesión y obtención de token JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario con sus credenciales y retorna un token JWT para usar en los demás endpoints. " +
                      "Incluya el token en el encabezado: `Authorization: Bearer <token>`"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso - Token JWT generado",
            content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al generar el token", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Credenciales de acceso (usuario y contraseña)",
                required = true)
            @RequestBody LoginJWTDTO loginJWTDTO) {
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
