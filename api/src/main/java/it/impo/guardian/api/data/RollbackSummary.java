package it.impo.guardian.api.data;

public record RollbackSummary(int total, int applied, int skipped, int partial) {
}
