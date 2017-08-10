package com.oddrock.pdf.pdfedgecutter;

import com.oddrock.common.prop.PropertiesReader;

public class PropertiesManager {
	private static final PropertiesReader PR = new PropertiesReader();
	static{
		load();
	}
	
	private static void load(){
		PR.addFilePath("pdfedgecutter-secret.properties");
		PR.addFilePath("pdfedgecutter.properties");
		PR.loadProperties();
	}
	
	public static String getValue(String key){
		return PR.getValue(key);
	}
	
	public static String getValue(String key, String defaultValue) {
		return PR.getValue(key, defaultValue);
	}
}