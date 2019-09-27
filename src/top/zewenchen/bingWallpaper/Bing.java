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
import com.alibaba.fastjson.JSONObject;

public class Bing {
	final static String BING_SITE = "https://cn.bing.com";
	final static String PIC_1080 = "_1920x1080.jpg";
	final static String PIC_720 = "_1366x768.jpg";

	// 保存地址(路径最后有一个/)
	static String path = "./Bingwallpaper/";

	// 下载地址
	static URL url;

	// 文件名称
	static String wallpaperName;

	// 下载图片的信息是一个jsonArray，0是下载图片的相关信息，1上有使用范围
	static JSONObject picInfo;

	// 判断是否到达边界,是的话就不用再次获取信息了
	static boolean isSides = false;

	static int Pixel = 1080;

	public static void main(String[] args) throws Exception {
		// 先对传参进行判别
		if (args.length == 0) {
			defaltDown();
		} else {
			customDown(args);
		}

		// 检查下载地址
		System.out.println("下载地址：" + url);
		if (url == null)
			System.exit(0);

		// 检查下载路径
		Utils.judeDirExists(path);
		System.out.println("图片保存路径" + path);

		// 开始下载
		downloadPicture();

		System.out.println("下载完成，正在退出");
	}

	/**
	 * .自定义下载 .需要处理的参数， 1.day 天数 (-1~7)2.path 下载路径 3.pixle 图片分辨率(720/1080)4.name 命名方式(date/en)
	 * 
	 * @param args
	 */
	private static void customDown(String[] args) {
		System.out.println("自定义下载");
		// TODO Auto-generated method stub
		// TODO 使用日期作为文件名 或者 使用默认的英文名
		int len = args.length;
		for (int i = 0; i < len; i++) {
			switch (args[i]) {
			case "-day":
				picInfo = getInfo(Integer.parseInt(args[i + 1]));
				break;

			case "-path":
				path = args[i + 1];
				break;
				
			case "-pixel":
				if (args[i + 1].equals("720")) {
					url = getURL(PIC_720);
				} else {
					url = getURL(PIC_1080);
				}
				break;
				
			case "-name":
				break;

			}
		}
	}

	/**
	 * 默认的下载参数：day = 0（今天），下载分辨率1080P，下载路径 `./Bingwallpaper/`
	 */
	private static void defaltDown() {
		System.out.println("使用默认参数下载中……");
		picInfo = getInfo(0);
		url = getURL(PIC_1080);
		wallpaperName = picInfo.get("startdate") + PIC_1080;
	}

	private static URL getURL(String pixle) {
		URL url = null;

		String baseUrl = picInfo.get("urlbase").toString();
		String urlStr = BING_SITE + baseUrl + pixle;
		try {
			url = new URL(urlStr);
		} catch (Exception e) {
			System.err.println(e);
		}
		return url;
	}

	/**
	 * 通过微软API获取正确的下载地址 format 返回类型 js xml idx 图片时间 -1(明天)0(今天)1(昨天)2(前天)最多回去到前7天的内容
	 * 
	 * @return JSONObject
	 */
	private static JSONObject getInfo(int day) {
		// 行对参数进行判断是否合法,非法则返回上一次的值,默认开启会注入一个默认值,所以不会空
		if (day < -1 || day > 7) {
			isSides = true;
			System.out.println("参数超出范围（-1~7），为您显示最相近的一条");
			// return picInfo;
		}
		// 对上一次标记清除
		isSides = false;

		// https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1
		String requestURL = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=" + day + "&n=1";
		String json = null;
		URL requestUrl = null;

		try {

			// 初始化链接
			requestUrl = new URL(requestURL);
			HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
			connection.connect();

			// 使用BufferedReader获取url的json数据
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			json = reader.readLine();

			// 关闭资源
			reader.close();
			connection.disconnect();

		} catch (Exception e) {
			System.err.println("获取地址出现错误" + e);
		}

		Object object = JSON.parseObject(json).get("images");
		System.out.println("echo:getURL RETURN" + object);

		return JSON.parseArray(object.toString()).getJSONObject(0);
	}

	/**
	 * 下载图片
	 * 
	 */
	private static void downloadPicture() throws Exception {

//		try {

		DataInputStream dataInputStream = new DataInputStream(url.openStream());

		// 初始化文件
		String imageName = path + wallpaperName;
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

//		} catch (Exception e) {
//			System.err.println("下载中出现错误" + e);
//		}
	}

}
