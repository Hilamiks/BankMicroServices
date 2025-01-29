package com.hilamiks.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http

//            .authorizeExchange(exchange ->
//                exchange.pathMatchers(HttpMethod.GET).permitAll()
//                    .pathMatchers("/somebank/accounts/**").authenticated()
//                    .pathMatchers("/somebank/loans/**").authenticated()
//                    .pathMatchers("/somebank/cards/**").authenticated()
//            )

            .authorizeExchange(exchange ->
                exchange.pathMatchers(HttpMethod.GET).permitAll()
                    .pathMatchers("/somebank/accounts/**").hasRole("ACCOUNTS")
                    .pathMatchers("/somebank/loans/**").hasRole("LOANS")
                    .pathMatchers("/somebank/cards/**").hasRole("CARDS")
            )

//            .oauth2ResourceServer(
//                oAuth2ResourceServerSpec ->
//                    oAuth2ResourceServerSpec.jwt(Customizer.withDefaults())
//            )

            .oauth2ResourceServer(
                oAuth2ResourceServerSpec ->
                    oAuth2ResourceServerSpec.jwt(
                        jwtSpec -> jwtSpec.jwtAuthenticationConverter(
                            grantedAuthoritiesExtractor()
                        ))
            )

            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            .build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
            new KeycloakRoleConverter()
        );
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
