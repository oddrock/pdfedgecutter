package com.oddrock.pdf.pdfedgecutter;

public class FoxitPdfPage {
	private double width;
	private double height;
	public FoxitPdfPage() {
		super();
	}
	public FoxitPdfPage(double width, double height) {
		super();
		this.width = width;
		this.height = height;
	}
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
}
