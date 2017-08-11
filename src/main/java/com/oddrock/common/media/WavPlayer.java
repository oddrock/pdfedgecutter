package com.oddrock.common.media;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

/**
 * wav格式文件播放器
 * @author oddrock
 *
 */
public class WavPlayer {
	private static Logger logger = Logger.getLogger(WavPlayer.class);
	public static void play(String wavFilePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		File wavFile = new File(wavFilePath);
		if(!wavFile.exists() || !wavFile.isFile()){
			logger.warn("声音文件不存在："+wavFilePath);
			return ;
		}
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
		AudioFormat audioFormat = audioInputStream.getFormat();
		DataLine.Info dataLine_info = new DataLine.Info(SourceDataLine.class, audioFormat);
		SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLine_info);
		byte[] b = new byte[1024];
		int len = 0;
		sourceDataLine.open(audioFormat, 1024);
		sourceDataLine.start();
		while ((len = audioInputStream.read(b)) > 0) {
			sourceDataLine.write(b, 0, len);
		}
		audioInputStream.close();
		sourceDataLine.drain();
		sourceDataLine.close();
	}
	
	public static void play(String wavFilePath, int playCount) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		for(int i=0; i<playCount; i++){
			play(wavFilePath);
		}
	}

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		String filePath = "C:\\_Temp\\Scratch 2.0动画游戏与创意设计教程\\范例文件\\声音库\\欢迎光临_wav.wav";
		WavPlayer.play(filePath,3);
	}

}