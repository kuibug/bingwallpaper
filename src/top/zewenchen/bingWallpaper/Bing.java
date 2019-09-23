package top.zewenchen.bingWallpaper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Bing {
	// 保存地址(路径最后有一个/)
	static String path = "./Bingwallpaper/";
	// 要壁纸的上传时间-1(明天)0(今天)1(昨天)2(前天)最多回去到前7天的内容
	static int day = 0;

	public static void main(String[] args) throws Exception {
		// 先对传参进行判别
		hasArgs(args);

		// 获取下载地址
		String[] str = getURL();
		String urlString = str[0];
		// 获取文件名
		String wallpaperName = str[2];
		//System.out.println(wallpaperName);

		// 检查下载路径
		judeDirExists(path);

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
	private static String[] getURL() throws Exception {

		//String getURL = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=" + day + "&n=1&mkt=zh-CN";
		String getURL = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
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

		// 获取json中url和urlbase两个key值的位置
		int indexStart = json.indexOf("url");
		int indexEnd = json.indexOf("urlbase");

		// 获取部分下载链接
		String URL = json.substring(indexStart + 6, indexEnd - 3);
		// 拼接真正的下载地址
		URL = "https://cn.bing.com" + URL;

		// 2019年4月3日API接口变更，URL变更，改从描述中获取文件名copyright键值中@之前的内容，注意文件名有斜杠和反斜杠会GG
		indexStart = json.indexOf("copyright");
		indexEnd = json.indexOf("copyrightlink");
		String fileNameCN = json.substring(indexStart + 12, indexEnd - 3).replaceAll("\\/", "-") + ".jpg";

		// 或者直接截取"url"中id=OHR.后的内容，并且不用担心空格、斜杠、反斜杠和乱码的干扰。我选择前者的原因是因为包含了作者信息
		indexStart = json.indexOf("id=");
		indexEnd = json.indexOf("rf=");
		String fileNameEN = json.substring(indexStart + 7, indexEnd - 1);
		String arr[] = { URL, fileNameCN, fileNameEN };

		return arr;
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
