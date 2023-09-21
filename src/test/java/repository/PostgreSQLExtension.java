package repository;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLExtension implements BeforeAllCallback, AfterAllCallback {
    private PostgreSQLContainer<?> postgres;

    @Override
    public void beforeAll(ExtensionContext context) {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("project3")
                .withUsername("postgres")
                .withPassword("maxim")
                .withInitScript("db/NewTables.sql");

        postgres.start();
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
        System.setProperty("hibernate.driver_class", postgres.getDriverClassName());
    }

    @Override
    public void afterAll(ExtensionContext context) {
    }
}
