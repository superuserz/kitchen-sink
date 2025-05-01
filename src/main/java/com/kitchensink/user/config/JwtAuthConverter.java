package com.kitchensink.user.config;

import org.springframework.stereotype.Component;

@Component
public class JwtAuthConverter {
//implements Converter<Jwt, AbstractAuthenticationToken> {
//
//	@Override
//	public AbstractAuthenticationToken convert(Jwt jwt) {
//		Collection<GrantedAuthority> roles = extractAuthorities(jwt);
//		return new JwtAuthenticationToken(jwt, roles);
//	}
//
//	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
//		if (jwt.getClaim("realm_access") != null) {
//			Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//			ObjectMapper mapper = new ObjectMapper();
//			List<String> keycloakRoles = mapper.convertValue(realmAccess.get("roles"), List.class);
//			List<GrantedAuthority> roles = new ArrayList<>();
//			for (String keycloakRole : keycloakRoles) {
//				roles.add(new SimpleGrantedAuthority(keycloakRole));
//			}
//			return roles;
//		}
//		return new ArrayList<>();
//	}
//
//	@Bean
//	DefaultMethodSecurityExpressionHandler msecurity() {
//		DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
//		defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
//		return defaultMethodSecurityExpressionHandler;
//	}
}