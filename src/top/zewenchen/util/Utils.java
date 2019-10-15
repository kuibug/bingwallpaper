package top.zewenchen.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
	public static void writeFile(String content, String file) {
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

	/**
	 * 调起浏览器访问指定页面
	 * @param url
	 */
	public static void browserUrl(String url) {
		// 判断是否支持Desktop扩展,如果支持则进行下一步
		if (Desktop.isDesktopSupported()) {
			try {
				URI uri = new URI(url);
				Desktop desktop = Desktop.getDesktop(); // 创建desktop对象
				// 调用默认浏览器打开指定URL
				desktop.browse(uri);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// 如果没有默认浏览器时，将引发下列异常
				e.printStackTrace();
			}

		}
	}
}
