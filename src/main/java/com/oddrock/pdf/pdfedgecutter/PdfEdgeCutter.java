package com.oddrock.pdf.pdfedgecutter;

import com.oddrock.common.windows.GlobalKeyListener;

import org.jnativehook.GlobalScreen;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.oddrock.common.awt.RobotManager;
import com.oddrock.common.email.EmailManager;
import com.oddrock.common.file.FileUtils;
import com.oddrock.common.pdf.PdfManager;
import com.oddrock.common.pdf.PdfSize;
import com.oddrock.common.windows.ClipboardUtils;
import com.oddrock.common.windows.CmdExecutor;
import com.oddrock.common.windows.CmdResult;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.jnativehook.NativeHookException;

public class PdfEdgeCutter {
	private static Logger logger = Logger.getLogger(PdfEdgeCutter.class);
	
	// 认定像素点为白色时的R/G/B最小值
	private static int threshold_whitepnt_rmin;
	private static int threshold_whitepnt_gmin;
	private static int threshold_whitepnt_bmin;

	private double threshold_badpnt_proportion;
	private double threshold_whiteline_proportion;

	private int delay_beforejumpnextpage;
	private int delay_afteropenpdf;
	private int delay_middle;
	private int delay_min;
	private int demo_pagecount;
	
	// 调整步长，每几个像素做一条线测试是否是白边
	private int ajdust_step_length;
	private RobotManager robotMngr;
	private PdfManager pdfMngr;
	private int scX;
	private int scY;
	private int scWidth;
	private int scHeight;
	private String foxitAppPath;
	private String foxitAppName;
	private int delay_aftersavepdf;
	
	public PdfEdgeCutter(boolean needEscKey, String foxitAppPath, String foxitAppName, int... scParams) throws AWTException, NativeHookException{
		robotMngr = new RobotManager();
		pdfMngr = new PdfManager();
		if(needEscKey){
			GlobalScreen.registerNativeHook();//初始化ESC钩子 
	        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
		}
		this.foxitAppPath = foxitAppPath;
		this.foxitAppName = foxitAppName;
		scX = scParams[0];
		scY = scParams[1];
		scWidth = scParams[2];
		scHeight = scParams[3];
		ajdust_step_length = scParams[4];
		delay_beforejumpnextpage = Integer.parseInt(Prop.get("delay.beforejumpnextpage"));
		delay_afteropenpdf = Integer.parseInt(Prop.get("delay.afteropenpdf"));
		delay_middle = Integer.parseInt(Prop.get("delay.middle"));
		delay_min = Integer.parseInt(Prop.get("delay.min"));
		delay_aftersavepdf = Integer.parseInt(Prop.get("delay.aftersavepdf"));
		demo_pagecount = Integer.parseInt(Prop.get("demo.pagecount"));
		threshold_badpnt_proportion = Double.parseDouble(Prop.get("threshold.badpnt.proportion"));
		threshold_whiteline_proportion = Double.parseDouble(Prop.get("threshold.whiteline.proportion"));
		threshold_whitepnt_rmin = Integer.parseInt(Prop.get("threshold.whitepnt.rmin"));
		threshold_whitepnt_gmin = Integer.parseInt(Prop.get("threshold.whitepnt.gmin"));
		threshold_whitepnt_bmin = Integer.parseInt(Prop.get("threshold.whitepnt.bmin"));
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
		robotMngr.delay(delay_afteropenpdf);
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
		robotMngr.delay(delay_min);
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
		robotMngr.delay(delay_min);
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
		robotMngr.delay(delay_min);
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
		robotMngr.delay(delay_beforejumpnextpage);
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
		robotMngr.delay(delay_min);
		robotMngr.pressRight();
	}

