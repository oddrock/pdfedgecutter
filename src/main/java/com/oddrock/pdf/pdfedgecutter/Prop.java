package com.oddrock.pdf.pdfedgecutter;

import com.oddrock.common.prop.PropertiesReader;

public class Prop {
	private static final PropertiesReader PR = new PropertiesReader();
	static{
		load();
	}
	
	private static void load(){
		PR.addFilePath("pdfedgecutter.properties");
		PR.loadProperties();
	}
	
	public static String get(String key){
		return PR.getValue(key);
	}
	
	public static String get(String key, String defaultValue) {
		return PR.getValue(key, defaultValue);
	}
}