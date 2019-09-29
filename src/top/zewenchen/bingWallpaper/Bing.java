package top.zewenchen.bingWallpaper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Bing {
	final static String BING_SITE = "https://cn.bing.com";
	final static String PIXEL_1080 = "_1920x1080.jpg";
	final static String PIXEL_720 = "_1366x768.jpg";

	static String path;// 图片地址
	static String name;// 使用
	static String mkt;// 区域
	static String pixel;// 区域

	public static void initial() {
		// 初始化的时候将文件中的变量读取进来
		JSONObject object = JSON.parseObject(Utils.readFileContent("config.json"));
		mkt = object.getString("mkt");
		path = object.getString("path");
		name = object.getString("name");
		pixel = object.getString("pixel");
		System.out
				.println("读取配置文件中的参数\n\t区域：" + mkt + "；\n\t保存路径：" + path + "；\n\t命名方式：" + name + "；\n\t图片质量：" + pixel);
	}

	/**
	 * 将当前配置读入到配置文件中
	 * 
	 * @return
	 */
	static void saveConfig() {
		StringBuffer buffer = new StringBuffer(127);
		buffer.append("{\n\"mkt\": \"");
		buffer.append(mkt);
		buffer.append("\",\n\"path\": \"");
		buffer.append(path);
		buffer.append("\",\n\"name\": \"");
		buffer.append(name);
		buffer.append("\",\n\"pixel\": \"");
		buffer.append(pixel);
		buffer.append("\"\n}");

		String str = buffer.toString();
		System.out.println("正在保存配置\n" + str);
		Utils.writeFile(str, "config.json");
	}

	static Wallpaper getWallpaper() {
		return getWallpaper(0);
	}

	static Wallpaper getWallpaper(int day) {
		return getWallpaper(day, mkt, pixel, name);
	}

	// 这里可以临时传参而不影响全局配置
	static Wallpaper getWallpaper(int day, String mkt, String pixel, String name) {
		JSONObject info = BingCore.getInfo(day);
		String fileName = null;
		if (name.toLowerCase().equals("link")) {
			fileName = info.getString("urlbase").substring(11);
		} else {
			fileName = info.getString("startdate");
		}
		return new Wallpaper(mkt, path, pixel, info.getString("urlbase"), fileName, info.getString("copyright"));
	}

	public static void main(String[] args) {
		initial();
		Wallpaper wallpaper = getWallpaper();
		BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
		System.out.println("下载结束，保存配置中");
		saveConfig();
	}
}
