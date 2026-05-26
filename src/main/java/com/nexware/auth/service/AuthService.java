package com.nexware.auth.service;

import com.nexware.auth.dto.AuthResponse;
import com.nexware.auth.dto.LoginRequest;
import com.nexware.auth.dto.LogoutRequest;
import com.nexware.auth.dto.RefreshTokenRequest;
import com.nexware.auth.dto.RegisterRequest;
import com.nexware.auth.dto.UserProfileResponse;
import com.nexware.auth.entity.RefreshToken;
import com.nexware.auth.exception.InvalidTokenException;
import com.nexware.auth.mapper.AuthMapper;
import com.nexware.auth.repository.RefreshTokenRepository;
import com.nexware.auth.security.AuthenticatedUser;
import com.nexware.config.ApplicationProperties;
import com.nexware.shared.exception.BusinessRuleViolationException;
import com.nexware.shared.exception.ConflictException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.user.entity.Role;
import com.nexware.user.entity.RoleName;
import com.nexware.user.entity.User;
import com.nexware.user.repository.RoleRepository;
import com.nexware.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int REFRESH_TOKEN_BYTES = 64;
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthMapper authMapper;
    private final ApplicationProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Clock clock = Clock.systemUTC();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new ConflictException("Email is already registered");
        }

        Role defaultRole = roleRepository.findByName(RoleName.INVENTORY_STAFF)
                .orElseThrow(() -> new BusinessRuleViolationException("Default user role is not configured"));

        User user = User.register(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName().trim(),
                request.lastName().trim(),
                request.phoneNumber(),
                Set.of(defaultRole)
        );

        User savedUser = userRepository.save(user);
        return issueTokens(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        user.recordSuccessfulLogin(Instant.now(clock));

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.refreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid"));

        Instant now = Instant.now(clock);
        if (!refreshToken.isActive(now)) {
            throw new InvalidTokenException("Refresh token is expired or revoked");
        }

        refreshToken.revoke(now);
        return issueTokens(refreshToken.getUser());
    }

    @Transactional
    public void logout(LogoutRequest request, String authorizationHeader) {
        refreshTokenRepository.findByTokenHash(hashToken(request.refreshToken()))
                .ifPresent(token -> token.revoke(Instant.now(clock)));

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String accessToken = authorizationHeader.substring(BEARER_PREFIX.length());
            try {
                tokenBlacklistService.blacklist(jwtService.extractTokenId(accessToken), jwtService.extractExpiration(accessToken));
            } catch (JwtException | IllegalArgumentException ignored) {
                // Logout remains idempotent even if the caller supplies an already invalid access token.
            }
        }
    }

    @Transactional(readOnly = true)
    public UserProfileResponse currentUser(AuthenticatedUser principal) {
        User user = userRepository.findByIdAndDeletedAtIsNull(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
        return authMapper.toUserProfile(user);
    }

    private AuthResponse issueTokens(User user) {
        AuthenticatedUser principal = AuthenticatedUser.from(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = generateRefreshToken();
        Instant now = Instant.now(clock);
        Instant refreshExpiresAt = now.plus(properties.security().jwt().refreshTokenTtl());

        refreshTokenRepository.save(RefreshToken.create(user, hashToken(refreshToken), refreshExpiresAt, now));

        return AuthResponse.bearer(
                accessToken,
                refreshToken,
                jwtService.accessTokenExpiresInSeconds(),
                authMapper.toUserProfile(user)
        );
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
