package com.wy.confuse.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	public static String readFile(File file) {
		byte[] input = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			bufferedInputStream = new BufferedInputStream(
					new FileInputStream(file));
			input = new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				bufferedInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new String(input);
	}
	
	public static void writeFile(File file, String content, String charsetName) {
		byte[] input = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			bufferedOutputStream = new BufferedOutputStream(
					new FileOutputStream(file));
			bufferedOutputStream.write(content.getBytes(charsetName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				bufferedOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 
	 * @Title: getFileListBySuffixName 
	 * @Description: 根据文件后缀名获取文件列表
	 * @param dirPath
	 * @param suffixName
	 * @return
	 * @throws 
	 */
	public static List<File> getFileListBySuffixName(String dirPath, String suffixName) {
		List<File> list = new ArrayList<File>();
		File dir = new File(dirPath);
		addSuffixFile(list, dir, suffixName);
		return list;
	}
	
	private static void addSuffixFile(List<File> list, File dir, String suffixName) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					if (file.isDirectory()) {
						addSuffixFile(list, file, suffixName);
					}else {
						if (file.getName().endsWith(suffixName)) {
							list.add(file);
						}
					}
				}
			}
		}
	}
	
	/** 
	 * @Title: getClassFullName 
	 * @Description: 根据文件路径和源获取文件的类全名
	 * @param path
	 * @param sources
	 * @return
	 * @throws 
	 */
	public static String getClassFullName(String path, String[] sources) {
		// 得到要替换的类的全名
		String classFullName = path.replaceAll("\\\\", "/");
		for (String source : sources) {
			classFullName = classFullName.replaceFirst(source + "/", "");
		}
		classFullName = classFullName.replaceAll("/", ".").replaceAll(".java", "");
		return classFullName;
	}
	
}
