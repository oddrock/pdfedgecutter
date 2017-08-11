package com.oddrock.common.media.test;

import java.io.File;

import java.io.FileInputStream;

import java.io.IOException;

import java.io.InputStream;

import sun.audio.AudioPlayer;

public class MusicTest2 {

	private InputStream inputStream = null;

	private String file = "./intel.wav";

	public MusicTest2() {

	}

	public void play() throws IOException {

		inputStream = new FileInputStream(new File(file));

		AudioPlayer.player.start(inputStream);

	}

	public static void main(String[] args) {

		try {

			new MusicTest2().play();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}