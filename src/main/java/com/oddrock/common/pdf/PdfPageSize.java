package com.oddrock.common.pdf;

public class PdfPageSize {
	private double width;
	private double height;
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public PdfPageSize(double width, double height) {
		super();
		this.width = width;
		this.height = height;
	}
	public PdfPageSize() {
		super();
	}
}
