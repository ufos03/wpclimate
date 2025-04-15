package com.wpclimate.git.credentials.ssh;

import com.wpclimate.configurator.model.Model;

public class SshCredentialModel extends Model
{
    private static final String PUBLIC_CERT_KEY = "PUBLIC_CERT_PATH";
    private static final String PRIVATE_CERT_KEY = "PRIVATE_CERT_PATH";
    private static final String REPO_NAME_KEY = "REPO_NAME";
    private static final String REPO_URL_KEY = "REPO_URL";

    private String publicCertPath;
    private String privateCertPath;
    private String repoName;
    private String repoUrl;

    public void setPublicCertPath(String pathPublicCert)
    {
        this.publicCertPath = pathPublicCert;
        super.set(PUBLIC_CERT_KEY, this.publicCertPath, false);
    }

    public void setPrivateCertPath(String pathPrivateCert)
    {
        this.privateCertPath = pathPrivateCert;
        super.set(PRIVATE_CERT_KEY, this.privateCertPath, false);
    }

    public void setRepoName(String repoName)
    {
        this.repoName = repoName;
        super.set(REPO_NAME_KEY, repoName, false);
    }

    public void setRepoUrl(String repoUrl)
    {
        this.repoUrl = repoUrl;
        super.set(REPO_URL_KEY, repoUrl, false);
    }

    public String getPathPublicCert() 
    {
        return this.publicCertPath;
    }

    public String getPathPrivateCert() 
    {
        return this.privateCertPath;
    }

    public String getRepoName() 
    {
        return this.repoName;
    }

    public String getRepoUrl() 
    {
        return this.repoUrl;
    }
}
