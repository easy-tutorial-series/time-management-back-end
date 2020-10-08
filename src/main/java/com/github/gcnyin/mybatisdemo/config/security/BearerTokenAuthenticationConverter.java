package com.github.gcnyin.mybatisdemo.config.security;

import com.github.gcnyin.mybatisdemo.mapper.TokenMapper;
import com.github.gcnyin.mybatisdemo.mapper.UserMapper;
import com.github.gcnyin.mybatisdemo.model.Token;
import com.github.gcnyin.mybatisdemo.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class BearerTokenAuthenticationConverter implements AuthenticationConverter {
  private final TokenMapper tokenMapper;
  private final UserMapper userMapper;
  public static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

  public BearerTokenAuthenticationConverter(TokenMapper tokenMapper, UserMapper userMapper) {
    this.tokenMapper = tokenMapper;
    this.userMapper = userMapper;
  }

  @Override
  public BearerAuthenticationToken convert(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null) {
      return null;
    }
    header = header.trim();
    if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BEARER)) {
      return null;
    }

    if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
      throw new BadCredentialsException("Empty bearer authentication token");
    }
    String tokenId = header.substring(AUTHENTICATION_SCHEME_BEARER.length() + 1);
    tokenMapper.touchToken(tokenId);
    Token token = tokenMapper.findById(tokenId);
    User user = userMapper.findById(token.getUserId());
    return new BearerAuthenticationToken(user.getName());
  }
}
