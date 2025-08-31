package co.com.crediya.application.api.config.security.utils;

import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {
  private static final String USER_ID = "userId";
  private static final String PASSWORD = "password";
  private static final String AUTHORITIES = "authorities";

  @Value("${jwt.secret}")
  private String jwtSecret;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public UsernamePasswordAuthenticationToken getAuthenticationInfo(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

    String username = claims.getSubject();

    List<?> rawRoles = claims.get(AUTHORITIES, List.class);
    List<String> roles = rawRoles.stream().map(String::valueOf).toList();

    UUID userId = UUID.fromString(claims.get(USER_ID, String.class));
    List<SimpleGrantedAuthority> authorities =
        roles.stream().map(SimpleGrantedAuthority::new).toList();

    CustomUserDetails userDetails = new CustomUserDetails(username, PASSWORD, userId, authorities);

    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}
