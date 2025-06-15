package com.wpclimate.core.command;

/**
 * The {@code ParamType} enumeration defines the supported types for command parameters
 * in the WP-CLI application.
 *
 * <p>
 * This enumeration is used in conjunction with the {@link CommandParam} annotation to specify
 * the type of a parameter. It ensures type safety and provides a standardized way to define
 * parameter types across commands.
 * </p>
 *
 * <h2>Supported Types:</h2>
 * <ul>
 *   <li>{@link #STRING} - Represents a textual parameter.</li>
 *   <li>{@link #BOOLEAN} - Represents a true/false parameter.</li>
 *   <li>{@link #INTEGER} - Represents a numeric parameter (whole numbers).</li>
 *   <li>{@link #FLOAT} - Represents a numeric parameter (decimal numbers).</li>
 *   <li>{@link #PATH} - Represents a file or directory path parameter.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code ParamType} enumeration is typically used in the {@link CommandParam} annotation
 * to define the type of a parameter. For example:
 * </p>
 * <pre>
 * {@literal @}CommandParam(name = "filePath", type = ParamType.PATH, required = true, description = "The path to the file")
 * private String filePath;
 * 
 * {@literal @}CommandParam(name = "isDryRun", type = ParamType.BOOLEAN, required = false, defaultValue = "false", description = "Whether to execute in dry-run mode")
 * private boolean isDryRun;
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code ParamType} enumeration is thread-safe as it is immutable and does not contain
 * any mutable state.
 * </p>
 *
 * @see CommandParam
 */
public enum ParamType {
    /**
     * Represents a textual parameter.
     *
     * <p>
     * This type is used for parameters that accept string values, such as names,
     * URLs, or general text inputs.
     * </p>
     */
    STRING,

    /**
     * Represents a true/false parameter.
     *
     * <p>
     * This type is used for parameters that accept boolean values, such as flags
     * or options that toggle functionality.
     * </p>
     */
    BOOLEAN,

    /**
     * Represents a numeric parameter (whole numbers).
     *
     * <p>
     * This type is used for parameters that accept integer values, such as counts,
     * IDs, or other whole number inputs.
     * </p>
     */
    INTEGER,

    /**
     * Represents a numeric parameter (decimal numbers).
     *
     * <p>
     * This type is used for parameters that accept floating-point values, such as
     * percentages, measurements, or other decimal inputs.
     * </p>
     */
    FLOAT,

    /**
     * Represents a file or directory path parameter.
     *
     * <p>
     * This type is used for parameters that accept paths to files or directories,
     * such as configuration files, logs, or working directories.
     * </p>
     */
    PATH
}
