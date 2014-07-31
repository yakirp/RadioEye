package com.common;

public class FilesUtils {
	public static String getFileExtions(String fileName) {
		String filenameArray[] = fileName.split("\\.");
		String extension = filenameArray[filenameArray.length - 1];

		return "."+extension;
	}
}
