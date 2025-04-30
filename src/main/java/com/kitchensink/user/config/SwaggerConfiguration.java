package com.kitchensink.user.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(name = "Keycloak", openIdConnectUrl = "http://localhost:8080/realms/dive-dev/.well-known/openid-configuration", scheme = "bearer", type = SecuritySchemeType.OPENIDCONNECT, in = SecuritySchemeIn.HEADER)
public class SwaggerConfiguration {

}
