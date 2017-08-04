package com.oddrock.pdf.pdfedgecutter;

import java.io.File;
import java.io.IOException;

import com.oddrock.common.awt.RobotManager;
import com.oddrock.common.windows.CmdExecutor;
import com.oddrock.common.windows.CmdResult;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class PdfEdgeCutter {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(PdfEdgeCutter.class);
	private static final String FOXIT_APP_PATH = "C:\\Program Files (x86)\\Foxit Software\\Foxit Phantom\\Foxit Phantom.exe";
	private static final String FOXIT_APP_NAME = "Foxit Phantom.exe";
	private static final String KANKAN_APP_NAME = "KanKan.exe";
	private static final int OPEN_DELAY = 2000;
	private static final int MIDDLE_DELAY = 300;
	private static final int MIN_DELAY = 100;
	// 认定像素点为白色时的R/G/B最小值
	private static final int MIN_R = 250;
	private static final int MIN_G = 250;
	private static final int MIN_B = 250;
	// 切白边截图时的X坐标、Y坐标、宽度、高度
	private static final int SCREEN_CAPTURE_CUTPAGE_X = 902;
	private static final int SCREEN_CAPTURE_CUTPAGE_Y = 258;
	private static final int SCREEN_CAPTURE_CUTPAGE_WIDTH = 396;
	private static final int SCREEN_CAPTURE_CUTPAGE_HEIGHT = 441;
	/* 	判断基准线和PDF文字内容是否存在交叉的非白点的阀值。
		如果基准线一侧的非白点的个数高于这个阀值，就判断基准线和PDF内容存在交叉。 */
	private static final int CROSS_POINT_THRESHOLD = 6;
	
	private static final int BAD_POINT_COUNT_THRESHOLD = 6;
	private static final double BAD_POINT_PROPORTION_THRESHOLD = 0.02;
	private static final double WHITELINE_HEIGHT_PROPORTION = 0.8;
	
	private static final double FOXIT_PDF_WIDTH = 7.01;
	private static final double FOXIT_PDF_HEIGHT = 9.19;
	private static final int DEALY_JUMP_NEXT_PAGE = 800;
	

	public static CmdResult openPdfByFoxit(String foxitAppPath, String pdfPath) {
		return CmdExecutor.getSingleInstance().exeCmd(
				foxitAppPath + " " + pdfPath);
	}

	public static CmdResult closeFoxit(String foxitAppName) {
		return CmdExecutor.getSingleInstance().exeCmd(
				"taskkill /f /im \"" + foxitAppName + "\"");
	}
	
	public static CmdResult closeKanKan(String kankanAppName){
		return CmdExecutor.getSingleInstance().exeCmd(
				"taskkill /f /im \"" + kankanAppName + "\"");
	}

	public static RobotManager robotMngr;
	static {
		try {
			robotMngr = new RobotManager();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 视图 | 缩放 | 实际大小
	 * 
	 * @throws AWTException
	 */
	public static void zoom2SuitablePage() throws AWTException {
		robotMngr.delay(OPEN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_CONTROL, KeyEvent.VK_2);
	}
	
	/**
	 * 视图 | 缩放 | 适合宽度
	 * 
	 * @throws AWTException
	 */
	public static void zoom2SuitableWidth() throws AWTException {
		robotMngr.delay(OPEN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_P);
		robotMngr.pressKey(KeyEvent.VK_S);
	}

	/**
	 * 视图 | 页面布局 | 单页
	 * 
	 * @throws AWTException
	 */
	public static void singlePage() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_P);
		robotMngr.pressKey(KeyEvent.VK_S);
	}

	/**
	 * 视图 | 跳至 | 第一页
	 * 
	 * @throws AWTException
	 */
	public static void jumpFirstPage() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_G);
		robotMngr.pressKey(KeyEvent.VK_F);
	}

	/**
	 * 视图 | 跳至 | 下一页
	 * 
	 * @throws AWTException
	 */
	public static void jumpNextPage() throws AWTException {
		robotMngr.delay(DEALY_JUMP_NEXT_PAGE);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_G);
		robotMngr.pressKey(KeyEvent.VK_N);
	}

	/**
	 * 视图 | 跳至 | 跳至页面
	 * 
	 * @throws AWTException
	 */
	public static void jumpSpecPage(int pageNum) throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_G);
		robotMngr.pressKey(KeyEvent.VK_A);
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressContinuousKey(String.valueOf(pageNum));
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressEnter();
	}
	
	/**
	 * 开始裁剪页面
	 * 
	 * @throws AWTException
	 */
	public static void startCutPage() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_O);
		robotMngr.pressKey(KeyEvent.VK_C);
	}

	/**
	 * 开始裁剪页面顶部
	 * 
	 * @throws AWTException
	 */
	public static void startCutPageTop() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_O);
		robotMngr.pressKey(KeyEvent.VK_C);
	}

	/**
	 * 开始裁剪页面底部
	 * 
	 * @throws AWTException
	 */
	public static void startCutPageBottom() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_O);
		robotMngr.pressKey(KeyEvent.VK_C);
		robotMngr.pressTab();
	}

	/**
	 * 开始裁剪页面右面
	 * 
	 * @throws AWTException
	 */
	public static void startCutPageRight() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_O);
		robotMngr.pressKey(KeyEvent.VK_C);
		robotMngr.pressTab();
		robotMngr.pressTab();
	}

	/**
	 * 开始裁剪页面左面
	 * 
	 * @throws AWTException
	 */
	public static void startCutPageLeft() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_O);
		robotMngr.pressKey(KeyEvent.VK_C);
		robotMngr.pressTab();
		robotMngr.pressTab();
		robotMngr.pressTab();
	}
	
	/**
	 * count的单位是0.1英寸
	 * @param i
	 */
	public static void ajustSize(int count, int... delay){
		if(delay.length>0 && delay[0]>0){
			robotMngr.delay(delay[0]);
		}
		if(count>0){
			for(int i=0; i<count; i++){
				robotMngr.pressUp();
			}
		}else{
			count = count * -1;
			for(int i=0; i<count; i++){
				robotMngr.pressDown();
			}
		}
		
	}
	
	
	

	
	
	/**
	 * 切页面顶部白边
	 * @throws AWTException
	 */
	public static void cutPageTop() throws AWTException{
		robotMngr.delay(MIN_DELAY);
		startCutPageTop();
		getPageTopBaseline();
		robotMngr.pressEnter();
	}
	
	/**
	 * 切页面底部白边
	 * @throws AWTException
	 * @throws IOException 
	 */
	public static void cutPageBottom() throws AWTException, IOException{
		robotMngr.delay(MIN_DELAY);
		startCutPageBottom();
		/*horizontalDistance(2);
		BufferedImage image = screenCaptureCutPage();
		System.out.println(image.getHeight());
		int RGB = image.getRGB(3, image.getHeight()-19);
		int R =(RGB & 0xff0000 ) >> 16 ;
		int G= (RGB & 0xff00 ) >> 8 ;
		int B= (RGB & 0xff );
		System.out.println(R + ","+G+","+B);
		robotMngr.delay(MIN_DELAY);
		File file = new File("C:\\Users\\oddro\\Desktop\\screencapture.jpg");
		ImageIO.write(image, "jpg", file);
		String cmd = "C:\\Program Files (x86)\\Meitu\\KanKan\\KanKan.exe C:\\Users\\oddro\\Desktop\\screencapture.jpg";
		CmdExecutor.getSingleInstance().exeCmd(cmd);
		System.out.println(findEndYOfBottomBaseline(screenCaptureCutPage()));*/
		getPageBottomBaseline();
		robotMngr.pressEnter();
	}
	
	/**
	 * 获得页面底部白边基准线
	 */
	private static void getPageBottomBaseline() {
		robotMngr.delay(MIN_DELAY);
		BufferedImage image = screenCaptureCutPage();
		int height = image.getHeight();
		int cutInch = image.getHeight()-3;
		while(cutInch>=1){	
			ajustSize(1);
			image = screenCaptureCutPage();
			if(isHorizontalBaselineCrossWithContent(image, findEndYOfBottomBaseline(image))){
				if(cutInch<=height){
					ajustSize(-1);
				}
				break;
			}
			cutInch++;
		}
	}

	/**
	 * 获得页面顶部白边的基准线
	 */
	public static void getPageTopBaseline(){
		robotMngr.delay(MIN_DELAY);
		BufferedImage image = screenCaptureCutPage();
		int height = image.getHeight();
		int cutInch = 1;
		while(cutInch<=height){	
			ajustSize(1);
			image = screenCaptureCutPage();
			if(isHorizontalBaselineCrossWithContent(image, findEndYOfTopBaseline(image))){
				if(cutInch>=1){
					ajustSize(-1);
				}
				break;
			}
			cutInch++;
		}
	}
	
	/**
	 * 寻找切白边时下基准线上沿的Y坐标值（基准线上1个像素）
	 * @param image
	 * @return
	 */
	private static int findEndYOfBottomBaseline(BufferedImage image) {
		int height = image.getHeight();
		int startY = height-2;
		int endY = 1;
		for(int y=height-2; y>1; y--){
			if(!isWhitePoint(image,3,y)){
				startY = y;	
				break;
			}
		}
		for(int y=startY-1; y>1; y--){
			if(isWhitePoint(image,3,y)){
				endY = y;		
				break;
			}
		}
		return endY;
	}

	/**
	 * 寻找切白边时上基准线下沿的Y坐标值（基准线下1个像素）
	 * @param image
	 * @return
	 */
	public static int findEndYOfTopBaseline(BufferedImage image){
		int height = image.getHeight();
		int startY = 2;
		int endY = height;
		for(int y=2; y<=height; y++){
			if(!isWhitePoint(image,3,y)){
				startY = y;	
				break;
			}
		}
		for(int y=startY+1; y<=height; y++){
			if(isWhitePoint(image,3,y)){
				endY = y;		
				break;
			}
		}
		return endY;
	}
	
	/**
	 * 检查水平线是否与PDF的内容交叉了
	 * @param image
	 * @param y	水平基准线的上沿或下沿的Y坐标值
	 * @return
	 */
	public static boolean isHorizontalBaselineCrossWithContent(BufferedImage image, int y){
		int startX = 3;
		int endX = image.getWidth()-3;
		int crossCount = 0;
		for(int x=startX; x<=endX;  x++){
			if(!isWhitePoint(image, x, y)){
				crossCount++;
			}
		}
		if(crossCount>=CROSS_POINT_THRESHOLD){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 检查垂直基准线是否与PDF的内容交叉了
	 * @param image
	 * @param x	垂直基准线的左沿或右沿的Y坐标值
	 * @return
	 */
	public static boolean isVerticalBaselineCrossWithContent(BufferedImage image, int x){
		int startY = 3;
		int endY = image.getHeight()-3;
		int crossCount = 0;
		for(int y=startY; y<=endY;  y++){
			if(!isWhitePoint(image, x, y)){
				crossCount++;
			}
		}
		if(crossCount>=CROSS_POINT_THRESHOLD){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 将切边页面截图
	 * @return
	 */
	public static BufferedImage screenCaptureCutPage(){
		robotMngr.delay(MIDDLE_DELAY);
		return robotMngr.createScreenCapture(new Rectangle(
				//Toolkit.getDefaultToolkit().getScreenSize()
				SCREEN_CAPTURE_CUTPAGE_X,
				SCREEN_CAPTURE_CUTPAGE_Y,
				SCREEN_CAPTURE_CUTPAGE_WIDTH,
				SCREEN_CAPTURE_CUTPAGE_HEIGHT
				));
	}
	
	/**
	 * 检查图片上某个点是否是白色
	 * @param image
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isWhitePoint(BufferedImage image, int x, int y){
		boolean flag = false;
		int RGB = image.getRGB(x, y);
		int R =(RGB & 0xff0000 ) >> 16 ;
		int G= (RGB & 0xff00 ) >> 8 ;
		int B= (RGB & 0xff );
		//System.out.println(R + ","+G+","+B);
		/*Color mycolor = new Color(RGB);
		int R = mycolor.getRed();
		int G = mycolor.getGreen();
		int B = mycolor.getBlue();*/
		if(R>=MIN_R && G>=MIN_G && B>=MIN_B){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 测试左边黑边宽度
	 * @return	左边黑边宽度(单位为像素)
	 */
	public static int measureLeftBlackEdgeWidth(){
		// 左边黑边宽度(单位为像素)
		int LeftBlackEdgeWidth = 0;
		return LeftBlackEdgeWidth;
	}
	
	/**
	 * 截屏切边时的屏幕
	 * @throws IOException
	 */
	public static void showScreenCaptureCutPage() throws IOException{
		BufferedImage image = screenCaptureCutPage();
		File file = new File("C:\\Users\\oddro\\Desktop\\screencapture.jpg");
		ImageIO.write(image, "jpg", file);
		String cmd = "C:\\Program Files (x86)\\Meitu\\KanKan\\KanKan.exe C:\\Users\\oddro\\Desktop\\screencapture.jpg";
		CmdExecutor.getSingleInstance().exeCmd(cmd);
	}
	
	@SuppressWarnings("unused")
	public static boolean isWhiteVerticalLine(BufferedImage image, int x){
		int height = image.getHeight();
		int lastWhiteLineMinY = -1;
		int lastWhiteLineMaxY = -1;
		int lastNotwhitePointCount = 0;
		int whiteLineMinY = -1;
		int whiteLineMaxY = -1;
		int notwhitePointCount = 0;		// 非白色点个数
		boolean firstFind = true;
		boolean findWhiteLine = false;
		for(int y=1; y<=height-1; y++){
			if(isWhitePoint(image, x ,y)){
				if(findWhiteLine){
					if(firstFind){
						lastWhiteLineMaxY = y;
					}
					whiteLineMaxY = y;
				}else{
					findWhiteLine = true;
					if(firstFind){
						lastWhiteLineMinY = y;
						lastWhiteLineMaxY = y;
						lastNotwhitePointCount = 0;
					}
					notwhitePointCount = 0;
					whiteLineMinY = y;
					whiteLineMaxY = y;
				}
			}else{
				if(findWhiteLine){
					notwhitePointCount++;
					if(firstFind){
						lastNotwhitePointCount++;
					}
					if(notwhitePointCount>BAD_POINT_COUNT_THRESHOLD){
						if(firstFind){
							firstFind = false;
						}else{
							if((whiteLineMaxY-whiteLineMinY)>(lastWhiteLineMaxY-lastWhiteLineMinY)){
								notwhitePointCount = notwhitePointCount -1;
								if(((double)notwhitePointCount/(double)(whiteLineMaxY-whiteLineMinY))
										<=BAD_POINT_PROPORTION_THRESHOLD){
									lastWhiteLineMaxY = whiteLineMaxY;
									lastWhiteLineMinY = whiteLineMinY;
									lastNotwhitePointCount = notwhitePointCount;
								}		
							}
						}
						findWhiteLine = false;
					}
				}
			}
		}
		if(((double)(lastWhiteLineMaxY-lastWhiteLineMinY)/(double)height)>=WHITELINE_HEIGHT_PROPORTION){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 获得白边外沿左边界X坐标
	 * @param image
	 * @return
	 */
	public static int whiteMarginOutterLeftX(BufferedImage image){
		for(int x=1; x<image.getWidth()/3; x++){
			if(isWhiteVerticalLine(image, x)){
				return x;
			}
		}
		return -1;
	}
	
	/**
	 * 获得白边内沿左边界X坐标
	 * @param image
	 * @return
	 */
	public static int whiteMarginInnerLeftX(BufferedImage image){
		int whiteMarginOutterLeftX = whiteMarginOutterLeftX(image);
		if(whiteMarginOutterLeftX<0){
			return -1;
		}
		int value = -1;
		for(int x=whiteMarginOutterLeftX; x<image.getWidth()/3; x++){
			if(isWhiteVerticalLine(image, x)){
				value = x;
			}else{
				break;
			}
		}
		return value;
	}
	
	/**
	 * 获得白边外沿右边界X坐标
	 * @param image
	 * @return
	 */
	public static int whiteMarginOutterRightX(BufferedImage image){
		for(int x=image.getWidth()-1; x>=image.getWidth()/3*2; x--){
			if(isWhiteVerticalLine(image, x)){
				return x;
			}
		}
		return -1;
	}
	
	/**
	 * 获得白边内沿右边界X坐标
	 * @param image
	 * @return
	 */
	public static int whiteMarginInnerRightX(BufferedImage image){
		int whiteMarginOutterRightX = whiteMarginOutterRightX(image);
		if(whiteMarginOutterRightX<0){
			return -1;
		}
		int value = -1;
		for(int x=whiteMarginOutterRightX; x>=image.getWidth()/3*2; x--){
			if(isWhiteVerticalLine(image, x)){
				value = x;
			}else{
				break;
			}
		}
		return value;
	}
	
	@SuppressWarnings("unused")
	public static boolean isWhiteHorizontalLine(BufferedImage image, int y){
		int width = image.getWidth();
		int lastWhiteLineMinY = -1;
		int lastWhiteLineMaxY = -1;
		int lastNotwhitePointCount = 0;
		int whiteLineMinY = -1;
		int whiteLineMaxY = -1;
		int notwhitePointCount = 0;		// 非白色点个数
		boolean firstFind = true;
		boolean findWhiteLine = false;
		for(int x=1; x<=width-1; x++){
			if(isWhitePoint(image, x, y)){
				if(findWhiteLine){
					if(firstFind){
						lastWhiteLineMaxY = x;
					}
					whiteLineMaxY = x;
				}else{
					findWhiteLine = true;
					if(firstFind){
						lastWhiteLineMinY = x;
						lastWhiteLineMaxY = x;
						lastNotwhitePointCount = 0;
					}
					notwhitePointCount = 0;
					whiteLineMinY = x;
					whiteLineMaxY = x;
				}
			}else{
				if(findWhiteLine){
					notwhitePointCount++;
					if(firstFind){
						lastNotwhitePointCount++;
					}
					if(notwhitePointCount>BAD_POINT_COUNT_THRESHOLD){
						if(firstFind){
							firstFind = false;
						}else{
							if((whiteLineMaxY-whiteLineMinY)>(lastWhiteLineMaxY-lastWhiteLineMinY)){
								notwhitePointCount = notwhitePointCount -1;
								if(((double)notwhitePointCount/(double)(whiteLineMaxY-whiteLineMinY))
										<=BAD_POINT_PROPORTION_THRESHOLD){
									lastWhiteLineMaxY = whiteLineMaxY;
									lastWhiteLineMinY = whiteLineMinY;
									lastNotwhitePointCount = notwhitePointCount;
								}		
							}
						}
						findWhiteLine = false;
					}
				}
			}
		}
		if(((double)(lastWhiteLineMaxY-lastWhiteLineMinY)/(double)width)>=WHITELINE_HEIGHT_PROPORTION){
			return true;
		}else{
			return false;
		}
	}

	public static int whiteMarginOutterTopY(BufferedImage image){
		for(int y=1; y<image.getHeight()/3; y++){
			if(isWhiteHorizontalLine(image, y)){
				return y;
			}
		}
		return -1;
	}
	
	public static int whiteMarginInnerTopY(BufferedImage image){
		int whiteMarginOutterTopY = whiteMarginOutterTopY(image);
		if(whiteMarginOutterTopY<0){
			return -1;
		}
		int value = -1;
		for(int y=whiteMarginOutterTopY; y<image.getHeight()/3; y++){
			if(isWhiteHorizontalLine(image, y)){
				value = y;
			}else{
				break;
			}
		}
		return value;
	}
	

	public static int whiteMarginOutterBottomY(BufferedImage image){
		for(int y=image.getHeight()-1; y>=image.getHeight()/3*2; y--){
			if(isWhiteHorizontalLine(image, y)){
				return y;
			}
		}
		return -1;
	}
	
	public static int whiteMarginInnerBottomY(BufferedImage image){
		int whiteMarginOutterBottomY = whiteMarginOutterBottomY(image);
		if(whiteMarginOutterBottomY<0){
			return -1;
		}
		int value = -1;
		for(int y=whiteMarginOutterBottomY; y>=image.getHeight()/3*2; y--){
			if(isWhiteHorizontalLine(image, y)){
				value = y;
			}else{
				break;
			}
		}
		return value;
	}
	
	/**
	 * 切一个页面的白边
	 * @throws AWTException
	 */
	public static void cutOnePage(boolean... realOpt) throws AWTException{
		startCutPage();
		BufferedImage image = screenCaptureCutPage();
		double left_margin = ((double)whiteMarginInnerLeftX(image)/(double)image.getWidth())*FOXIT_PDF_WIDTH;
		double right_margin;
		if(whiteMarginInnerRightX(image)>0){
			right_margin = ((double)(image.getWidth()-whiteMarginInnerRightX(image))/(double)image.getWidth())*FOXIT_PDF_WIDTH;
		}else{
			right_margin = -1;
		}
		double top_margin = ((double)whiteMarginInnerTopY(image)/(double)image.getHeight())*FOXIT_PDF_HEIGHT;
		double bottom_margin;
		if(whiteMarginInnerBottomY(image)>0){
			bottom_margin = ((double)(image.getHeight()-whiteMarginInnerBottomY(image))/(double)image.getHeight())*FOXIT_PDF_HEIGHT;
		}else{
			bottom_margin = -1;
		}
		if(Math.floor(top_margin*10)>0){
			ajustSize((int)Math.floor(top_margin*10));
			//System.out.println((int)Math.floor(top_margin*10));
		}
		robotMngr.pressTab();
		if(Math.floor(bottom_margin*10)>0){
			ajustSize((int)Math.floor(bottom_margin*10));
			//System.out.println((int)Math.floor(bottom_margin*10));
		}
		robotMngr.pressTab();
		if(Math.floor(left_margin*10)>0){
			ajustSize((int)Math.floor(left_margin*10));
			//System.out.println((int)Math.floor(left_margin*10));
		}
		robotMngr.pressTab();
		if(Math.floor(right_margin*10)>0){
			ajustSize((int)Math.floor(right_margin*10));
			//System.out.println((int)Math.floor(right_margin*10));
		}
		if(realOpt.length==0 || realOpt[0]!=false){
			robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_K);	
		}	
	}
	
	/**
	 * 切白边前准备工作，页面单页并且调整至适合页面大小
	 * @throws AWTException 
	 */
	public static void preCutPages() throws AWTException{
		zoom2SuitablePage();
		singlePage();
		jumpFirstPage();
	}
	
	public static void main(String[] args) throws IOException, AWTException {
		closeFoxit(FOXIT_APP_NAME);
		closeFoxit(KANKAN_APP_NAME);
		robotMngr.delay(MIDDLE_DELAY);
		openPdfByFoxit(FOXIT_APP_PATH, "C:\\Users\\oddro\\Desktop\\Hadoop权威指南第三版(英文).pdf");
		preCutPages();
		/*jumpSpecPage(2);
		cutPage();*/
		for(int i=0;i<30;i++){
			cutOnePage();
			jumpNextPage();
		}
		//jumpSpecPage(672);	
		/*BufferedImage image = screenCaptureCutPage();
		int y = findEndYOfTopBaseline(image);
		System.out.println(isHorizontalBaselineCrossWithContent(image,y));
		File file = new File("C:\\Users\\oddro\\Desktop\\screencapture.jpg");
		ImageIO.write(image, "jpg", file);
		String cmd = "C:\\Program Files (x86)\\Meitu\\KanKan\\KanKan.exe C:\\Users\\oddro\\Desktop\\screencapture.jpg";
		CmdExecutor.getSingleInstance().exeCmd(cmd);*/
	}
}
