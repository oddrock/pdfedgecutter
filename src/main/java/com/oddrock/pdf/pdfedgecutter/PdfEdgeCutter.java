package com.oddrock.pdf.pdfedgecutter;

import java.io.File;
import java.io.IOException;
import com.oddrock.common.awt.RobotManager;
import com.oddrock.common.file.FileUtils;
import com.oddrock.common.pdf.PdfManager;
import com.oddrock.common.pdf.PdfSize;
import com.oddrock.common.windows.ClipboardUtils;
import com.oddrock.common.windows.CmdExecutor;
import com.oddrock.common.windows.CmdResult;
import com.oddrock.common.windows.GlobalKeyListener;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

public class PdfEdgeCutter {
	private static Logger logger = Logger.getLogger(PdfEdgeCutter.class);
	private static final String FOXIT_APP_PATH = "C:\\Program Files (x86)\\Foxit Software\\Foxit Phantom\\Foxit Phantom.exe";
	private static final String FOXIT_APP_NAME = "Foxit Phantom.exe";
	@SuppressWarnings("unused")
	private static final String KANKAN_APP_NAME = "KanKan.exe";
	
	// 认定像素点为白色时的R/G/B最小值
	private static final int MIN_R = 230;
	private static final int MIN_G = 230;
	private static final int MIN_B = 230;
	// 切白边截图时的X坐标、Y坐标、宽度、高度
	private static final int SCREEN_CAPTURE_CUTPAGE_X = 902;
	private static final int SCREEN_CAPTURE_CUTPAGE_Y = 258;
	private static final int SCREEN_CAPTURE_CUTPAGE_WIDTH = 396;
	private static final int SCREEN_CAPTURE_CUTPAGE_HEIGHT = 441;

	private static final double BAD_PNT_PROPORTION_THRESHOLD = 0.02;
	private static final double WHITELINE_PROPORTION = 0.8;

	
	private static final int DEALY_JUMP_NEXT_PAGE = 800;
	private static final int DELAY_AFTER_OPEN_PDF = 2000;
	private static final int MIDDLE_DELAY = 300;
	private static final int MIN_DELAY = 100;
	private static final int DEMO_PAGE_COUNT = 20;
	
	// 调整步长，每几个像素做一条线测试是否是白边
	private static final int AJDUST_STEP_LENGTH = 1;
	
	private RobotManager robotMngr;
	private PdfManager pdfMngr;
	
	private int scX;
	private int scY;
	private int scWidth;
	private int scHeight;
	
	
	public PdfEdgeCutter(boolean needEscKey, int... scParams) throws AWTException, NativeHookException{
		robotMngr = new RobotManager();
		pdfMngr = new PdfManager();
		if(needEscKey){
			GlobalScreen.registerNativeHook();//初始化ESC钩子 
	        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
		}
		if(scParams.length>=4){
			scX = scParams[0];
			scY = scParams[1];
			scWidth = scParams[2];
			scHeight = scParams[3];
		}else{
			scX = SCREEN_CAPTURE_CUTPAGE_X;
			scY = SCREEN_CAPTURE_CUTPAGE_Y;
			scWidth = SCREEN_CAPTURE_CUTPAGE_WIDTH;
			scHeight = SCREEN_CAPTURE_CUTPAGE_HEIGHT;
		}
	}

	/**
	 * 使用foxit打开一个pdf
	 * @param foxitAppPath
	 * @param pdfPath
	 * @return
	 */
	private CmdResult openPdfByFoxit(String foxitAppPath, String pdfPath) {
		return CmdExecutor.getSingleInstance().exeCmd(
				foxitAppPath + " \"" + pdfPath + "\"");
	}
	
	/**
	 * 关闭foxit
	 * @param foxitAppName
	 * @return
	 */
	private CmdResult closeFoxit(String foxitAppName) {
		return CmdExecutor.getSingleInstance().exeCmd(
				"taskkill /f /im \"" + foxitAppName + "\"");
	}
	
