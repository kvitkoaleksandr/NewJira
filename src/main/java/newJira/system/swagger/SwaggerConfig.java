package newJira.system.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.ui.AbstractSwaggerResourceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    @Bean
    public AbstractSwaggerResourceResolver swaggerResourceResolver(
            SwaggerUiConfigProperties swaggerUiConfigProperties) {
        return new AbstractSwaggerResourceResolver(swaggerUiConfigProperties);
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("newJira-system")
                .packagesToScan("newJira.system")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("NewJira System API")
                        .description("NewJira system API documentation")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(new Server().url("/")));
    }

    @Bean
    public GroupedOpenApi swaggerUi() {
        return GroupedOpenApi.builder()
                .group("swagger-ui")
                .pathsToMatch("/swagger-ui/**")
                .build();
    }
}