package top.zewenchen.bingWallpaper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;

public class Bing {
	// 保存地址(路径最后有一个/)
	static String path = "./Bingwallpaper/";
	// 要壁纸的上传时间-1(明天)0(今天)1(昨天)2(前天)最多回去到前7天的内容
	static int day = 0;
	static String urlString;
	static String wallpaperName;
	final static String bingSite = "https://cn.bing.com";
	final static String pic_1080 = "_1920x1080.jpg";
	final static String pic_720 = "_1366x768.jpg";

	public static void main(String[] args) throws Exception {
		// 先对传参进行判别
		hasArgs(args);

		// 获取下载地址
		String str = getURL();
		urlString = bingSite + (String) JSON.parseArray(str).getJSONObject(0).get("urlbase") + pic_1080;
		// 获取文件名
		wallpaperName = (String) JSON.parseArray(str).getJSONObject(0).get("startdate") + pic_1080;
		// System.out.println(wallpaperName);

		// 检查下载路径
		Utils.judeDirExists(path);

		// "https://cn.bing.com/az/hprichbg/rb/DivingEmperors_ZH-CN8118506169_1920x1080.jpg";
		downloadPicture(urlString, wallpaperName);
		System.out.println("下载完成，文件保存在：" + path + wallpaperName);
	}

	/**
	 * 对传参运行进行判别
	 * 
	 * @param args
	 */
	private static void hasArgs(String[] args) {
		// 只传一个参数,默认修改天数
		if (args.length == 1) {
			int tem = Integer.parseInt(args[0]);
			if (tem < 8 && tem > -2)
				day = tem;
		}
		if (args.length == 2) {
			int i = Integer.parseInt(args[0]);
			if (i < 8 && i > -2)
				day = Integer.parseInt(args[0]);
		}
	}

	/**
	 * 通过微软API获取正确的下载地址 format 返回类型 js xml idx 图片时间 -1(明天)0(今天)1(昨天)2(前天)最多回去到前7天的内容
	 * 
	 * @return String[] { URL, fileNameCN, fileNameEN }
	 * @throws Exception
	 */
	private static String getURL() throws Exception {

		// https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1
		String getURL = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=" + day + "&n=1";

		// 初始化链接
		URL getUrl = new URL(getURL);
		HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
		connection.connect();

		// 使用BufferedReader获取url的json数据
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String json = reader.readLine();

		// 关闭资源
		reader.close();
		connection.disconnect();

		System.out.println(json);

		Object object = JSON.parseObject(json).get("images");
		System.out.println("echo:getURL RETURN" + object);
		// JSON.parseArray(object);

		return object.toString();
	}

	/**
	 * 下载图片
	 * 
	 * @param urlList 下载地址
	 * @param name    下载的文件名
	 * @throws Exception
	 */
	private static void downloadPicture(String urlList, String name) throws Exception {
		URL url = null;

		// 初始化链接
		url = new URL(urlList);
		DataInputStream dataInputStream = new DataInputStream(url.openStream());

		// 初始化文件
		String imageName = path + name;
		FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int length;

		// 开始写入文件
		while ((length = dataInputStream.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		fileOutputStream.write(output.toByteArray());

		// 关闭资源
		dataInputStream.close();
		fileOutputStream.close();

	}

}
