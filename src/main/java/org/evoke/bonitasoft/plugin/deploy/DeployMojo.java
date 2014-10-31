package org.evoke.bonitasoft.plugin.deploy;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo (name="deploy")
public class DeployMojo extends AbstractMojo{	
	 
	@Parameter( property = "deploy.source-folder", required=true  )
    private String sourceFolder;
    
	@Parameter( property = "deploy.replace", defaultValue = "false"  )
    private Boolean replace;
       
    public void execute() throws MojoExecutionException, MojoFailureException{		
		Deployer_V2.deploy(sourceFolder, replace, getLog());   
	}
}


//org.evoke.bonitasoft:deploy-bonitasoft-maven-plugin:deploy -Ddeploy.source-folder=C:\Users\dyumkhai\.jenkins\jobs\BonitaBPM-BuildProcesses\workspace\process-bars -Ddeploy.replace=true 