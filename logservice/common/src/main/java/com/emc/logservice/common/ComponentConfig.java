package com.emc.logservice.common;

import java.util.Properties;

/**
 * Base Configuration for any Component in the Log Service. Allows sharing a single .properties file (loaded up as a
 * java.util.Properties object), and section it based on a component code. Each property in the file will be prefixed by
 * a component code which identifies which component it belongs to.
 * <p/>
 * Example:
 * <ul>
 * <li>foo.key1=value1
 * <li>foo.key2=value2
 * <li>bar.key1=value3
 * <li>bar.key3=value4
 * </ul>
 * Indicate that component "foo" has Key-Values (key1=value1, key2=value2), and component "bar" has (key1=value3, key3=value4).
 */
public abstract class ComponentConfig {
    // region Members

    private static final String Separator = ".";
    private final String keyPrefix;
    private final Properties properties;

    //endregion

    //region Constructor

    /**
     * Creates a new instance of the ComponentConfig class.
     *
     * @param properties    The java.util.Properties object to read Properties from.
     * @param componentCode The configuration code for the component.
     * @throws MissingPropertyException Whenever a required Property is missing from the given properties collection.
     * @throws NumberFormatException    Whenever a Property has a value that is invalid for it.
     * @throws NullPointerException     If any of the arguments are null.
     * @throws IllegalArgumentException If componentCode is an empty string..
     */
    public ComponentConfig(Properties properties, String componentCode) throws MissingPropertyException, InvalidPropertyValueException {
        Exceptions.throwIfNull(properties, "properties");
        Exceptions.throwIfNullOfEmpty(componentCode, "componentCode");

        this.properties = properties;
        this.keyPrefix = componentCode + Separator;

        refresh();
    }

    //endregion

    //region Property Retrieval

    /**
     * Gets the value of a String property.
     *
     * @param name The name of the property (no component code prefix).
     * @return The property value or default value, if no such is defined in the base Properties.
     * @throws MissingPropertyException When the given property name does not exist within the current component.
     */
    protected String getProperty(String name) throws MissingPropertyException {
        String fullKeyName = getPropertyKey(name);
        String value = this.properties.getProperty(fullKeyName, null);
        if (value == null) {
            throw new MissingPropertyException(fullKeyName);
        }

        return value;
    }

    /**
     * Gets the value of a String property.
     *
     * @param name         The name of the property (no component code prefix).
     * @param defaultValue The default value for the property.
     * @return The property value or default value, if no such is defined in the base Properties.
     */
    protected String getProperty(String name, String defaultValue) {
        String fullKeyName = getPropertyKey(name);
        return this.properties.getProperty(fullKeyName, defaultValue);
    }

    /**
     * Gets the value of an Int32 property.
     *
     * @param name The name of the property (no component code prefix).
     * @return The property value or default value, if no such is defined in the base Properties.
     * @throws MissingPropertyException      When the given property name does not exist within the current component.
     * @throws InvalidPropertyValueException When the property cannot be parsed as an Int32.
     */
    protected int getInt32Property(String name) throws MissingPropertyException, InvalidPropertyValueException {
        String value = getProperty(name).trim();

        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {
            throw new InvalidPropertyValueException(getPropertyKey(name), value, ex);
        }
    }

    /**
     * Gets the value of an Int32 property.
     *
     * @param name         The name of the property (no component code prefix).
     * @param defaultValue The default value for the property.
     * @return The property value or default value, if no such is defined in the base Properties.
     * @throws InvalidPropertyValueException When the property cannot be parsed as an Int32.
     */
    protected int getInt32Property(String name, int defaultValue) throws InvalidPropertyValueException {
        String value = getProperty(name, null);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException ex) {
            throw new InvalidPropertyValueException(getPropertyKey(name), value, ex);
        }
    }

    /**
     * Gets the value of an Int64 property.
     *
     * @param name The name of the property (no component code prefix).
     * @return The property value or default value, if no such is defined in the base Properties.
     * @throws MissingPropertyException      When the given property name does not exist within the current component.
     * @throws InvalidPropertyValueException When the property cannot be parsed as an Int64.
     */
    protected long getInt64Property(String name) throws MissingPropertyException, InvalidPropertyValueException {
        String value = getProperty(name).trim();

        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException ex) {
            throw new InvalidPropertyValueException(getPropertyKey(name), value, ex);
        }
    }

    /**
     * Gets the value of an Int64 property.
     *
     * @param name         The name of the property (no component code prefix).
     * @param defaultValue The default value for the property.
     * @return The property value or default value, if no such is defined in the base Properties.
     * @throws InvalidPropertyValueException When the property cannot be parsed as an Int64.
     */
    protected long getInt64Property(String name, long defaultValue) throws InvalidPropertyValueException {
        String value = getProperty(name, null);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value.trim());
        }
        catch (NumberFormatException ex) {
            throw new InvalidPropertyValueException(getPropertyKey(name), value, ex);
        }
    }

    /**
     * Gets the value of a boolean property.
     * Notes:
     * <ul>
     * <li> "true", "yes" and "1" (case insensitive) map to boolean "true".
     * <li> "false", "no" and "0" (case insensitive) map to boolean "false".
     * </ul>
     *
     * @param name The name of the property (no component code prefix).
     * @return The property value or default value, if no such is defined in the base Properties.
     * @throws MissingPropertyException      When the given property name does not exist within the current component.
     * @throws InvalidPropertyValueException When the property cannot be parsed as a Boolean.
     */
    protected boolean getBooleanProperty(String name) throws MissingPropertyException, InvalidPropertyValueException {
        String value = getProperty(name).trim();

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1")) {
            return true;
        }
        else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("0")) {
            return false;
        }
        else {
            throw new InvalidPropertyValueException(getPropertyKey(name), value);
        }
    }

    /**
     * Gets the value of a boolean property.
     * Notes:
     * <ul>
     * <li> "true", "yes" and "1" (case insensitive) map to boolean "true".
     * <li> "false", "no" and "0" (case insensitive) map to boolean "false".
     * </ul>
     *
     * @param name         The name of the property (no component code prefix).
     * @param defaultValue The default value for the property.
     * @return The property value or default value, if no such is defined in the base Properties.
     * @throws InvalidPropertyValueException When the property cannot be parsed as a Boolean.
     */
    protected boolean getBooleanProperty(String name, boolean defaultValue) throws InvalidPropertyValueException {
        String value = getProperty(name, null);
        if (value == null) {
            return defaultValue;
        }

        value = value.trim();
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1")) {
            return true;
        }
        else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("0")) {
            return false;
        }
        else {
            throw new InvalidPropertyValueException(getPropertyKey(name), value);
        }
    }

    private String getPropertyKey(String name) {
        Exceptions.throwIfNullOfEmpty(name, "name");
        return this.keyPrefix + name;
    }

    //endregion

    //region Configuration Refresh

    /**
     * Refreshes the configuration based on the latest Property values.
     */
    protected abstract void refresh() throws MissingPropertyException, InvalidPropertyValueException;

    //endregion
}
