package com.hilamiks.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}


	@Bean
	public RouteLocator customRoutes(RouteLocatorBuilder builder) {
		return builder
			.routes()
				.route(p -> p
					.path("/somebank/accounts/**")
					.filters(f ->
						f.rewritePath("/somebank/accounts/?(?<segment>.*)","/${segment}")
							.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
							.circuitBreaker(config -> {
								config.setName("accountsCircuitBreaker")
									.setFallbackUri("forward:/contactSupport")
								;
							}))
					.uri("lb://ACCOUNTS")
				)
				.route(p -> p
					.path("/somebank/cards/**")
					.filters(f ->
						f.rewritePath("/somebank/cards/?(?<segment>.*)","/${segment}")
							.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
							.retry(config -> {
								config.setRetries(3)
									.setMethods(HttpMethod.GET)
									.setBackoff(
										Duration.ofMillis(100L),
										Duration.ofMillis(1000L),
										2,
										true);
							}))
					.uri("lb://CARDS")
				)
				.route(p -> p
					.path("/somebank/loans/**")
					.filters(f ->
						f.rewritePath("/somebank/loans/?(?<segment>.*)","/${segment}")
							.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
							.requestRateLimiter(config -> {
								config
									.setRateLimiter(redisRateLimiter())
									.setKeyResolver(keyResolver());
							}))
					.uri("lb://LOANS")
				)
			.build();
	}

	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(
			id -> {
				return new Resilience4JConfigBuilder(id)
					.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
					.timeLimiterConfig(TimeLimiterConfig.custom()
						.timeoutDuration(Duration.ofSeconds(20))
						.build())
					.build();
			}
		);
	}

	@Bean
	public RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 1, 1);
	}

	@Bean
	public KeyResolver keyResolver() {
		return exchange -> Mono.justOrEmpty(
			exchange.getRequest().getHeaders().getFirst("user")
		).defaultIfEmpty("anonymous");
	}
}
