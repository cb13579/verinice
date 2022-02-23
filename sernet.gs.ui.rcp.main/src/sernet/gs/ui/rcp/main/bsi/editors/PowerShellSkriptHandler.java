package sernet.gs.ui.rcp.main.bsi.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class PowerShellSkriptHandler {
	
	//private static final Logger LOG = Logger.getLogger(M365StatusEditor.class);
	
	/**
	 * Executes a Powershell command.
	 * 
	 * @param command the command
	 * @return the result as String.
	 * @throws Exception if an error occurs 
	 */
	public static String executePSCommand(String command) throws Exception {
		String cmd = "cmd /c powershell -ExecutionPolicy RemoteSigned -noprofile -noninteractive " + command;
		return exec(cmd);
	}
	
	/**
	 * Executes a Powershell script.
	 * 
	 * @param scriptFilename the filename of the script
	 * @param args any arguments to pass to the script
	 * @return the result as String.
	 * @throws Exception if an error occurs 
	 */
	public static String executePSScript(String scriptFilename, String args) throws Exception {
		if (!new File(scriptFilename).exists())
			throw new Exception("Script file doesn't exist: " + scriptFilename);
		
		String cmd = "cmd /c powershell -ExecutionPolicy RemoteSigned -noprofile -noninteractive -file \"" + scriptFilename + "\"";
		if (args != null && args.length() > 0)
			cmd += " " + args;
		return exec(cmd);
	}
	
	/**
	 * Executes a batch file.
	 * If you want to call Powershell from the batch file you need to do it using this syntax:
	 * powershell -ExecutionPolicy RemoteSigned -NoProfile -NonInteractive -File c:/temp/script.ps1
	 * 
	 * @param batchFilename the filename of the batch file
	 * @param params any parameters to pass to the batch file
	 * @return the result as String.
	 * @throws Exception if an error occurs 
	 */
	public static String executeBatchFile(String batchFilename, String params) throws Exception {
		if (!new File(batchFilename).exists())
			throw new Exception("Batch file doesn't exist: " + batchFilename);
		
		String cmd = "cmd /c \"" + batchFilename + "\"";
		if (params != null && params.length() > 0)
			cmd += " " + params;
		return exec(cmd);
	}
	
	private static String exec(String command) throws Exception {
        	StringBuffer sbInput = new StringBuffer();
        	StringBuffer sbError = new StringBuffer();
        
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command);
		proc.getOutputStream().close();
        	InputStream inputstream = proc.getInputStream();        
        	InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        	BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        
        	String line;
        	while ((line = bufferedreader.readLine()) != null) {
        		sbInput.append(line + "\n");
        	}
        
        	inputstream = proc.getErrorStream();
        	inputstreamreader = new InputStreamReader(inputstream);
        	bufferedreader = new BufferedReader(inputstreamreader);
        	while ((line = bufferedreader.readLine()) != null) {
            		sbError.append(line + "\n");
        	}
        
        	if (sbError.length() > 0)
        		throw new Exception("The command [" + command + "] failed to execute!\n\nResult returned:\n" + sbError.toString());

       		return sbInput.toString();
	}
}
