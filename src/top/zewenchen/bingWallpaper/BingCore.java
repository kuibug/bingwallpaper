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

public class BingCore {
	final static String BING_SITE = "https://cn.bing.com";
	final static String PIC_1080 = "_1920x1080.jpg";
	final static String PIC_720 = "_1366x768.jpg";

	static String MKT = "zh-CN";

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

	static URL getURL(String pixle) {
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
	static JSONObject getInfo(int day) {
		// 行对参数进行判断是否合法,非法则返回上一次的值,默认开启会注入一个默认值,所以不会空
		if (day < -1 || day > 7) {
			isSides = true;
			System.out.println("参数超出范围（-1~7），为您显示最相近的一条");
			// return picInfo;
		}

		// 对上一次标记清除
		isSides = false;

		// 国际版临时支持
		if (Bing.mkt.toLowerCase().equals("us")) {
			MKT = "en-US";
		}

		// en-US
		// zh-CN
		// https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN
		// idx:天数； n ：数量； mkt ：区域
		String requestURL = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=" + day + "&n=1&mkt=" + MKT;
		String json = null;
		URL requestUrl = null;

		try {
			//setProperty转移至失败等会改用httpClient 试试
			String Chrome = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";
			//System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");  
			System.setProperty("http.agent", Chrome);
			// 初始化链接
			requestUrl = new URL(requestURL);
			HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
			//connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
			//connection.setRequestProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
			//user-agent 
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
		picInfo = JSON.parseArray(object.toString()).getJSONObject(0);

		return JSON.parseArray(object.toString()).getJSONObject(0);
	}

	/**
	 * 下载图片
	 * 
	 */
	static void downloadPicture(URL url, String path, String wallpaperName) {

		try {
			DataInputStream dataInputStream = new DataInputStream(url.openStream());
			System.out.println(url + path + wallpaperName);
			Utils.judeDir(path);
			// 初始化文件
			String imageName = path + "/" + wallpaperName;
			File file = new File(imageName);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("下载进程结束");
		}
	}

}
