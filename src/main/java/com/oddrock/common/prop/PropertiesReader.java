package com.oddrock.common.prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class PropertiesReader {
	private Properties properties = new Properties();
	private Set<String> filePathSet = new HashSet<String>(); 
	
	public PropertiesReader(){
		super();
	}
	
	public PropertiesReader(String filePath){
		super();
		this.addFilePath(filePath);
		this.loadProperties();
	}
	
	public PropertiesReader(String... filePaths){
		super();
		for(String filePath : filePaths){
			this.addFilePath(filePath);
		}
		this.loadProperties();
	}

	public void addFilePath(String filePath) {
		if(filePath!=null && !"".equals(filePath.trim())){
			filePathSet.add(filePath);
		}
	}

	/**
	 * 获取属性文件中的属性
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		if (properties.containsKey(key)) {
			String value = properties.getProperty(key).trim();
			try {
				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return value;
		} else {
			return "";
		}
	}

	public String getValue(String key, String defaultValue) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key, defaultValue);
		} else {
			return defaultValue;
		}
	}

	/* 加载所有属性文件 */
	public void loadProperties() {
		try {
			for(String filePath : filePathSet){
				InputStream inputString = guessPropFile(filePath);
				if(inputString==null){
					continue;
				}
				properties.load(inputString);
				inputString.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* 加载某个属性文件  */
	public void loadProperties(String filePath) {
		try {
			InputStream inputString = guessPropFile(filePath);
			properties.load(inputString);
			inputString.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private InputStream guessPropFile(String propFile) {
		Class cls = PropertiesReader.class;
		try {
			ClassLoader loader = cls.getClassLoader();
			InputStream in = loader.getResourceAsStream(propFile);
			if (in != null) {
				return in;
			}
			Package pack = cls.getPackage();
			if (pack != null) {
				String packName = pack.getName();
				String path = "";
				if (packName.indexOf(".") < 0) {
					path = packName + "/";
				} else {
					int start = 0, end = 0;
					end = packName.indexOf(".");
					while (end != -1) {
						path = path + packName.substring(start, end) + "/";
						start = end + 1;
						end = packName.indexOf(".", start);
					}
					path = path + packName.substring(start) + "/";
				}
				in = loader.getResourceAsStream(path + propFile);
				if (in != null) {
					return in;
				}
			}
			File file = null;
			String curDir = System.getProperty("user.dir");
			file = new File(curDir, propFile);
			if (file.exists()) {
				return new FileInputStream(file);
			}
			String classpath = System.getProperty("java.class.path");
			String[] cps = classpath
					.split(System.getProperty("path.separator"));
			for (int i = 0; i < cps.length; i++) {
				file = new File(cps[i], propFile);
				if (file.exists()) {
					break;
				}
				file = null;
			}

			if (file != null) {
				return new FileInputStream(file);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
