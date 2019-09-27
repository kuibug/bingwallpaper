package top.zewenchen.bingWallpaper;

import java.io.File;

public class Utils {
	/**
	 * 文件夹路径判断，若没有则新建
	 * @param file
	 */
	public static void judeDirExists(String file) {

		File filePath = new File(file);

		if (filePath.exists()) {
			if (filePath.isDirectory()) {
				System.out.println("下载路径正确！");
			}
		} else {
			System.out.println("下载路径不存在，将自动创建！");
			filePath.mkdir();
		}
	}
}
