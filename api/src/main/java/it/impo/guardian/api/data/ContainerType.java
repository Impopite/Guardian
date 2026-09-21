package it.impo.guardian.api.data;

/**
 * Identifies the type of container involved in a container log.
 *
 * <p>Used to store and later resolve the inventory to operate on during a container
 * rollback.</p>
 */
public enum ContainerType {
    /** A regular chest. */
    CHEST,
    /** A trapped chest. */
    TRAPPED_CHEST,
    /** A barrel. */
    BARREL,
    /** A hopper. */
    HOPPER,
    /** A dispenser. */
    DISPENSER,
    /** A dropper. */
    DROPPER,
    /** Any other block acting as a container that is not explicitly mapped. */
    UNKNOWN
}