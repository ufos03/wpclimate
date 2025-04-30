package com.wpclimate.git.credentials.ssh;

import com.wpclimate.configurator.exceptions.NoModelProvided;
import com.wpclimate.configurator.model.Model;
import com.wpclimate.git.credentials.CredentialsType;

/**
 * The {@code SshCredentialModel} class extends the {@link Model} class and represents
 * the credentials required for accessing a Git repository over SSH.
 * 
 * <p>
 * This class provides methods to set and retrieve the following properties:
 * </p>
 * <ul>
 *   <li>Public Certificate Path</li>
 *   <li>Private Certificate Path</li>
 *   <li>Repository Name</li>
 *   <li>Repository URL</li>
 * </ul>
 * 
 * <p>
 * The certificate paths and repository details are stored as plain text. This class
 * also provides utility methods to convert a {@link Model} object into an
 * {@code SshCredentialModel} instance.
 * </p>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * SshCredentialModel model = new SshCredentialModel();
 * model.setPublicCertPath("/path/to/public/cert");
 * model.setPrivateCertPath("/path/to/private/cert");
 * model.setRepoName("myRepo");
 * model.setRepoUrl("git@github.com:myRepo.git");
 * 
 * System.out.println(model.getPathPublicCert());
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
public class SshCredentialModel extends Model 
{
    private static final int NUM_OF_KEYS = 5;

    private static final String PUBLIC_CERT_KEY = "PUBLIC_CERT_PATH";  // field_setted -> 0
    private static final String PRIVATE_CERT_KEY = "PRIVATE_CERT_PATH"; // field_setted -> 1
    private static final String REPO_NAME_KEY = "REPO_NAME"; // field_setted -> 2
    private static final String REPO_URL_KEY = "REPO_URL"; // field_setted -> 3
    private static final String CREDENTIAL_TYPE_KEY= "CREDENTIAL_TYPE"; // field_setted -> 4

    private String publicCertPath;
    private String privateCertPath;
    private String repoName;
    private String repoUrl;
    private String credentialType;

    private boolean[] field_setted = new boolean[NUM_OF_KEYS];

    /**
     * Default constructor.
     */
    public SshCredentialModel() 
    {
        super();
    };

    /**
     * Constructs an {@code SshCredentialModel} instance from a {@link Model}.
     * 
     * @param model The {@link Model} object containing SSH credential data.
     * @throws NoModelProvided If the provided model is {@code null}.
     */
    public SshCredentialModel(Model model) throws NoModelProvided {
        super();

        if (model == null)
            throw new NoModelProvided("SSH Model isn't valid");

        if (model.containsKey(PUBLIC_CERT_KEY))
            this.setPublicCertPath(model.get(PUBLIC_CERT_KEY));

        if (model.containsKey(PRIVATE_CERT_KEY))
            this.setPrivateCertPath(model.get(PRIVATE_CERT_KEY));

        if (model.containsKey(REPO_NAME_KEY))
            this.setRepoName(model.get(REPO_NAME_KEY));

        if (model.containsKey(REPO_URL_KEY))
            this.setRepoUrl(model.get(REPO_URL_KEY));
        
        this.setCredentialType();
    }

    /**
     * Creates an {@code SshCredentialModel} instance from a {@link Model}.
     * 
     * @param model The {@link Model} object containing SSH credential data.
     * @return A new {@code SshCredentialModel} instance.
     * @throws NoModelProvided If the provided model is {@code null}.
     */
    public static SshCredentialModel fromModel(Model model) throws NoModelProvided {
        if (model == null)
            throw new NoModelProvided("SSH Model isn't valid");

        return new SshCredentialModel(model);
    }

    /**
     * Sets the path to the public certificate.
     * 
     * @param pathPublicCert The path to the public certificate. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the path is null, empty, or blank.
     */
    public void setPublicCertPath(String pathPublicCert) {
        if (pathPublicCert == null || pathPublicCert.isBlank())
            throw new IllegalArgumentException("The public certificate path cannot be null, empty, or blank.");

        this.publicCertPath = pathPublicCert;
        System.out.println(this.publicCertPath);
        super.set(PUBLIC_CERT_KEY, this.publicCertPath, false);
        this.field_setted[0] = true;
    }

    /**
     * Sets the path to the private certificate.
     * 
     * @param pathPrivateCert The path to the private certificate. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the path is null, empty, or blank.
     */
    public void setPrivateCertPath(String pathPrivateCert) {
        if (pathPrivateCert == null || pathPrivateCert.isBlank())
            throw new IllegalArgumentException("The private certificate path cannot be null, empty, or blank.");

        this.privateCertPath = pathPrivateCert;
        super.set(PRIVATE_CERT_KEY, this.privateCertPath, false);
        this.field_setted[1] = true;
    }

    /**
     * Sets the repository name.
     * 
     * @param repoName The name of the repository. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the repository name is null, empty, or blank.
     */
    public void setRepoName(String repoName) {
        if (repoName == null || repoName.isBlank())
            throw new IllegalArgumentException("The repository name cannot be null, empty, or blank.");

        this.repoName = repoName;
        super.set(REPO_NAME_KEY, repoName, false);
        this.field_setted[2] = true;
    }

    /**
     * Sets the repository URL.
     * 
     * @param repoUrl The URL of the repository. Must not be null, empty, or blank.
     * @throws IllegalArgumentException If the repository URL is null, empty, or blank.
     */
    public void setRepoUrl(String repoUrl) {
        if (repoUrl == null || repoUrl.isBlank())
            throw new IllegalArgumentException("The repository URL cannot be null, empty, or blank.");

        this.repoUrl = repoUrl;
        super.set(REPO_URL_KEY, repoUrl, false);
        this.field_setted[3] = true;
    }

    public void setCredentialType()
    {
        this.credentialType = CredentialsType.SSH.getType();
        super.set(CREDENTIAL_TYPE_KEY, credentialType, false);
        this.field_setted[4] = true;
    }

    /**
     * Returns the path to the public certificate.
     * 
     * @return The path to the public certificate.
     */
    public String getPathPublicCert() {
        return this.publicCertPath;
    }

    /**
     * Returns the path to the private certificate.
     * 
     * @return The path to the private certificate.
     */
    public String getPathPrivateCert() {
        return this.privateCertPath;
    }

    /**
     * Returns the repository name.
     * 
     * @return The repository name.
     */
    public String getRepoName() {
        return this.repoName;
    }

    /**
     * Returns the repository URL.
     * 
     * @return The repository URL.
     */
    public String getRepoUrl() {
        return this.repoUrl;
    }

    public String getCredentialType()
    {
        return this.credentialType;
    }

    /**
     * Verifies if all required fields in the model have been properly set.
     * 
     * <p>
     * This method checks the status of all required fields in the model to determine if
     * the model is in a valid state for use in operations. The model is considered valid
     * only when all fields (publicCertPath, privateCertPath, repository name, repository URL, and credential type)
     * have been set.
     * </p>
     * 
     * <p>
     * <strong>Implementation Note:</strong> This method internally tracks which fields have been set
     * through the {@code field_setted} array that is updated whenever a setter method is called.
     * </p>
     * 
     * @return {@code true} if all required fields have been set; {@code false} otherwise.
     */
    public boolean isValid()
    {
        for (int i = 0; i < field_setted.length; i++) 
        {
            if (!field_setted[i])
                return false;  // Almeno un campo non è impostato
        }

        return true;  // Tutti i campi sono impostati
    }

    /**
     * Returns a string representation of the {@code SshCredentialModel}.
     * 
     * @return A string representation of the object.
     */
    @Override
    public String toString() {  // TODO Uccidere carrellino
        return "SshCredentialModel [Parlato e in disaccordo=" + publicCertPath + ", privateZampilliPower=" + privateCertPath
                + ", DavideName" + repoName + ", DragoMalfoy=" + repoUrl + "operativo]";
    }
}