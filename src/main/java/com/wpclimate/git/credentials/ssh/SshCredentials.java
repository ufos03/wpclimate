package com.wpclimate.git.credentials.ssh;

import java.util.Map;

import com.wpclimate.constants.FileName;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.CredentialsType;
import com.wpclimate.git.credentials.https.HttpsCredentialModel;

public class SshCredentials implements Credential
{
    private final GitContext context;
    private SshCredentialModel sshModel;

    public SshCredentials(GitContext context)
    {
        this.context = context;
        this.sshModel = new SshCredentialModel();
    }

    @Override
    public void configure(Map<String, String> configuration) throws Exception
    {
        if (configuration.size() == 0)
            return; // TODO: Lancia eccezione

        
        if (configuration.containsKey("name"))
            this.sshModel.setRepoName(configuration.get("name"));

        if (configuration.containsKey("url"))
            this.sshModel.setRepoUrl(configuration.get("url"));

        if (configuration.containsKey("pubkey"))
            this.sshModel.setPublicCertPath(configuration.get("pubPath"));

        if (configuration.containsKey("privkey"))
            this.sshModel.setPrivateCertPath(configuration.get("privPath"));

        this.context.getConfigurator().save(this.context.getFileManager().getFilePath(FileName.GIT_SSH_FILE_NAME), this.sshModel);
    }

    @Override
    public CredentialsType getType()
    {
        return CredentialsType.SSH;
    }

    @Override
    public HttpsCredentialModel read() throws Exception
    {
        return HttpsCredentialModel.fromModel(this.context.getConfigurator().read(this.context.getFileManager().getFilePath(FileName.GIT_HTTPS_FILE_NAME)));
    }

}
