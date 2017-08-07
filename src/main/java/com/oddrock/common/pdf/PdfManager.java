package com.oddrock.common.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.oddrock.common.file.FileUtils;

public class PdfManager {
	private static Logger logger = Logger.getLogger(PdfManager.class);

	public PdfManager() {}

	/**
	 * 获得PDF总页数
	 * @param pdfFilePath
	 * @return
	 * @throws IOException
	 */
	public int pdfPageCount(String pdfFilePath) throws IOException {
		PdfReader reader = new PdfReader(pdfFilePath);
		int pageCount = reader.getNumberOfPages();
		reader.close();
		return pageCount;
	}
	
	public PdfSize pdfSize(String pdfFilePath) throws IOException{
		PdfReader reader = new PdfReader(pdfFilePath);
		int pageCount = reader.getNumberOfPages();
		PdfSize pdfSize = new PdfSize();
		Rectangle rec;
		for(int i=1; i<=pageCount; i++){
			rec = reader.getPageSize(i);
			pdfSize.addPageSize(i,rec.getWidth(), rec.getHeight());
		}
		return pdfSize;
	}
	
	public void showPdfSize(String pdfFilePath) throws IOException{
		PdfSize pdfSize =  pdfSize(pdfFilePath);
		Integer pageNum;
		java.text.DecimalFormat   df   =  new   java.text.DecimalFormat("#.00");  
		for(Object o : pdfSize.getMap().keySet()){
			pageNum = (Integer)o;
			System.out.println(String.valueOf(pageNum) 
					+ ":" + df.format(pdfSize.getPageWidthInch(pageNum))
					+ ":" + df.format(pdfSize.getPageHeightInch(pageNum)));
		}
	}
	
	/**
	 * 是否加密
	 * @param pdfFilePath
	 * @return
	 * @throws IOException
	 */
	public boolean isEncrypted(String pdfFilePath) throws IOException{
		PdfReader pr = new PdfReader(pdfFilePath);
		return pr.isEncrypted();
	}
	
	/**
	 * 是否可以转换
	 * @param pdfFilePath
	 * @return
	 * @throws IOException
	 */
	public boolean canCutPage(String pdfFilePath) throws IOException{
		if(isEncrypted(pdfFilePath)){
			logger.warn("文件【"+pdfFilePath+"】已加密，无法转换");
			return false;
		}
		return true;
	}
	
	/**
	 * 提取PDF中的图片
	 * @param pdfFilePath
	 * @param picDirPath
	 * @param picNamePrefix
	 */
	@SuppressWarnings("rawtypes")
	public void extractImage(String pdfFilePath, String picDirPath, String picNamePrefix){  
		File file = new File(pdfFilePath);
		if(!file.exists() || !file.isFile()){
			return;
		}
        if(picDirPath==null || picDirPath.trim().length()==0){
        	picDirPath = FileUtils.getDirPathFromFilePath(pdfFilePath);
        }
        if(picNamePrefix==null || picNamePrefix.trim().length()==0){
        	picNamePrefix = FileUtils.getFileNameWithoutSuffixFromFilePath(pdfFilePath);
        }
        FileUtils.mkdirIfNotExists(picDirPath);
        PdfReader reader = null;  
        try {  
            //读取pdf文件  
            reader = new PdfReader(pdfFilePath);  
            //获得pdf文件的页数  
            int sumPage = reader.getNumberOfPages();      
            //读取pdf文件中的每一页  
            for(int i = 1;i <= sumPage;i++){  
                //得到pdf每一页的字典对象  
                PdfDictionary dictionary = reader.getPageN(i);  
                //通过RESOURCES得到对应的字典对象  
                PdfDictionary res = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.RESOURCES));  
                //得到XOBJECT图片对象  
                PdfDictionary xobj = (PdfDictionary) PdfReader.getPdfObject(res.get(PdfName.XOBJECT));  
                if(xobj != null){  
                    for(Iterator it = xobj.getKeys().iterator();it.hasNext();){  
                        PdfObject obj = xobj.get((PdfName)it.next());             
                        if(obj.isIndirect()){  
                            PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);                   
                            PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));  
                            if(PdfName.IMAGE.equals(type)){       
                                PdfObject object =  PdfReader.getPdfObject(obj);  
                                if(object.isStream()){                        
                                    PRStream prstream = (PRStream)object;  
                                    byte[] b;  
                                    try{  
                                        b = PdfReader.getStreamBytes(prstream);  
                                    }catch(UnsupportedPdfException e){  
                                        b = PdfReader.getStreamBytesRaw(prstream);  
                                    }  
                                    FileOutputStream output = new FileOutputStream(String.format(picDirPath+"\\"+picNamePrefix+"%d.jpg",i));  
                                    output.write(b);  
                                    output.flush();  
                                    output.close();                               
                                }  
                            }  
                        }  
                    }  
                }  
            }  
              
        } catch (IOException e) {  
            e.printStackTrace();  
        }
	}
	
	public static void main(String[] args) throws IOException{
		String pdfFilePath = "C:\\Users\\oddro\\Desktop\\pdf测试\\Thinking In Java（中文版 第四版）.pdf";
		PdfReader pr = new PdfReader(pdfFilePath);
		System.out.println(pr.isEncrypted());
		System.out.println(pr.is128Key());
		System.out.println(pr.isAppendable());
		System.out.println(pr.isTampered());
		System.out.println(pr.isTagged());
		pdfFilePath = "C:\\Users\\oddro\\Desktop\\pdf测试\\《YES！产品经理》.pdf";
		pr = new PdfReader(pdfFilePath);
		System.out.println(pr.isEncrypted());
		System.out.println(pr.is128Key());
		System.out.println(pr.isAppendable());
		System.out.println(pr.isTampered());
		System.out.println(pr.isTagged());
		new PdfManager().extractImage(pdfFilePath, "C:\\Users\\oddro\\Desktop\\片", null);
	}
}
