package com.oddrock.common.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

public class FileUtils {
	/**
	 * 从文件路径获取文件名
	 * @param filePath
	 * @return
	 */
	public static String getFileNameFromFilePath(String filePath){
		File file = new File(filePath); 
		return file.getName();
	}
	
	/**
	 * 从文件路径获取不带后缀的文件名
	 * @param filePath
	 * @return
	 */
	public static String getFileNameWithoutSuffixFromFilePath(String filePath){
		String fileName = getFileNameFromFilePath(filePath);
		if(fileName.lastIndexOf(".")>0){
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		return fileName;
	}
	
	/**
	 * 从文件路径获取所在目录路径
	 * @param filePath
	 * @return
	 */
	public static String getDirPathFromFilePath(String filePath){
		File file = new File(filePath); 	
		return file.getParent();
	}
	
	/**
	 * 获取文件后缀名
	 * @param fileName
	 * @return
	 */
	public static String getFileNameSuffix(String fileName){
		if(fileName!=null){
			return fileName.substring(fileName.lastIndexOf(".")+1);
		}else{
			return "";
		}
	}
	
	/**
	 * 将文件作为一个字符串整体读取
	 * @param fileName
	 * @return
	 */
	public static String readFileContentToStr(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	sb.append(tempString+"\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString();
    }
	
	/**
	 * 更改文件名称
	 * @param srcFilePath	文件路径
	 * @param newName		新的文件名，不包括后缀和前面的目录路径
	 * @return
	 */
	public static String renameFile(String srcFilePath, String newName){
		String dirPath = getDirPathFromFilePath(srcFilePath);
		String suffix = getFileNameSuffix(srcFilePath);
		if(suffix.length()>0){
			newName = newName + "." + suffix;
		}
		String separator = java.io.File.separator;
		return dirPath+separator+newName;
	}
	
	/**
	 * 在文件名后面追加字符形成新文件名
	 * @param srcFilePath
	 * @param addStr
	 * @return
	 */
	public static String renameFileByAdd(String srcFilePath, String addStr){
		String fileName = getFileNameWithoutSuffixFromFilePath(srcFilePath);
		return renameFile(srcFilePath, fileName+addStr);
	}
	
	/**
	 * 在文件名后面追加字符形成新文件名，且目录路径也发生变化
	 * @param srcFilePath
	 * @param destDirPath
	 * @param addStr
	 * @return
	 */
	public static String renameFileByAdd(String srcFilePath, String destDirPath, String addStr){
		String fileName = getFileNameWithoutSuffixFromFilePath(srcFilePath);
		String suffix = getFileNameSuffix(srcFilePath);
		fileName = fileName + addStr;
		if(suffix.length()>0){
			fileName = fileName + "." + suffix;
		}
		return destDirPath+java.io.File.separator+fileName;
	}
	
	public static void mkdirIfNotExists(String dirPath){
		File dirname = new File(dirPath);
		if (!dirname.isDirectory()){ //目录不存在
		     dirname.mkdir(); //创建目录
		}  
	}
	
	public static void writeToFile(String filePath, String conent, boolean append) {     
        BufferedWriter out = null;     
        try {     
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, append)));     
            out.write(conent);     
        } catch (Exception e) {     
            e.printStackTrace();     
        } finally {     
            try {     
                if(out != null){  
                    out.close();     
                }  
            } catch (IOException e) {     
                e.printStackTrace();     
            }     
        }     
    }  
	
	public static void writeLineToFile(String filePath, String conent, boolean append) {     
		writeToFile(filePath, conent+"\n", append);
	}
	
	
	public static void main(String[] args){
		/*String filePath = "C:\\Users\\oddro\\Desktop\\Hadoop权威指南第三版(英文).pdf";
		System.out.println(getFileNameWithoutSuffixFromFilePath(filePath));
		System.out.println(getFileNameSuffix(filePath));
		System.out.println(getFileNameFromFilePath(filePath));
		System.out.println(getDirPathFromFilePath(filePath));
		System.out.println(renameFileByAdd(filePath, "323"));
		System.out.println(renameFileByAdd(filePath, "C:\\","323"));*/
		writeToFile("C:\\Users\\oddro\\Desktop\\test.log", "123\n", false);
		writeToFile("C:\\Users\\oddro\\Desktop\\test.log", "456\n", false);
		writeToFile("C:\\Users\\oddro\\Desktop\\test.log", new Date()+"\n", true);
	}
}
