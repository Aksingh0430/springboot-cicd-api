package com.devops.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * DataInitializer is intentionally empty for production.
 *
 * Sample data is loaded via Flyway migration: V2__seed_sample_data.sql
 * This ensures data is version-controlled and applied consistently
 * across all environments (local, staging, production).
 *
 * Flyway runs automatically on startup and tracks which migrations
 * have already been applied, so data is never duplicated.
 */
@Component
@Profile("!test")
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("✅ Application started. Sample data loaded via Flyway V2 migration.");
        log.info("📊 Connect to PostgreSQL to view data: jdbc:postgresql://localhost:5432/productdb");
        log.info("🌐 API running at: http://localhost:8080/api/v1/products");
        log.info("❤️  Health check: http://localhost:8080/api/v1/health");
    }
}
