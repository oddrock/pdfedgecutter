package com.oddrock.common.windows;

import java.io.IOException;

public class CmdExecutor {
	private static final CmdExecutor instance = new CmdExecutor();
	private CmdExecutor(){}
	public static CmdExecutor getSingleInstance(){
		return instance;
	}
	
	public CmdResult exeCmd(String cmd){
		CmdResult cmdResult = new CmdResult();
		Runtime rt = Runtime.getRuntime();
		Process p;
		try {
			p = rt.exec(cmd);
			cmdResult.setSuccess(true);
			cmdResult.setMessage(p.toString());
		} catch (IOException e) {
			e.printStackTrace();
			cmdResult.setSuccess(false);
			cmdResult.setMessage(e.getMessage());
		}
		return cmdResult;
	}
	

	public static void main(String[] args) {
		String cmd = "\"C:\\Program Files (x86)\\Foxit Software\\Foxit Phantom\\Foxit Phantom.exe\" C:\\flume.pdf";
		CmdExecutor.getSingleInstance().exeCmd(cmd);
		cmd = "taskkill /f /im \"Foxit Phantom.exe\"";
		CmdExecutor.getSingleInstance().exeCmd(cmd);
	}

}
