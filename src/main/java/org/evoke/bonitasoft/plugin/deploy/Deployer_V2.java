package org.evoke.bonitasoft.plugin.deploy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.bar.InvalidBusinessArchiveFormatException;
import org.bonitasoft.engine.bpm.process.ProcessActivationException;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessDeployException;
import org.bonitasoft.engine.bpm.process.ProcessEnablementException;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.bonitasoft.engine.util.APITypeManager;

import com.bonitasoft.engine.bpm.bar.BusinessArchiveFactory; 

public class Deployer_V2 {
	private static final String SERVER_URL = "http://localhost:8081/";
	private static final String APPLICATION_NAME = "bonita";
	private static final String USER_NAME = "WFRECO1";
	private static final String PASSWORD = "bpm";	

	static {
		// Define the REST parameters
		Map<String, String> map = new HashMap<String, String>();
		map.put("server.url", SERVER_URL);
		map.put("application.name", APPLICATION_NAME);
		APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, map);
	}

	public static void deploy(String sourceFolder, Boolean replace, Log log) {
		try {
			// get the LoginAPI using the TenantAPIAccessor
			final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();

			// log in to the tenant to create a session
			APISession session = loginAPI.login(USER_NAME, PASSWORD);
			log.info("Logged in at " + session.getCreationDate());

			ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);

			// Check whether process already exists. If exists, delete
			File folder = new File(sourceFolder);
			File[] files = folder.listFiles();
			for(File file: files){
				Long defId = processAPI.getProcessDefinitionId(getProcessName(file.getName()), getVersion(file.getName()));
				if (defId != null) {
					if (replace) {
						processAPI.disableAndDeleteProcessDefinition(defId);
						log.info("Old archive " + file.getName() + " deleted");
					} else {
						// Cancel deployment
						// TODO
						log.info("Not wanting to replace. Deployment will be cancelled.");
					}
				}
				
				// Read bar and deploy (If no same old archive exists or wanting to replace if exist)
				if (defId == null || (defId != null && replace)) {
					BusinessArchive archive = BusinessArchiveFactory.readBusinessArchive(file);
					log.info("Archive retrieved successfully.");

					processAPI.deployAndEnableProcess(archive);
					log.info("Archive deployed and enabled.");
				} else {
					log.info("Archive deployment cancelled");
				}
			}			

			// logout
			loginAPI.logout(session);
			log.info("Logged out.");
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BonitaHomeNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownAPITypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidBusinessArchiveFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SessionNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessDeployException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessEnablementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessDefinitionNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessActivationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeletionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doIt(){
		
	}
	
	private static String getProcessName(String fileName) {
		return fileName.substring(0,
				fileName.lastIndexOf("--"));
	}
	
	private static String getVersion(String fileName) {
		return fileName.substring(fileName.lastIndexOf("--") + 2,
				fileName.lastIndexOf(".bar"));
	}

	public static void main(String s[]){ }
	
}
