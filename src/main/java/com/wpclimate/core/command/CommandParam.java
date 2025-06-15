package com.wpclimate.core.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The {@code CommandParam} annotation is used to define metadata for parameters
 * in command classes within the WP-CLI application.
 *
 * <p>
 * This annotation can be applied to fields, method parameters, or methods to specify
 * details about a command parameter, such as its name, type, whether it is required,
 * its default value, and a description. It is primarily used for reflection-based
 * command registration and execution.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Defines the name of the parameter.</li>
 *   <li>Specifies the type of the parameter using {@link ParamType}.</li>
 *   <li>Indicates whether the parameter is required.</li>
 *   <li>Provides a default value for optional parameters.</li>
 *   <li>Includes a description for documentation purposes.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code CommandParam} annotation is typically used in command classes to annotate
 * fields or method parameters that represent command inputs. For example:
 * </p>
 * <pre>
 * {@literal @}CommandParam(name = "oldValue", type = ParamType.STRING, required = true, description = "The value to search for in the database")
 * private String oldValue;
 * 
 * {@literal @}CommandParam(name = "newValue", type = ParamType.STRING, required = true, description = "The value to replace the search value with")
 * private String newValue;
 * </pre>
 *
 * <h2>Reflection:</h2>
 * <p>
 * This annotation is processed at runtime using reflection to dynamically register
 * and execute commands. It allows the system to extract parameter metadata and
 * validate inputs during command execution.
 * </p>
 *
 * <h2>Retention Policy:</h2>
 * <p>
 * The {@code CommandParam} annotation has a {@link RetentionPolicy#RUNTIME} retention policy,
 * meaning it is available at runtime for reflection-based processing.
 * </p>
 *
 * <h2>Target:</h2>
 * <p>
 * The {@code CommandParam} annotation can be applied to:
 * <ul>
 *   <li>{@link ElementType#FIELD} - Fields in command classes.</li>
 *   <li>{@link ElementType#PARAMETER} - Method parameters.</li>
 *   <li>{@link ElementType#METHOD} - Methods that define command parameters.</li>
 * </ul>
 * </p>
 *
 * @see ParamType
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface CommandParam {

    /**
     * Specifies the name of the parameter.
     *
     * <p>
     * The name is used to identify the parameter during command execution and
     * validation. It must be unique within the scope of a command.
     * </p>
     *
     * @return The name of the parameter.
     */
    String name();

    /**
     * Specifies the type of the parameter.
     *
     * <p>
     * The type is defined using the {@link ParamType} enumeration, which includes
     * common types such as {@code STRING}, {@code BOOLEAN}, {@code INTEGER}, and {@code FLOAT}.
     * The default type is {@code STRING}.
     * </p>
     *
     * @return The type of the parameter.
     */
    ParamType type() default ParamType.STRING;

    /**
     * Indicates whether the parameter is required.
     *
     * <p>
     * If {@code true}, the parameter must be provided during command execution.
     * If {@code false}, the parameter is optional and may have a default value.
     * The default value is {@code false}.
     * </p>
     *
     * @return {@code true} if the parameter is required, {@code false} otherwise.
     */
    boolean required() default false;

    /**
     * Specifies the default value for the parameter.
     *
     * <p>
     * The default value is used if the parameter is optional and not provided
     * during command execution. The default value is an empty string.
     * </p>
     *
     * @return The default value of the parameter.
     */
    String defaultValue() default "";

    /**
     * Provides a description of the parameter.
     *
     * <p>
     * The description is used for documentation purposes and helps users understand
     * the purpose of the parameter. The default value is an empty string.
     * </p>
     *
     * @return The description of the parameter.
     */
    String description() default "";
}