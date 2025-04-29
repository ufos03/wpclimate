package com.wpclimate.git.credentials.ssh;

import com.wpclimate.configurator.exceptions.NoModelProvided;
import com.wpclimate.configurator.model.Model;

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
public class SshCredentialModel extends Model {

    private static final String PUBLIC_CERT_KEY = "PUBLIC_CERT_PATH";
    private static final String PRIVATE_CERT_KEY = "PRIVATE_CERT_PATH";
    private static final String REPO_NAME_KEY = "REPO_NAME";
    private static final String REPO_URL_KEY = "REPO_URL";

    private String publicCertPath;
    private String privateCertPath;
    private String repoName;
    private String repoUrl;

    /**
     * Default constructor.
     */
    public SshCredentialModel() {};

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

    /**
     * Returns a string representation of the {@code SshCredentialModel}.
     * 
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "SshCredentialModel [publicCertPath=" + publicCertPath + ", privateCertPath=" + privateCertPath
                + ", repoName=" + repoName + ", repoUrl=" + repoUrl + "]";
    }
}