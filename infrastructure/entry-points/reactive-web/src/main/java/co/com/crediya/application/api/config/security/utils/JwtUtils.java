package co.com.crediya.application.api.config.security.utils;

import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import co.com.crediya.application.model.CommonConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {
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

    List<?> rawRoles = claims.get(CommonConstants.Fields.AUTHORITIES, List.class);
    List<String> roles = rawRoles.stream().map(String::valueOf).toList();

    UUID userId = UUID.fromString(claims.get(CommonConstants.Fields.USER_ID, String.class));
    List<SimpleGrantedAuthority> authorities =
        roles.stream().map(SimpleGrantedAuthority::new).toList();

    CustomUserDetails userDetails =
        new CustomUserDetails(username, CommonConstants.Fields.PASSWORD, userId, authorities);

    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}
