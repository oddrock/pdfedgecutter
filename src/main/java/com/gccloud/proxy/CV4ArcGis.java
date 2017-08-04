package com.gccloud.proxy;
 
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage; 
import java.io.IOException; 
import java.util.logging.Level;
import java.util.logging.Logger; 
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

/**
 
 * CV4ArcGis是计算机视觉(Computer Vision)在电信行业gis方位角绘制领域的运用，
 * 本类没有使用Opencv、Javacv等开源框架，大大降低了使用门槛，你可以简单的调用它们，例如：
 * <p><blockquote><pre>
 *  	cv.moveTo(255, 165).mouseClick().mouseClick().pressNum("123.456").keyClick(KeyEvent.VK_TAB).moveTo(410, 165)
		 .mouseClick().mouseClick().pressNum("40.12").keyClick(KeyEvent.VK_ENTER).delay(100);
		cv.drawLine(60);
 * </pre></blockquote><p>
 * 上述代码的意思是，将鼠标移动到屏幕坐标255，165的位置，双击，敲击键盘输入123.456，敲击Tab，
 * 再将鼠标移动到屏幕坐标410，165的位置，双击，敲击键盘输入40.12，回车，暂停100毫秒，绘制一条60度的方位角（电信专业名词），
 *  
 * 一个完整调用的例子如下，按ESC是停止程序的唯一方法
 * <p><blockquote><pre>
 * public static void main(String[] args) throws Exception {
		Thread.sleep(5000); //别忘了给一个休眠的时间让你有时间可以切换软件
		
		CV4ArcGis cv = new CV4ArcGis(); 
		FileInputStream is = new FileInputStream("d:/test.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s = "";  
		while ((s = reader.readLine()) != null) {

			String x = s.split(",")[7];
			String y = s.split(",")[8];
			String angle = s.split(",")[6];
 
			 cv.moveTo(255, 165).mouseClick().mouseClick().pressNum(x).keyClick(KeyEvent.VK_TAB).moveTo(410, 165)
			 .mouseClick().mouseClick().pressNum(y).keyClick(KeyEvent.VK_ENTER).delay(100);
			 

			cv.drawLine(Integer.parseInt(angle));
 
		}
		reader.close();

	}
  
 *</pre></blockquote><p>
 * @author  Yafeng Shi
 * @since   JDK1.7
 */
public class CV4ArcGis {
	 /** 放大图标的X坐标 */
	public int zoomOutX=116;
	 /** 缩小图标的X坐标 */
	public int zoomInX=138;
	 /** 缩小、放大图标的Y坐标 */
	public int zoomY=89;
	 /** 缩放以后系统休眠时间，太小则有可能捕获到白屏 */
	public int zoomDelay=300;
	/** 画线单次移动的时间间隔，越短越快，也就是画线的速度 */
	public int drawSpeed=3;
	/** 为了画线，鼠标移动最大步数，防止一些异常情况鼠标移动出屏幕范围 */
	public int stepLen=1000; 
	/** 泰森多边形边线的颜色 */
	public int lineRGB=-9539986;
	 
	private int blueRGB=-16776961;
	/** 基站点的颜色值 */
	public int dotRGB=-5767168;
	/** 编辑区中间点的X坐标 */
	public int centerX=777;
	/** 编辑区中间点的Y坐标 */
	public int centerY=396; 
	 
	private Rectangle rect=new Rectangle(
			Toolkit.getDefaultToolkit().getScreenSize());
	private Robot robot;
	
 
	public CV4ArcGis() throws AWTException, NativeHookException{
 
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    	logger.setLevel(Level.OFF); 
    	logger.setUseParentHandlers(false); 
    	robot=new Robot(); 
		GlobalScreen.registerNativeHook();//初始化ESC钩子 
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
	}
	 
