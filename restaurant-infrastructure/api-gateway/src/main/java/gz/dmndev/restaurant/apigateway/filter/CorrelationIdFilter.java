package gz.dmndev.restaurant.apigateway.filter;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

  private static final String CORRELATION_ID = "X-Correlation-Id";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    // Check if correlation ID already exists
    if (request.getHeaders().containsKey(CORRELATION_ID)) {
      return chain.filter(exchange);
    }

    // Generate new correlation ID
    String correlationId = UUID.randomUUID().toString();

    // Add correlation ID to request headers
    ServerHttpRequest requestWithCorrelationId =
        request.mutate().header(CORRELATION_ID, correlationId).build();

    // Replace the request with our modified request
    return chain.filter(exchange.mutate().request(requestWithCorrelationId).build());
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
