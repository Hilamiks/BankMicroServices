package com.hilamiks.gatewayserver.filter;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class ResponseTraceFilter {

    private static final Logger log = LoggerFactory.getLogger(ResponseTraceFilter.class);

    private final FilterUtility filterUtility;


    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) ->
            chain.filter(exchange)
                .then(
                    Mono.fromRunnable(() -> {
                        HttpHeaders headers = exchange.getRequest().getHeaders();
                        String correlationId = filterUtility.getCorrelationId(headers);

                        if (!exchange.getResponse().getHeaders().containsKey(filterUtility.CORRELATION_ID)) {
                            log.debug("Updated correlation id to the outbound header: {}", correlationId);
                            exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
                        }
                    })
                );
    }

}
