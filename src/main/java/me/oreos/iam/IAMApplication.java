package me.oreos.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication(scanBasePackages = { "me.oreos.iam", "org.wakanda.framework" })
@Configuration
@ConfigurationPropertiesScan("me.oreos.iam.config")
@OpenAPIDefinition(
    info = @Info(
        title = "Wakanda IAM",
        version = "1.0.0",
        description = "Wakanda Identity and Access Management"
    )
)
@EntityScan(basePackages = { "me.oreos.iam.entities", "org.wakanda.framework.entity" })
@EnableJpaRepositories(basePackages = "me.oreos.iam.repositories")
@EnableTransactionManagement
public class IAMApplication {
    public static void main(String[] args) {
        SpringApplication.run(IAMApplication.class, args);
        System.out.println("Starting Wakanda IAM Application...");
    }
}
