package newJira.system;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "newJira.system.repository")
@EntityScan(basePackages = "newJira.system.entity")
@EnableWebMvc
@OpenAPIDefinition
public class NewJira {
    public static void main(String[] args) {
        SpringApplication.run(NewJira.class, args);
    }
}