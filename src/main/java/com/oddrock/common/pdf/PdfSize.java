package com.oddrock.common.pdf;

import java.util.HashMap;
import java.util.Map;

public class PdfSize {
	public static final double PIXEL_COUNT_PER_INCH = 72;
	private Map<Integer, PageSize> map = new HashMap<Integer, PageSize>();
	public Map<Integer, PageSize> getMap() {
		return map;
	}
	public void setMap(Map<Integer, PageSize> map) {
		this.map = map;
	}
	public void addPageSize(int pageNum, double width, double height){
		PageSize pageSize = (PageSize)map.get(Integer.valueOf(pageNum));
		if(pageSize==null){
			pageSize = new PageSize();
		}
		pageSize.setWidth(width);
		pageSize.setHeight(height);
		map.put(Integer.valueOf(pageNum), pageSize);
	}
	public PageSize getPageSize(int pageNum){
		return (PageSize)map.get(Integer.valueOf(pageNum));
	}
	public double getPageWidth(int pageNum){
		PageSize pageSize = (PageSize)map.get(Integer.valueOf(pageNum));
		if(pageSize==null){
			return -1;
		}else{
			return pageSize.getWidth();
		}
	}
	public double getPageHeight(int pageNum){
		PageSize pageSize = (PageSize)map.get(Integer.valueOf(pageNum));
		if(pageSize==null){
			return -1;
		}else{
			return pageSize.getHeight();
		}
	}
	public double getPageWidthInch(int pageNum){
		double value = getPageWidth(pageNum);
		if(value==-1){
			return -1;
		}else{
			return value/PIXEL_COUNT_PER_INCH;
		}
	}
	public double getPageHeightInch(int pageNum){
		double value = getPageHeight(pageNum);
		if(value==-1){
			return -1;
		}else{
			return value/PIXEL_COUNT_PER_INCH;
		}
	}
}
