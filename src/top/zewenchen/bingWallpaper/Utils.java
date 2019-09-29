package top.zewenchen.bingWallpaper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
	/**
	 * 文件夹路径判断，若没有则新建
	 * 
	 * @param file
	 */
	public static void judeDir(String file) {

		File filePath = new File(file);

		if (filePath.exists()) {
			if (filePath.isDirectory()) {
				System.out.println("下载路径正确！");
			}
		} else {
			filePath.mkdirs();
			System.out.println(file + "下载路径不存在，将自动创建！");
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param fileName
	 * @return String
	 */
	public static String readFileContent(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer sbf = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempStr;
			while ((tempStr = reader.readLine()) != null) {
				sbf.append(tempStr);
			}
			reader.close();
			return sbf.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return sbf.toString();
	}

	/**
	 * 将字符串写入文件中
	 * 
	 * @param content
	 */
	static void writeFile(String content, String file) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
