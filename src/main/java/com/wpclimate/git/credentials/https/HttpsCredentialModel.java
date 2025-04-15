package com.wpclimate.git.credentials.https;

import com.wpclimate.configurator.model.Model;

// TODO: Aggiungere controlli agli input !!

public class HttpsCredentialModel extends Model
{
    private static final String USERNAME_KEY = "USERNAME";
    private static final String PSW_KEY = "PSW";
    private static final String REPO_NAME_KEY = "REPO_NAME";
    private static final String REPO_URL_KEY = "REPO_URL";

    private String username;
    private String psw;
    private String repoName;
    private String repoUrl;

    public HttpsCredentialModel()
    {
        super();
    }

    public HttpsCredentialModel(Model model)
    {
        super();

        if (model.containsKey(USERNAME_KEY))
            this.setUsername(model.get(USERNAME_KEY));

        if (model.containsKey(PSW_KEY))
            this.setPsw(model.get(PSW_KEY));

        if (model.containsKey(REPO_NAME_KEY))
            this.setRepoName(model.get(REPO_NAME_KEY));

        if (model.containsKey(REPO_URL_KEY))
            this.setRepoUrl(model.get(REPO_URL_KEY));
    }

    public static HttpsCredentialModel fromModel(Model model)
    {
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

    public void setUsername(String username)
    {
        this.username = username;
        super.set(USERNAME_KEY, username, false);
    }

    public void setPsw(String password)
    {
        this.psw = password;
        super.set(PSW_KEY, password, true);  // Encrypted !!
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

    public String getUsername() 
    {
        return this.username;
    }

    public String getPsw() 
    {
        return this.psw;
    }

    public String getRepoName() 
    {
        return this.repoName;
    }

    public String getRepoUrl() 
    {
        return this.repoUrl;
    }

    @Override
    public String toString() {
        return "HttpsCredentialModel [username=" + username + ", psw=" + psw + ", repoName=" + repoName + ", repoUrl="
                + repoUrl + "]";
    }
}