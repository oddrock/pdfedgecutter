package com.oddrock.common.media.test;

import java.applet.AudioClip;
import java.io.*;
import java.applet.Applet;

import java.awt.Frame;

import java.net.MalformedURLException;

import java.net.URL;

public class Music extends Frame {

	public static String imagePath = System.getProperty("user.dir") + "/Music/";

	public Music() {

		try {

			URL cb;

			// File f = new File(imagePath+"mario.midi");

			// File f = new File(imagePath+"1000.ogg");

			File f = new File(imagePath + "失败音效.wav");

			// File f = new File("d:\\铃声.mp3");

			cb = f.toURL();

			AudioClip aau;

			aau = Applet.newAudioClip(cb);

			aau.play();// 循环播放 aau.play() 单曲 aau.stop()停止播放

			// aau.loop();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		}

	}

	public static void main(String args[]) {

		new Music();

	}

}