	/**
	 * 视图 | 跳至 | 跳至页面
	 * 
	 * @throws AWTException
	 */
	private void jumpSpecPage(int pageNum) throws AWTException {
		robotMngr.delay(delay_min);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_V);
		robotMngr.pressKey(KeyEvent.VK_G);
		robotMngr.pressKey(KeyEvent.VK_A);
		robotMngr.delay(delay_min);
		robotMngr.pressContinuousKey(String.valueOf(pageNum));
		robotMngr.delay(delay_min);
		robotMngr.pressEnter();
	}
	
	/**
	 * 开始裁剪页面
	 * 
	 * @throws AWTException
	 */
	private void startCutPage() throws AWTException {
		robotMngr.delay(delay_middle);
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
		robotMngr.delay(delay_middle);
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
		if(R>=threshold_whitepnt_rmin && G>=threshold_whitepnt_gmin && B>=threshold_whitepnt_bmin){
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
			if(((double)(curLineEndY-curLineStartY)/(double)height)>=threshold_whiteline_proportion 
					&& ((double)badPointCount/(double)(curLineEndY-curLineStartY))<=threshold_badpnt_proportion){
				return true;
			}
			if(!isWhitePoint(image, x ,y) && curLineStartY>0){
				if(curLineEndY>0 && ((double)badPointCount/(double)(curLineEndY-curLineStartY))>threshold_badpnt_proportion){
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
		for(int x=1; x<image.getWidth()/3; x=x+ajdust_step_length){
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
		for(int x=whiteMarginOutterLeftX; x<image.getWidth()/3; x=x+ajdust_step_length){
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
		for(int x=image.getWidth()-ajdust_step_length; x>=image.getWidth()/3*2; x=x-ajdust_step_length){
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
		for(int x=whiteMarginOutterRightX; x>=image.getWidth()/3*2; x=x-ajdust_step_length){
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
			if(((double)badPointCount/(double)(curLineEndX-curLineStartX))<=threshold_badpnt_proportion
					&& ((double)(curLineEndX-curLineStartX)/(double)width)>=threshold_whiteline_proportion){
				return true;
			}
			if(!isWhitePoint(image, x ,y) && curLineStartX>0){
				if(curLineEndX>0 && ((double)badPointCount/(double)(curLineEndX-curLineStartX))>threshold_badpnt_proportion){
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
		for(int y=1; y<image.getHeight()/3; y=y+ajdust_step_length){
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
		for(int y=whiteMarginOutterTopY; y<image.getHeight()/3; y=y+ajdust_step_length){
			if(isWhiteHorizontalLine(image, y)){
				value = y;
			}else{
				break;
			}
		}
		return value;
	}
	

	private int whiteMarginOutterBottomY(BufferedImage image){
		for(int y=image.getHeight()-ajdust_step_length; y>=image.getHeight()/3*2; y=y-ajdust_step_length){
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
		for(int y=whiteMarginOutterBottomY; y>=image.getHeight()/3*2; y=y-ajdust_step_length){
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
		robotMngr.delay(delay_min);
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
		closeFoxit(foxitAppName);
		robotMngr.delay(delay_middle);
		robotMngr.moveMouseToRightDownCorner();
		File file = new File(pdfFilePath);
		if(!file.exists() && !file.isFile()){
			logger.warn("文件【"+pdfFilePath+"】不存在");
			return;
		}
		if(!pdfMngr.canCutPage(pdfFilePath)){	// 检查是否具备切白边条件
			return;	
		}
		openPdfByFoxit(foxitAppPath, pdfFilePath);
		preCutPages();
		PdfSize pdfSize = new PdfManager().pdfSize(pdfFilePath);
		int pageCount = new PdfManager().pdfPageCount(pdfFilePath);
		int endPageNum = pageCount;
		if(demoFlag){
			if(pageCount<=demo_pagecount){
				endPageNum = pageCount/4;
			}else{
				endPageNum = demo_pagecount;
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
		robotMngr.delay(delay_aftersavepdf);
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
			boolean newDirFlag, String newDirPath, boolean demo) throws AWTException, IOException{
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
					cutWhiteEdge(file.getAbsolutePath(), renameFlag, addstr, newDirFlag, newDirPath, demo);
					timer.countPdf();
					timer.countPages(new PdfManager().pdfPageCount(file.getAbsolutePath()));
					if(demo){
						break;
					}
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
		robotMngr.delay(delay_afteropenpdf);
		robotMngr.pressCombinationKey(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_S);
		robotMngr.delay(delay_afteropenpdf);
		robotMngr.pressCombinationKey(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
		robotMngr.delay(delay_afteropenpdf);
		robotMngr.pressCombinationKey(KeyEvent.VK_ALT, KeyEvent.VK_S);
		robotMngr.delay(delay_afteropenpdf);
		robotMngr.pressKey(KeyEvent.VK_Y);
		robotMngr.delay(delay_afteropenpdf);
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
		closeFoxit(foxitAppName);
		robotMngr.delay(delay_middle);
		openPdfByFoxit(foxitAppPath, pdfFilePath);
		preCutPages();
		jumpSpecPage(pageNum);
		PdfSize pdfSize = new PdfManager().pdfSize(pdfFilePath);
		cutCurrentPage(pdfSize.getPageWidthInch(pageNum), pdfSize.getPageHeightInch(pageNum),false);
	}
	
	public boolean isWhiteVerticalLine(String pdfFilePath, int pageNum, int x) throws AWTException{
		closeFoxit(foxitAppName);
		robotMngr.delay(delay_middle);
		openPdfByFoxit(foxitAppPath, pdfFilePath);
		preCutPages();
		jumpSpecPage(pageNum);
		startCutPage();
		BufferedImage image = screenCaptureCutPage();
		boolean result = isWhiteVerticalLine(image, x);
		logger.warn(result);
		return result;
	}
	
	public void sendMail(String content) throws UnsupportedEncodingException, MessagingException{
		String senderAccount = Prop.get("mail.sender.account");
		String senderPasswd = Prop.get("mail.sender.passwd");
		String recverAccounts = Prop.get("mail.recver.accounts");
		EmailManager.sendEmailFast(senderAccount, senderPasswd, recverAccounts, content);
	}
	
	public static void main(String[] args) throws IOException, AWTException, NativeHookException, MessagingException {		
		try{
			boolean demo= Boolean.parseBoolean(Prop.get("demo.flag"));
			String foxitAppPath = Prop.get("foxit.path");
			String foxitAppName = Prop.get("foxit.appname");
			boolean needEscKey = Boolean.parseBoolean(Prop.get("needesckey"));
			String srcDirPath = Prop.get("srcpath");
			String dstDirPath = Prop.get("newdir.dstpath");
			String apendName = Prop.get("rename.appendname");
			boolean rename = Boolean.parseBoolean(Prop.get("rename.flag"));
			boolean newdir = Boolean.parseBoolean(Prop.get("newdir.flag"));
			int scX = Integer.parseInt(Prop.get("foxit.coordinate.scx"));
			int scY = Integer.parseInt(Prop.get("foxit.coordinate.scy"));
			int scWidth = Integer.parseInt(Prop.get("foxit.coordinate.width"));
			int scHeight = Integer.parseInt(Prop.get("foxit.coordinate.height"));
			int adjuststeplength = Integer.parseInt(Prop.get("adjuststeplength"));
			if(args.length>0){
				demo = Boolean.parseBoolean(args[0]);
			}
			PdfEdgeCutter cutter = new PdfEdgeCutter(needEscKey, foxitAppPath, foxitAppName, scX, scY, scWidth, scHeight,adjuststeplength);
			cutter.cutWhiteEdgeBatch(srcDirPath, rename, apendName, newdir, dstDirPath, demo);
			cutter.sendMail("所有PDF切白边已完成！！！");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.exit(0);
		}		
	}
}
