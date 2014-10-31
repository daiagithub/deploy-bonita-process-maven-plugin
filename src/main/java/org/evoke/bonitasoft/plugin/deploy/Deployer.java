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

public class Deployer {
	private static final String SERVER_URL = "http://localhost:8081/";
	private static final String APPLICATION_NAME = "bonita";
	private static final String USER_NAME = "WFRECO1";
	private static final String PASSWORD = "bpm";
	private static final String FILE_NAME = "C:\\Users\\dyumkhai\\.jenkins\\jobs\\BonitaBPM-BuildProcesses\\workspace\\process-bars\\CITest--1.0.bar";
	
	private static final String PROCESS_NAME = "CITest";
	private static final String VERSION = setVersion();
	
	static {
		// Define the REST parameters
		Map<String, String> map = new HashMap<String, String>();
		map.put("server.url", SERVER_URL);
		map.put("application.name", APPLICATION_NAME);
		APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, map);		
	}
	
	public static void deploy(Boolean replace, Log log){
		try {
			// get the LoginAPI using the TenantAPIAccessor
			final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();

			// log in to the tenant to create a session		
			APISession session = loginAPI.login(USER_NAME, PASSWORD);
			log.info("Logged in at " + session.getCreationDate());
			
			ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);
			
			//Check whether process already exists
			Long defId = processAPI.getProcessDefinitionId(PROCESS_NAME, VERSION);
			
			//Delete the existing process if exists
			if(defId != null){
				if(replace){
					processAPI.disableAndDeleteProcessDefinition(defId);
				}else{
					//Cancel deployment
					//TODO
				}
			}
			
			//Read bar and deploy
			if (defId == null || (defId != null && replace)){
				BusinessArchive archive = BusinessArchiveFactory.readBusinessArchive(new File(FILE_NAME));
				log.info("Archive initialized");				
				
				processAPI.deployAndEnableProcess(archive);
				log.info("Archive deployed and enabled.");
			}else{
				log.info("Archive deployment cancelled");
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
		}catch (ProcessEnablementException e) {
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
	
	private static String setVersion(){
		return FILE_NAME.substring(FILE_NAME.lastIndexOf("--") + 2, FILE_NAME.lastIndexOf(".bar"));
	}
	
	/*public static void main(String s[]){
		System.out.println(setVersion());
	}*/
}
