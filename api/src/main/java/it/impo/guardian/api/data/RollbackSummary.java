package it.impo.guardian.api.data;

/**
 * Summarizes the outcome of a rollback operation.
 *
 * @param total    the number of log entries matched by the rollback
 * @param applied  how many of them were actually reverted
 * @param skipped  how many could not be reverted (e.g. missing world or block)
 * @param partial  how many were only partially reverted (used for container rollbacks)
 */
public record RollbackSummary(int total, int applied, int skipped, int partial) {
}