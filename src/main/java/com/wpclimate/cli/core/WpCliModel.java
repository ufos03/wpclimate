package com.wpclimate.cli.core;

import com.wpclimate.configurator.model.Model;

/**
 * The {@code WpCliModel} class extends the {@code Model} class and provides
 * specific functionality for managing WP-CLI configurations.
 * It includes fields for the PHP executable path and the WP-CLI executable path.
 */
public class WpCliModel extends Model 
{
    private static final String PHP_KEY = "PHP"; // Key for the PHP executable path
    private static final String WPCLI_KEY = "WPCLI"; // Key for the WP-CLI executable path
    private static final String MYSQL_KEY = "MYSQL"; // Key for the MySQL executable

    private String php; // Path to the PHP executable
    private String wp; // Path to the WP-CLI executable
    private String mysql; // Path to the MySQL executable

    /**
     * Default constructor.
     * Initializes an empty {@code WpCliModel} instance.
     */
    public WpCliModel() 
    {
        super();
    }

    /**
     * Constructs a {@code WpCliModel} instance from an existing {@code Model}.
     * Extracts the PHP and WP-CLI paths from the tokens in the provided model.
     *
     * @param model The {@code Model} instance to extract data from.
     */
    public WpCliModel(Model model) 
    {
        super();

        if (model.containsKey(PHP_KEY))
            this.setPhp(model.get(PHP_KEY));
        
        if (model.containsKey(WPCLI_KEY))
            this.setWp(model.get(WPCLI_KEY));

        if (model.containsKey(MYSQL_KEY))
            this.setMYSQL(model.get(MYSQL_KEY));
    }

    /**
     * Creates a new {@code WpCliModel} instance from an existing {@code Model}.
     * Extracts the PHP and WP-CLI paths from the tokens in the provided model
     * and populates a new {@code WpCliModel} instance.
     *
     * @param model The {@code Model} instance to extract data from.
     * @return A new {@code WpCliModel} instance populated with data from the provided model.
     */
    public static WpCliModel fromModel(Model model)
    {
        WpCliModel newWpCliModel = new WpCliModel();

        if (model.containsKey(PHP_KEY))
            newWpCliModel.setPhp(model.get(PHP_KEY));
        
        if (model.containsKey(WPCLI_KEY))
            newWpCliModel.setWp(model.get(WPCLI_KEY));
        
        if (model.containsKey(MYSQL_KEY))
            newWpCliModel.setMYSQL(model.get(MYSQL_KEY));

        return newWpCliModel;
    }

    /**
     * Updates the current {@code WpCliModel} instance with data from the provided {@code Model}.
     * 
     * <p>
     * This method extracts the PHP and WP-CLI paths from the given {@code Model} instance
     * and updates the corresponding fields in the current {@code WpCliModel}.
     * </p>
     * 
     * @param model The {@code Model} instance containing the configuration data to extract.
     *              Must not be {@code null}.
     * 
     * @throws NullPointerException If the provided {@code model} is {@code null}.
     */
    public void setFromModel(Model model)
    {
        if (model.containsKey(PHP_KEY))
            this.setPhp(model.get(PHP_KEY));
        
        if (model.containsKey(WPCLI_KEY))
            this.setWp(model.get(WPCLI_KEY));

        if (model.containsKey(MYSQL_KEY))
            this.setMYSQL(model.get(MYSQL_KEY));
    }

    /**
     * Sets the path to the PHP executable.
     * Updates the underlying model with the new PHP path.
     *
     * @param php The PHP executable path to set.
     */
    public void setPhp(String php) 
    {
        this.php = php;
        super.set(PHP_KEY, this.php, false);
    }

    /**
     * Sets the path to the WP-CLI executable.
     * Updates the underlying model with the new WP-CLI path.
     *
     * @param wp The WP-CLI executable path to set.
     */
    public void setWp(String wp) 
    {
        this.wp = wp;
        super.set(WPCLI_KEY, this.wp, false);
    }

    /**
     * Sets the path to the MYSQL library.
     * Updates the underlying model with the new MYSQL path.
     * 
     * @param sqlDump The SQL-PATH executable path to set.
     */
    public void setMYSQL(String sqlDump)
    {
        this.mysql = sqlDump;
        super.set(MYSQL_KEY, this.mysql, false);
    }

    /**
     * Gets the path to the WP-CLI executable.
     *
     * @return The WP-CLI executable path.
     */
    public String getWp() 
    {
        return this.wp;
    }


    /**
     * Gets the path to the PHP executable.
     *
     * @return The PHP executable path.
     */
    public String getPhp() 
    {
        return this.php;
    }

    /**
     * Gets the path to the MYSQL library.
     *
     * @return The MYSQL executable path.
     */
    public String getMYSQL() 
    {
        return this.mysql;
    }
}