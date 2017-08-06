package com.oddrock.common.awt;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

public class RobotManager {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(RobotManager.class);
	private Robot robot;

	public RobotManager() throws AWTException {
		robot = new Robot();
	}

	/**
	 * 模拟键盘单击
	 * 
	 * @param robot
	 * @param keycode
	 */
	public RobotManager pressKey(int keycode) {
		robot.keyPress(keycode);
		robot.keyRelease(keycode);
		return this;
	}

	/**
	 * 模拟键盘同时单击多个键
	 * 
	 * @param robot
	 * @param keycodeArray
	 */
	public RobotManager pressCombinationKey(int... keycodeArray) {
		for (int i : keycodeArray) {
			robot.keyPress(i);
		}
		for (int i : keycodeArray) {
			robot.keyRelease(i);
		}
		return this;
	}

	/**
	 * 单击鼠标左键
	 * 
	 * @return
	 */
	public RobotManager clickMouseLeft() {
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		return this;
	}
	
	/**
	 * 点击Enter键
	 * @return
	 */
	public RobotManager pressEnter() {
		return pressKey(KeyEvent.VK_ENTER);
	}
	
	/**
	 * 点击右箭头
	 * @return
	 */
	public RobotManager pressRight() {
		return pressKey(KeyEvent.VK_RIGHT);
	}
	
	/**
	 * 点击Tab键
	 * @return
	 */
	public RobotManager pressTab() {
		return pressKey(KeyEvent.VK_TAB);
	}
	
	/**
	 * 点击Up键
	 * @return
	 */
	public RobotManager pressUp() {
		return pressKey(KeyEvent.VK_UP);
	}
	
	public BufferedImage createScreenCapture(Rectangle screenRect){
		return robot.createScreenCapture(screenRect);
	}
	
	/**
	 * 点击Down键
	 * @return
	 */
	public RobotManager pressDown() {
		return pressKey(KeyEvent.VK_DOWN);
	}

	/**
	 * 将鼠标指针移动到某个指定的屏幕坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public RobotManager moveMouseTo(int x, int y) {
		robot.mouseMove(x, y);
		return this;
	}

	/**
	 * 延迟
	 */
	public RobotManager delay(int mills) {
		robot.delay(mills);
		return this;
	}

	/**
	 * 模拟鼠标敲击键盘的数字、字母和英文句号
	 * 
	 * */
	public RobotManager pressContinuousKey(String key) {
		key = key.toLowerCase();
		char[] cs = key.toCharArray();
		int keycode = -1;
		for (int i = 0; i < cs.length; i++) {
			char c = cs[i];
			switch (c) {
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
			case 'a':
				robot.keyPress(KeyEvent.VK_A);
				robot.keyRelease(KeyEvent.VK_A);
				break;
			case 'b':
				robot.keyPress(KeyEvent.VK_B);
				robot.keyRelease(KeyEvent.VK_B);
				break;
			case 'c':
				robot.keyPress(KeyEvent.VK_C);
				robot.keyRelease(KeyEvent.VK_C);
				break;
			case 'd':
				robot.keyPress(KeyEvent.VK_D);
				robot.keyRelease(KeyEvent.VK_D);
				break;
			case 'e':
				robot.keyPress(KeyEvent.VK_E);
				robot.keyRelease(KeyEvent.VK_E);
				break;
			case 'f':
				robot.keyPress(KeyEvent.VK_F);
				robot.keyRelease(KeyEvent.VK_F);
				break;
			case 'g':
				robot.keyPress(KeyEvent.VK_G);
				robot.keyRelease(KeyEvent.VK_G);
				break;
			case 'h':
				robot.keyPress(KeyEvent.VK_H);
				robot.keyRelease(KeyEvent.VK_H);
				break;
			case 'i':
				robot.keyPress(KeyEvent.VK_I);
				robot.keyRelease(KeyEvent.VK_I);
				break;
			case 'j':
				robot.keyPress(KeyEvent.VK_J);
				robot.keyRelease(KeyEvent.VK_J);
				break;
			case 'k':
				keycode = KeyEvent.VK_K;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'l':
				keycode = KeyEvent.VK_L;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'm':
				keycode = KeyEvent.VK_M;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'n':
				keycode = KeyEvent.VK_N;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'o':
				keycode = KeyEvent.VK_O;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'p':
				keycode = KeyEvent.VK_P;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'q':
				keycode = KeyEvent.VK_Q;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'r':
				keycode = KeyEvent.VK_R;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 's':
				keycode = KeyEvent.VK_S;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 't':
				keycode = KeyEvent.VK_T;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'u':
				keycode = KeyEvent.VK_U;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'v':
				keycode = KeyEvent.VK_V;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'w':
				keycode = KeyEvent.VK_W;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'x':
				keycode = KeyEvent.VK_X;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'y':
				keycode = KeyEvent.VK_Y;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			case 'z':
				keycode = KeyEvent.VK_Z;
				robot.keyPress(keycode);
				robot.keyRelease(keycode);
				break;
			}

			robot.delay(100);
		}

		return this;
	}
}
