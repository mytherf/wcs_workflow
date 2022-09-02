package com.myf.wcs.database.migration.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;


/**
 * @author H443745
 */
@Slf4j
@Component
public class CallbackFlywayMigrationStrategy implements FlywayMigrationStrategy {

    @Override
    public void migrate(Flyway flyway) {
        log.info("Before flyway migration...");

        try {
            flyway.migrate();
        } catch (FlywayException e) {
            flyway.repair();
            log.error("Flyway ERROR:", e);
        }

        doAfterMigration();
    }

    private void doAfterMigration() {
        log.info("******************************************************");
        log.info("*..............After flyway migration................*");
        log.info("******************************************************");
    }
}
