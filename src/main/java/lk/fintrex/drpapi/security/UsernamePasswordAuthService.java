package lk.fintrex.drpapi.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import lk.fintrex.drpapi.dto.TokenRequest;
import lk.fintrex.drpapi.dto.TokenResponse;
import lk.fintrex.drpapi.exception.InvalidCredentialsException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UsernamePasswordAuthService {

    private final JwtService jwtService;

    private final String configuredUsername;
    private final String configuredPassword;
    private final String configuredScope;

    public UsernamePasswordAuthService(
            JwtService jwtService,
            @Value("${drp.api.username}") String configuredUsername,
            @Value("${drp.api.password}") String configuredPassword,
            @Value("${drp.api.scope}") String configuredScope
    ) {
        this.jwtService = jwtService;
        this.configuredUsername = configuredUsername;
        this.configuredPassword = configuredPassword;
        this.configuredScope = configuredScope;
    }

    public TokenResponse issueToken(
            TokenRequest request
    ) {

        if (!safeEquals(
                configuredUsername,
                request.username()
        ) || !safeEquals(
                configuredPassword,
                request.password()
        )) {

            throw new InvalidCredentialsException(
                    "Invalid username or password."
            );
        }

        String token =
                jwtService.generateAccessToken(
                        configuredUsername,
                        configuredScope
                );

        return new TokenResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                configuredScope
        );
    }

    private boolean safeEquals(
            String expected,
            String supplied
    ) {

        if (expected == null || supplied == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8)
        );
    }
}
