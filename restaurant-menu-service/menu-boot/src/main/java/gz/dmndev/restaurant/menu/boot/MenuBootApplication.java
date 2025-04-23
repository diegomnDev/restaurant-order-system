package gz.dmndev.restaurant.menu.boot;

import gz.dmndev.restaurant.commonsecurity.EnableResourceServerSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServerSecurity
@ComponentScan(basePackages = {"gz.dmndev.restaurant"})
public class MenuBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MenuBootApplication.class, args);
  }
}
