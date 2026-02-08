package com.backendRole.assignment.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Filter responsible for validating JWT tokens in incoming requests.
 * It extracts the Bearer token from the Authorization header, verifies it using
 * an RSA Public Key,
 * and extracts the user ID to be used in the application.
 */
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${app.jwt.public-key}")
    private String publicKeyStr;

    private PublicKey publicKey;

    /**
     * Filters incoming requests to validate JWT tokens.
     * Skips validation for public endpoints like Swagger UI and Actuator.
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the filter chain to proceed with
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // Skip JWT validation for public/system endpoints
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/actuator")) {
            log.debug("Skipping JWT validation for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (publicKey == null) {
                    log.info("Loading RSA Public Key for JWT validation...");
                    loadPublicKey();
                }

                log.debug("Attempting to parse and validate JWT token...");
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(publicKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.getSubject();
                log.info("Successfully validated JWT for user: {}", userId);

                // Inject userId into request header via a wrapper to make it accessible to
                // controllers
                HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
                requestWrapper.addHeader("x-user-id", userId);

                filterChain.doFilter(requestWrapper, response);
                return;

            } catch (JwtException | IllegalArgumentException e) {
                log.warn("JWT validation failed for path {}: {}", path, e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid or Expired Token: " + e.getMessage());
                return;
            } catch (Exception e) {
                log.error("Unexpected error during JWT processing for path {}: {}", path, e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Internal Server Error: Key Error");
                return;
            }
        }

        log.warn("Missing or invalid Authorization header for path: {}", path);
        // If no token or invalid format, return 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Missing Bearer Token");
    }

    /**
     * Loads the RSA Public Key from the configured string.
     * Cleans the PEM format and decodes the Base64 key.
     *
     * @throws Exception if key conversion fails
     */
    private void loadPublicKey() throws Exception {
        try {
            String safeKey = publicKeyStr
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(safeKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.publicKey = kf.generatePublic(spec);
            log.info("RSA Public Key loaded successfully.");
        } catch (Exception e) {
            log.error("Failed to load RSA Public Key: {}", e.getMessage(), e);
            throw e;
        }
    }
}
