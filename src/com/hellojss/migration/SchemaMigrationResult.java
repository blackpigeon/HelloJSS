package com.hellojss.migration;

public class SchemaMigrationResult {
    private final boolean migratedNow;

    public SchemaMigrationResult(boolean migratedNow) {
        this.migratedNow = migratedNow;
    }

    public boolean isMigratedNow() {
        return migratedNow;
    }
}