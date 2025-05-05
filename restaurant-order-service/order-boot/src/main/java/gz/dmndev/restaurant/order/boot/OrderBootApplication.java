package gz.dmndev.restaurant.order.boot;

import gz.dmndev.restaurant.common.security.EnableResourceServerSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServerSecurity
@EntityScan(
    basePackages = "gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.entity")
@ComponentScan(basePackages = {"gz.dmndev.restaurant"})
@EnableFeignClients(basePackages = "gz.dmndev.restaurant.order.infrastructure.adapter.out.rest")
public class OrderBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderBootApplication.class, args);
  }
}
