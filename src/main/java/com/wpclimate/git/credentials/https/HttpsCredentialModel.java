package com.wpclimate.git.credentials.https;

import com.wpclimate.configurator.model.Model;

/**
 * The {@code HttpsCredentialModel} class extends the {@link Model} class and represents
 * the credentials required for accessing a Git repository over HTTPS.
 * 
 * <p>
 * This class provides methods to set and retrieve the following properties:
 * </p>
 * <ul>
 *   <li>Username</li>
 *   <li>Password</li>
 *   <li>Repository Name</li>
 *   <li>Repository URL</li>
 * </ul>
 * 
 * <p>
 * The password is stored in an encrypted format, while other fields are stored as plain text.
 * This class also provides utility methods to convert a {@link Model} object into an
 * {@code HttpsCredentialModel} instance.
 * </p>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * HttpsCredentialModel model = new HttpsCredentialModel();
 * model.setUsername("myUsername");
 * model.setPsw("myPassword");
 * model.setRepoName("myRepo");
 * model.setRepoUrl("https://github.com/myRepo.git");
 * 
 * System.out.println(model.getUsername());
 * System.out.println(model.getRepoUrl());
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If multiple threads need to access the same instance,
 * external synchronization is required.
 * </p>
 * 
 * @see Model
 */
public class HttpsCredentialModel extends Model 
{

    private static final String USERNAME_KEY = "USERNAME";
    private static final String PSW_KEY = "PASSWORD";
    private static final String REPO_NAME_KEY = "REPO_NAME";
    private static final String REPO_URL_KEY = "REPO_URL";

    private String username;
    private String psw;
    private String repoName;
    private String repoUrl;

    /**
     * Constructs an empty {@code HttpsCredentialModel} instance.
     */
    public HttpsCredentialModel() 
    {
        super();
    }

    /**
     * Constructs an {@code HttpsCredentialModel} instance from an existing {@link Model}.
     * 
     * <p>
     * If the provided model contains keys for username, password, repository name, or
     * repository URL, their values are copied into the new instance.
     * </p>
     * 
     * @param model The {@link Model} object to convert.
     */
    public HttpsCredentialModel(Model model) 
    {
        super();
        if (model == null)
            return; //Lancia eccezione

        if (model.containsKey(USERNAME_KEY))
            this.setUsername(model.get(USERNAME_KEY));

        if (model.containsKey(PSW_KEY))
            this.setPsw(model.get(PSW_KEY));

        if (model.containsKey(REPO_NAME_KEY))
            this.setRepoName(model.get(REPO_NAME_KEY));

        if (model.containsKey(REPO_URL_KEY))
            this.setRepoUrl(model.get(REPO_URL_KEY));
    }

    /**
     * Converts a {@link Model} object into an {@code HttpsCredentialModel} instance.
     * 
     * @param model The {@link Model} object to convert.
     * @return A new {@code HttpsCredentialModel} instance populated with data from the model.
     */
    public static HttpsCredentialModel fromModel(Model model) 
    {
        if (model == null)
            return null; //Lancia eccezione

        HttpsCredentialModel newHttpsCredentialModel = new HttpsCredentialModel();

        if (model.containsKey(USERNAME_KEY))
            newHttpsCredentialModel.setUsername(model.get(USERNAME_KEY));

        if (model.containsKey(PSW_KEY))
            newHttpsCredentialModel.setPsw(model.get(PSW_KEY));

        if (model.containsKey(REPO_NAME_KEY))
            newHttpsCredentialModel.setRepoName(model.get(REPO_NAME_KEY));

        if (model.containsKey(REPO_URL_KEY))
            newHttpsCredentialModel.setRepoUrl(model.get(REPO_URL_KEY));

        return newHttpsCredentialModel;
    }

    /**
     * Sets the username for the HTTPS credentials.
     * 
     * @param username The username to set. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the username is null, empty, or blank.
     */
    public void setUsername(String username) 
    {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("The parameter username cannot be empty!");

        this.username = username;
        super.set(USERNAME_KEY, username, false);
    }

    /**
     * Sets the password for the HTTPS credentials.
     * 
     * <p>
     * The password is stored in an encrypted format.
     * </p>
     * 
     * @param password The password to set. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the password is null, empty, or blank.
     */
    public void setPsw(String password)
    {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("The parameter password cannot be empty!");

        this.psw = password;
        super.set(PSW_KEY, password, true); // Encrypted
    }

    /**
     * Sets the repository name for the HTTPS credentials.
     * 
     * @param repoName The repository name to set. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the repository name is null, empty, or blank.
     */
    public void setRepoName(String repoName) 
    {
        if (repoName == null || repoName.isBlank())
            throw new IllegalArgumentException("The parameter repoName cannot be empty!");

        this.repoName = repoName;
        super.set(REPO_NAME_KEY, repoName, false);
    }

    /**
     * Sets the repository URL for the HTTPS credentials.
     * 
     * @param repoUrl The repository URL to set. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the repository URL is null, empty, or blank.
     */
    public void setRepoUrl(String repoUrl) 
    {
        if (repoUrl == null || repoUrl.isBlank())
            throw new IllegalArgumentException("The parameter repoUrl cannot be empty!");

        this.repoUrl = repoUrl;
        super.set(REPO_URL_KEY, repoUrl, false);
    }

    /**
     * Returns the username for the HTTPS credentials.
     * 
     * @return The username.
     */
    public String getUsername() 
    {
        return this.username;
    }

    /**
     * Returns the password for the HTTPS credentials.
     * 
     * @return The password.
     */
    public String getPsw() 
    {
        return this.psw;
    }

    /**
     * Returns the repository name for the HTTPS credentials.
     * 
     * @return The repository name.
     */
    public String getRepoName() 
    {
        return this.repoName;
    }

    /**
     * Returns the repository URL for the HTTPS credentials.
     * 
     * @return The repository URL.
     */
    public String getRepoUrl() 
    {
        return this.repoUrl;
    }

    /**
     * Returns a string representation of the {@code HttpsCredentialModel}.
     * 
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "HttpsCredentialModel [username=" + username + ", psw=" + psw + ", repoName=" + repoName + ", repoUrl="
                + repoUrl + "]";
    }
}