	/**
	 * 关闭看看
	 * @param kankanAppName
	 * @return
	 */
	@SuppressWarnings("unused")
	private CmdResult closeKanKan(String kankanAppName){
		return CmdExecutor.getSingleInstance().exeCmd(
				"taskkill /f /im \"" + kankanAppName + "\"");
	}
	/**
	 * 视图 | 缩放 | 实际大小
	 * 
	 * @throws AWTException
	 */
	private void zoom2SuitablePage() throws AWTException {
		robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		robotMngr.pressCombinationKey(KeyEvent.VK_CONTROL, KeyEvent.VK_2);
	}
	
	private void exitFoxitPdf() throws AWTException {
		robotMngr.pressCombinationKey(KeyEvent.VK_CONTROL, KeyEvent.VK_Q);
	}
	
	/**
	 * 视图 | 缩放 | 适合宽度
	 * 
	 * @throws AWTException
	 */
	private void zoom2SuitableWidth() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_Z);
		robotMngr.pressKey(KeyEvent.VK_W);
	}

	/**
	 * 视图 | 页面布局 | 单页
	 * 
	 * @throws AWTException
	 */
	private void singlePage() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_P);
		robotMngr.pressKey(KeyEvent.VK_S);
	}
	
	/**
	 * 视图 | 页面布局 | 连续
	 * 
	 * @throws AWTException
	 */
	private void conitnuousPage() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_P);
		robotMngr.pressKey(KeyEvent.VK_C);
	}

	/**
	 * 视图 | 跳至 | 第一页
	 * 
	 * @throws AWTException
	 */
	private void jumpFirstPage() throws AWTException {
		robotMngr.delay(DEALY_JUMP_NEXT_PAGE);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_G);
		robotMngr.pressKey(KeyEvent.VK_F);
	}

	/**
	 * 视图 | 跳至 | 下一页
	 * 
	 * @throws AWTException
	 */
	private void jumpNextPage() throws AWTException {
		robotMngr.delay(MIN_DELAY);
		robotMngr.pressRight();
	}

	/**
	 * 视图 | 跳至 | 跳至页面
	 * 
	 * @throws AWTException
	 */
	private void jumpSpecPage(int pageNum) throws AWTException {
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
	private void startCutPage() throws AWTException {
		robotMngr.delay(MIDDLE_DELAY);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_O);
		robotMngr.pressKey(KeyEvent.VK_C);
	}
	
	/**
	 * count的单位是0.1英寸
	 * @param i
	 */
	private void ajustSize(int count, int... delay){
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
	 * 将切边页面截图
	 * @return
	 */
	private BufferedImage screenCaptureCutPage(){
		robotMngr.delay(MIDDLE_DELAY);
		return robotMngr.createScreenCapture(new Rectangle(
				//Toolkit.getDefaultToolkit().getScreenSize()
				scX,
				scY,
				scWidth,
				scHeight
				));
	}
	
	/**
	 * 检查图片上某个点是否是白色
	 * @param image
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isWhitePoint(BufferedImage image, int x, int y){
		boolean flag = false;
		int RGB = image.getRGB(x, y);
		int R =(RGB & 0xff0000 ) >> 16 ;
		int G= (RGB & 0xff00 ) >> 8 ;
		int B= (RGB & 0xff );
		if(R>=MIN_R && G>=MIN_G && B>=MIN_B){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 截屏切边时的屏幕
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void showScreenCaptureCutPage() throws IOException{
		BufferedImage image = screenCaptureCutPage();
		File file = new File("C:\\Users\\oddro\\Desktop\\screencapture.jpg");
		ImageIO.write(image, "jpg", file);
		String cmd = "C:\\Program Files (x86)\\Meitu\\KanKan\\KanKan.exe C:\\Users\\oddro\\Desktop\\screencapture.jpg";
		CmdExecutor.getSingleInstance().exeCmd(cmd);
	}
	
	private boolean isWhiteVerticalLine(BufferedImage image, int x){
		int height = image.getHeight();
		int curLineStartY = -1;
		int curLineEndY = -1;
		int badPointCount = 0;		// 非白色点个数
		for(int y=1; y<=height-1; y++){	
			if(isWhitePoint(image, x ,y)){
				if(curLineStartY>0){
					curLineEndY = y;
				}else{
					curLineStartY = y;
				}
			}
			if(((double)(curLineEndY-curLineStartY)/(double)height)>=WHITELINE_PROPORTION 
					&& ((double)badPointCount/(double)(curLineEndY-curLineStartY))<=BAD_PNT_PROPORTION_THRESHOLD){
				return true;
			}
			if(!isWhitePoint(image, x ,y) && curLineStartY>0){
				if(curLineEndY>0 && ((double)badPointCount/(double)(curLineEndY-curLineStartY))>BAD_PNT_PROPORTION_THRESHOLD){
					curLineEndY = -1;
					curLineStartY = -1;
					badPointCount = 0;
				}else{
					badPointCount++;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获得白边外沿左边界X坐标
	 * @param image
	 * @return
	 */
	private int whiteMarginOutterLeftX(BufferedImage image){
		for(int x=1; x<image.getWidth()/3; x=x+AJDUST_STEP_LENGTH){
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
	private int whiteMarginInnerLeftX(BufferedImage image){
		int whiteMarginOutterLeftX = whiteMarginOutterLeftX(image);
		if(whiteMarginOutterLeftX<0){
			return -1;
		}
		int value = -1;
		for(int x=whiteMarginOutterLeftX; x<image.getWidth()/3; x=x+AJDUST_STEP_LENGTH){
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
	private int whiteMarginOutterRightX(BufferedImage image){
		for(int x=image.getWidth()-AJDUST_STEP_LENGTH; x>=image.getWidth()/3*2; x=x-AJDUST_STEP_LENGTH){
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
	private int whiteMarginInnerRightX(BufferedImage image){
		int whiteMarginOutterRightX = whiteMarginOutterRightX(image);
		if(whiteMarginOutterRightX<0){
			return -1;
		}
		int value = -1;
		for(int x=whiteMarginOutterRightX; x>=image.getWidth()/3*2; x=x-AJDUST_STEP_LENGTH){
			if(isWhiteVerticalLine(image, x)){
				value = x;
			}else{
				break;
			}
		}
		return value;
	}
	
	private boolean isWhiteHorizontalLine(BufferedImage image, int y){
		int width = image.getWidth();
		int curLineStartX = -1;
		int curLineEndX = -1;
		int badPointCount = 0;		// 非白色点个数
		for(int x=1; x<=width-1; x++){	
			if(isWhitePoint(image, x ,y)){
				if(curLineStartX>0){
					curLineEndX = x;
				}else{
					curLineStartX = x;
				}
			}
			if(((double)badPointCount/(double)(curLineEndX-curLineStartX))<=BAD_PNT_PROPORTION_THRESHOLD
					&& ((double)(curLineEndX-curLineStartX)/(double)width)>=WHITELINE_PROPORTION){
				return true;
			}
			if(!isWhitePoint(image, x ,y) && curLineStartX>0){
				if(curLineEndX>0 && ((double)badPointCount/(double)(curLineEndX-curLineStartX))>BAD_PNT_PROPORTION_THRESHOLD){
					curLineEndX = -1;
					curLineStartX = -1;
					badPointCount = 0;
				}else{
					badPointCount++;
				}
			}
		}
		return false;
		
		
		
	}

	private int whiteMarginOutterTopY(BufferedImage image){
		for(int y=1; y<image.getHeight()/3; y=y+AJDUST_STEP_LENGTH){
			if(isWhiteHorizontalLine(image, y)){
				return y;
			}
		}
		return -1;
	}
	
	private int whiteMarginInnerTopY(BufferedImage image){
		int whiteMarginOutterTopY = whiteMarginOutterTopY(image);
		if(whiteMarginOutterTopY<0){
			return -1;
		}
		int value = -1;
		for(int y=whiteMarginOutterTopY; y<image.getHeight()/3; y=y+AJDUST_STEP_LENGTH){
			if(isWhiteHorizontalLine(image, y)){
				value = y;
			}else{
				break;
			}
		}
		return value;
	}
	

	private int whiteMarginOutterBottomY(BufferedImage image){
		for(int y=image.getHeight()-AJDUST_STEP_LENGTH; y>=image.getHeight()/3*2; y=y-AJDUST_STEP_LENGTH){
			if(isWhiteHorizontalLine(image, y)){
				return y;
			}
		}
		return -1;
	}
	
	private int whiteMarginInnerBottomY(BufferedImage image){
		int whiteMarginOutterBottomY = whiteMarginOutterBottomY(image);
		if(whiteMarginOutterBottomY<0){
			return -1;
		}
		int value = -1;
		for(int y=whiteMarginOutterBottomY; y>=image.getHeight()/3*2; y=y-AJDUST_STEP_LENGTH){
			if(isWhiteHorizontalLine(image, y)){
				value = y;
			}else{
				break;
			}
		}
		return value;
	}
	
	/**
	 * 切当前页面页面的白边
	 * @throws AWTException
	 */
	private void cutCurrentPage(double width, double height, boolean... realOpt) throws AWTException{		
		startCutPage();
		BufferedImage image = screenCaptureCutPage();
		double left_margin = ((double)whiteMarginInnerLeftX(image)/(double)image.getWidth())*width;
		double right_margin;
		if(whiteMarginInnerRightX(image)>0){
			right_margin = ((double)(image.getWidth()-whiteMarginInnerRightX(image))/(double)image.getWidth())*width;
		}else{
			right_margin = -1;
		}
		double top_margin = ((double)whiteMarginInnerTopY(image)/(double)image.getHeight())*height;
		double bottom_margin;
		if(whiteMarginInnerBottomY(image)>0){
			bottom_margin = ((double)(image.getHeight()-whiteMarginInnerBottomY(image))/(double)image.getHeight())*height;
		}else{
			bottom_margin = -1;
		}
		if(Math.floor(top_margin*10)>0){
			ajustSize((int)Math.floor(top_margin*10));
		}
		robotMngr.pressTab();
		if(Math.floor(bottom_margin*10)>0){
			ajustSize((int)Math.floor(bottom_margin*10));
		}
		robotMngr.pressTab();
		if(Math.floor(left_margin*10)>0){
			ajustSize((int)Math.floor(left_margin*10));
		}
		robotMngr.pressTab();
		if(Math.floor(right_margin*10)>0){
			ajustSize((int)Math.floor(right_margin*10));
		}
		robotMngr.delay(MIN_DELAY);
		if(realOpt.length==0 || realOpt[0]!=false){
			robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_K);	
			robotMngr.pressEnter();	
		}	
	}
	
	/**
	 * 切白边前准备工作，页面单页并且调整至适合页面大小
	 * @throws AWTException 
	 */
	private void preCutPages() throws AWTException{
		jumpFirstPage();
		singlePage();
		zoom2SuitablePage();
	}
	
	/**
	 * 切白边后工作，页面连续，调整至适合宽度，回到第一页
	 * @throws AWTException
	 */
	private void postCutPages() throws AWTException{
		jumpFirstPage();
		conitnuousPage();
		zoom2SuitableWidth();
	}
	
	/**
	 * 
	 * @param pdfFilePath
	 * @param renameFlag	是否改名
	 * @param addstr		如果改名，在名称后增加addstr这个字符串
	 * @param newDirFlag	是否保存到新的目录下
	 * @param newDirPath	如果保存到新的目录下，这是新的目录
	 * @throws AWTException 
	 * @throws IOException 
	 */
	public void cutWhiteEdge(String pdfFilePath, 
			boolean renameFlag, String addstr, 
			boolean newDirFlag, String newDirPath, boolean demoFlag) 
			throws AWTException, IOException{
		PdfEdgeCutTimer timer = new PdfEdgeCutTimer();
		timer.start();
		closeFoxit(FOXIT_APP_NAME);
		robotMngr.delay(MIDDLE_DELAY);
		robotMngr.moveMouseToRightDownCorner();
		File file = new File(pdfFilePath);
		if(!file.exists() && !file.isFile()){
			logger.warn("文件【"+pdfFilePath+"】不存在");
			return;
		}
		if(!pdfMngr.canCutPage(pdfFilePath)){	// 检查是否具备切白边条件
			return;	
		}
		openPdfByFoxit(FOXIT_APP_PATH, pdfFilePath);
		preCutPages();
		PdfSize pdfSize = new PdfManager().pdfSize(pdfFilePath);
		int pageCount = new PdfManager().pdfPageCount(pdfFilePath);
		int endPageNum = pageCount;
		if(demoFlag){
			if(pageCount<=DEMO_PAGE_COUNT){
				endPageNum = pageCount/4;
			}else{
				endPageNum = DEMO_PAGE_COUNT;
			}
		}
		for(int i=1;i<=endPageNum;i++){
			cutCurrentPage(pdfSize.getPageWidthInch(i), pdfSize.getPageHeightInch(i));
			timer.countPages();
			jumpNextPage();
		}
		postCutPages();
		
		// 生成文件名
		String fileName = FileUtils.getFileNameWithoutSuffixFromFilePath(pdfFilePath);
		String suffix = FileUtils.getFileNameSuffix(pdfFilePath);
		String dirPath = FileUtils.getDirPathFromFilePath(pdfFilePath);	
		if(newDirFlag && newDirPath!=null && newDirPath.trim().length()>0){
			dirPath = newDirPath.trim();
		}
		if(renameFlag && addstr!=null && addstr.trim().length()>0){
			fileName = fileName + addstr;
		}
		if(suffix.length()>0){
			fileName = fileName + "." + suffix;
		}
		FileUtils.mkdirIfNotExists(dirPath);
		String destFilePath = dirPath + java.io.File.separator+fileName;
		saveFoxitPdf(destFilePath);
		robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		exitFoxitPdf();
		logger.warn("切白边完成，【源文件】"+pdfFilePath+"，【目标文件】"+destFilePath);
		timer.showSpeedPer100Pages();
	}
	
	/**
	 * 批量切一个文件夹下的PDF文件的白边
	 * @param pdfDirPath
	 * @param renameFlag
	 * @param addstr
	 * @param newDirFlag
	 * @param newDirPath
	 * @throws IOException 
	 * @throws AWTException 
	 */
	public void cutWhiteEdgeBatch(String pdfDirPath, 
			boolean renameFlag, String addstr, 
			boolean newDirFlag, String newDirPath) throws AWTException, IOException{
		PdfEdgeCutTimer timer = new PdfEdgeCutTimer();
		timer.start();
		File srcDir = new File(pdfDirPath);
		if (!srcDir.exists() || !srcDir.isDirectory()) {
			return;
		}
		File[] files = srcDir.listFiles();
		for (File file : files) {
			if(file.isFile()){
				if(FileUtils.getFileNameSuffix(file.getName()).equalsIgnoreCase("pdf")){
					cutWhiteEdge(file.getAbsolutePath(), renameFlag, addstr, newDirFlag, newDirPath, false);
					timer.countPdf();
					timer.countPages(new PdfManager().pdfPageCount(file.getAbsolutePath()));
				}
			}   
        }
		timer.end();
		timer.showSpeed();
	}
	
	/**
	 * 保存foxit pdf
	 * @param destFilePath
	 */
	private void saveFoxitPdf(String destFilePath){
		ClipboardUtils.setSysClipboardText(destFilePath);
		robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		robotMngr.pressCombinationKey(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_S);
		robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		robotMngr.pressCombinationKey(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
		robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_S);
		robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		robotMngr.pressKey(KeyEvent.VK_Y);
		robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		robotMngr.pressEnter();
	}
	
	/**
	 * 对某个文件某一页模拟切白边
	 * @param pdfFilePath
	 * @param pageNum
	 * @throws AWTException 
	 * @throws IOException 
	 */
	public void simCutEdgeOnePage(String pdfFilePath, int pageNum) throws AWTException, IOException{
		closeFoxit(FOXIT_APP_NAME);
		robotMngr.delay(MIDDLE_DELAY);
		openPdfByFoxit(FOXIT_APP_PATH, pdfFilePath);
		preCutPages();
		jumpSpecPage(pageNum);
		PdfSize pdfSize = new PdfManager().pdfSize(pdfFilePath);
		cutCurrentPage(pdfSize.getPageWidthInch(pageNum), pdfSize.getPageHeightInch(pageNum),false);
	}
	
	public boolean isWhiteVerticalLine(String pdfFilePath, int pageNum, int x) throws AWTException{
		closeFoxit(FOXIT_APP_NAME);
		robotMngr.delay(MIDDLE_DELAY);
		openPdfByFoxit(FOXIT_APP_PATH, pdfFilePath);
		preCutPages();
		jumpSpecPage(pageNum);
		startCutPage();
		BufferedImage image = screenCaptureCutPage();
		boolean result = isWhiteVerticalLine(image, x);
		logger.warn(result);
		return result;
	}
	
	public static void main(String[] args) throws IOException, AWTException, NativeHookException {		
		int scX = 902;
		int scY = 258;
		int scWidth = 396;
		int scHeight = 441;
		String srcDirPath = "C:\\Users\\oddro\\Desktop\\pdf测试";
		String dstDirPath = "C:\\Users\\oddro\\Desktop\\qiebaibian";
		String apendName = "_切白边";
		if(args.length>=1 && !args[0].trim().equals("-")){
			srcDirPath = args[0];
		}
		if(args.length>=2 && !args[1].trim().equals("-")){
			dstDirPath = args[1];
		}
		if(args.length>=3 && !args[2].trim().equals("-")){
			apendName = args[2];
		}
		if(args.length>=7){
			scX = Integer.valueOf(args[3]);
			scY = Integer.valueOf(args[4]);
			scWidth = Integer.valueOf(args[5]);
			scHeight = Integer.valueOf(args[6]);
		}
		PdfEdgeCutter cutter = new PdfEdgeCutter(true, scX, scY, scWidth, scHeight);
		cutter.cutWhiteEdgeBatch(srcDirPath, true, apendName, true, dstDirPath);
		
		/*cutter.cutWhiteEdge("C:\\Users\\oddro\\Desktop\\pdf测试\\123.pdf", 
				true, "_切白边", true, "C:\\Users\\oddro\\Desktop\\qiebaibian", true);*/

		//String pdfDirPath = "C:\\Users\\oddro\\Desktop\\pdf测试";
		
		
		//cutter.simCutEdgeOnePage("C:\\Users\\oddro\\Desktop\\pdf测试\\启示录 打造用户喜爱的产品.pdf", 11);
		/*for(int i = 45; i< 66; i++)
			cutter.isWhiteVerticalLine("C:\\Users\\oddro\\Desktop\\pdf测试\\结网_切白边.pdf", 2, i);*/
		
		/*String pdfFilePath = "C:\\Users\\oddro\\Desktop\\pdf测试\\产品策划-精益求精：卓越的互联网产品设计与管理.pdf";
		cutter.closeFoxit(FOXIT_APP_NAME);
		cutter.robotMngr.delay(MIDDLE_DELAY);
		cutter.openPdfByFoxit(FOXIT_APP_PATH, pdfFilePath);
		cutter.robotMngr.delay(DELAY_AFTER_OPEN_PDF);
		cutter.preCutPages();
		cutter.robotMngr.delay(MIN_DELAY);
		cutter.getCurrentPageSize();*/
		
		System.exit(0);
	}
}
