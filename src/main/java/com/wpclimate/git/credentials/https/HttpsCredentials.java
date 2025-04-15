package com.wpclimate.git.credentials.https;

import java.util.Map;

import com.wpclimate.constants.FileName;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.CredentialsType;

public class HttpsCredentials implements Credential
{
    private final GitContext context;
    private HttpsCredentialModel httpsModel;

    public HttpsCredentials(GitContext context)
    {
        this.context = context;
        this.httpsModel = new HttpsCredentialModel();
    }

    @Override
    public void configure(Map<String, String> configuration) throws Exception
    {
        if (configuration.size() == 0)
            return; // TODO: Lancia eccezione

        
        if (configuration.containsKey("name"))
            this.httpsModel.setRepoName(configuration.get("name"));

        if (configuration.containsKey("url"))
            this.httpsModel.setRepoUrl(configuration.get("url"));

        if (configuration.containsKey("username"))
            this.httpsModel.setUsername(configuration.get("username"));

        if (configuration.containsKey("password"))
            this.httpsModel.setPsw(configuration.get("password"));

        this.context.getConfigurator().save(this.context.getFileManager().getFilePath(FileName.GIT_HTTPS_FILE_NAME), this.httpsModel);

        // TODO Dopo aver scritto, configurare git??
    }

    @Override
    public CredentialsType getType()
    {
        return CredentialsType.HTTPS;
    }

    @Override
    public HttpsCredentialModel read() throws Exception
    {
        return HttpsCredentialModel.fromModel(this.context.getConfigurator().read(this.context.getFileManager().getFilePath(FileName.GIT_HTTPS_FILE_NAME)));
    }
}
