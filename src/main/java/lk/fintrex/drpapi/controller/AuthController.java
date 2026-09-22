package lk.fintrex.drpapi.controller;

import jakarta.validation.Valid;

import lk.fintrex.drpapi.dto.ApiResponse;
import lk.fintrex.drpapi.dto.TokenRequest;
import lk.fintrex.drpapi.dto.TokenResponse;
import lk.fintrex.drpapi.security.UsernamePasswordAuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsernamePasswordAuthService authService;

    public AuthController(
            UsernamePasswordAuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> token(
            @Valid @RequestBody TokenRequest request
    ) {

        TokenResponse token =
                authService.issueToken(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Authentication successful.",
                        token
                )
        );
    }
}
