package co.com.crediya.application.api.config.security.utils;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomUserDetails extends User {
  private final UUID userId;

  public CustomUserDetails(
      String username,
      String password,
      UUID userId,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.userId = userId;
  }

  public static CustomUserDetails of(UserDetails user, UUID userId) {
    return new CustomUserDetails(
        user.getUsername(), user.getPassword(), userId, user.getAuthorities());
  }
}