	public Robot getControl(){
		return robot;
	}
	 /**
     *  模拟鼠标单击
     *  
     */
	public CV4ArcGis mouseClick(){
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK); 
		return this;
	}
	
	 /**
     *  模拟键盘单击，参数k来自是java.awt.event.KeyEvent
     *  
     */
	public CV4ArcGis keyClick(int k){
		robot.keyPress(k);
		robot.keyRelease(k); 
		return this;
	}
	
	 /**
     *  将鼠标指针移动到某个指定的屏幕坐标
     *  
     */
	public CV4ArcGis moveTo(int x,int y){
		robot.mouseMove(x, y);
		return this;
	}
	
	 /**
     *  鼠标延时，避免移动太快导致无法测算结果，单位是毫秒
     *  
     */
	public CV4ArcGis delay(int  mills){
		robot.delay(mills);
		return this;
	}
	
	/**
	 * 模拟鼠标敲击键盘，用于输出数字和点号
	 * 
	 * */
	public    CV4ArcGis  pressNum(String num){
		char[] cs=num.toCharArray();
		for(int i=0;i<cs.length;i++){
			char c=cs[i]; 
			switch(c){
			
			case '.':
				robot.keyPress(KeyEvent.VK_PERIOD);
				robot.keyRelease(KeyEvent.VK_PERIOD);
				break;
			case '0':
				robot.keyPress(KeyEvent.VK_0);
				robot.keyRelease(KeyEvent.VK_0);
				break;
			case '1':
				robot.keyPress(KeyEvent.VK_1);
				robot.keyRelease(KeyEvent.VK_1);
				break;
			case '2':
				robot.keyPress(KeyEvent.VK_2);
				robot.keyRelease(KeyEvent.VK_2);
				break;
			case '3':
				robot.keyPress(KeyEvent.VK_3);
				robot.keyRelease(KeyEvent.VK_3);
				break;
			case '4':
				robot.keyPress(KeyEvent.VK_4);
				robot.keyRelease(KeyEvent.VK_4);
				break;
			case '5':
				robot.keyPress(KeyEvent.VK_5);
				robot.keyRelease(KeyEvent.VK_5);
				break;
			case '6':
				robot.keyPress(KeyEvent.VK_6);
				robot.keyRelease(KeyEvent.VK_6);
				break;
			case '7':
				robot.keyPress(KeyEvent.VK_7);
				robot.keyRelease(KeyEvent.VK_7);
				break;
			case '8':
				robot.keyPress(KeyEvent.VK_8);
				robot.keyRelease(KeyEvent.VK_8);
				break;
			case '9':
				robot.keyPress(KeyEvent.VK_9);
				robot.keyRelease(KeyEvent.VK_9);
				break;
			}
		 
		  robot.delay(100);
		}
		
		return this;
	}
	 
	 /**
     *  计算最近的交点，作为是否缩放的条件
     *  
     */ 
	private  int minIntersectDistance(BufferedImage image)  { 
	   
 		int width = image.getWidth();
		int height = image.getHeight();
		int distance=width;
		for (int x = 200; x < width - 10; ++x) {
			for (int y = 100; y < height - 10; ++y) { 
				boolean line = false,rLine=false;
			    for (int m = -1; m <1; m++)
					for (int n = -1; n <1; n++) {
						 if(image.getRGB(x + n, y + m)==lineRGB) line=true; 
						 if(image.getRGB(x + n, y + m)==blueRGB) rLine=true; 
					} 
			    if(line&&rLine){
					 int length=(int)Math.sqrt((centerX-x)*(centerX-x)+(centerY-y)*(centerY-y));
			         if(length<distance)  distance=length; 
			    }
			}
			

		}
		
		return distance;
		
	}
	
	 /**
     *  在内存中预先画线，作为是否缩放的判定条件
     *  
     */ 
 
	private   BufferedImage memLine(int angle,double k,double b){ 
		BufferedImage image=robot.createScreenCapture(rect); 
		Graphics2D d2=(Graphics2D)image.getGraphics();
		d2.setColor(Color.BLUE);
		int x2=0,y2=0;
		 if(angle>=45&&angle<=135){
	      	 x2=image.getWidth();
	      	 y2=(int)(k*x2+b); 
	      	  }
	      	  if(angle>=0&&angle<45||angle<=360&&angle>=315){
	            x2=(int)(-b/k); 
	            }
	      	  if(angle>=225&&angle<315){
	             y2=(int)b;
	            }
	      	  if(angle<225&&angle>135){
	              y2=image.getHeight();
	              x2=(int)((y2-b)/k);
	            }
	      	 d2.drawLine(centerX, centerY, x2, y2);
	      	 return image;
	}
	
 
	 /**
     *  根据某个方位角(0-360)绘制线
     *  
     */ 
	public   CV4ArcGis drawLine(int angle) throws IOException{
	 
		double dx=centerX,dy=centerY;
	    double k= Math.tan(Math.toRadians(90+angle)); 
        double b=centerY-k*centerX; 
       
        BufferedImage image= memLine(angle,k,b); 
	      	 int distance=minIntersectDistance(image);
	       
	      	 while(distance<35){
	      		robot.mouseMove(zoomOutX, zoomY);
	      		robot.mousePress(InputEvent.BUTTON1_MASK);
	    		robot.mouseRelease(InputEvent.BUTTON1_MASK); 
	       		robot.delay(zoomDelay);   
	       		image= memLine(angle,k,b);
	       		distance=minIntersectDistance(image);    
	      	 }
	      	 while(distance>=image.getWidth()){
	      		robot.mouseMove(zoomInX, zoomY);
	      		robot.mousePress(InputEvent.BUTTON1_MASK);
	    		robot.mouseRelease(InputEvent.BUTTON1_MASK); 
		        robot.delay(zoomDelay); 
		       	image= memLine(angle,k,b);
		       	distance=minIntersectDistance(image);        	 
	      	 }
	    
	    robot.mouseMove(centerX, centerY); 
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		
        for(int i=0;i<stepLen;i++){
      	  if(angle>=45&&angle<=135){
      	  dy=Math.round(k*dx+b);
      	  dx++;
      	  }
      	  if(angle>=0&&angle<45||angle<=360&&angle>=315){
            dx=(dy-b)/k;
            dy--;
            }
      	  if(angle>=225&&angle<315){
            dy=Math.round(k*dx+b);
            dx--;
            }
      	  if(angle<225&&angle>135){
            dx=(dy-b)/k;
            dy++;
            }
      	  
      	  int curX=(int)dx;
      	  int curY=(int)dy; 
      	  robot.mouseMove(curX, curY);
      	  robot.delay(drawSpeed);
       	  boolean s=false;
      	  lineOut:for(int j=curX-5;j<curX+5;j++){
      		  for(int z=curY-5;z<curY+5;z++){
	        		  if(i>10&&image.getRGB(j, z)==lineRGB){
	        	          s=true; 		  
	        			  break lineOut;
	        		  }
	        	  }
      	  } 
      	  
       	if(s||i==stepLen-1){
        	robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.keyPress(KeyEvent.VK_F2);
			robot.keyRelease(KeyEvent.VK_F2); 
			 
      		break;
      	} 
      	  
      	   
 }
		return this;
	}

	 
}
