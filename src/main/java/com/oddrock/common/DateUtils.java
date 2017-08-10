package com.oddrock.common;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	private static final SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss"); 
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	public static String getFormatTime1(Date date){
		return format1.format(date);
	}
	
	public static String getFormatTime1(){
		return format1.format(new Date());
	}
	
	public static String getFormatTime(Date date){
		return format.format(date);
	}
	
	public static String getFormatTime(){
		return format.format(new Date());
	}

	public static void main(String[] args) {
		System.out.println(getFormatTime1());
	}

}