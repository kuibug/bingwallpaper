package top.zewenchen.bingWallpaper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import top.zewenchen.util.Utils;

public class Bing {
	final static String BING_SITE = "https://cn.bing.com";
	final static String PIXEL_1080 = "_1920x1080.jpg";
	final static String PIXEL_720 = "_1366x768.jpg";

	static String path;// 图片地址
	static String name;// 使用
	static String mkt;// 区域
	static String pixel;// 区域
	static int day;// 发布日期
	static int n = 1;// 批量获取
	static String cookie;// cookie

	public static void initial() {
		// 初始化的时候将文件中的变量读取进来
		JSONObject object = JSON.parseObject(Utils.readFileContent("config.json"));
		mkt = object.getString("mkt");
		path = object.getString("path");
		name = object.getString("name");
		pixel = object.getString("pixel");
		cookie = object.getString("cookie");
		System.out.println("读取配置文件中的参数\n\t区域：" + mkt + "；\n\t保存路径：" + path + "；\n\t命名方式：" + name + "；\n\t图片质量：" + pixel
				+ "；\n\tcookie：" + cookie);
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
		buffer.append("\",\n\"cookie\": \"");
		buffer.append(cookie);
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

		JSONObject info = BingCore.getInfo(day)[0];
		String fileName = null;
		if (name.toLowerCase().equals("link")) {
			fileName = info.getString("urlbase").substring(11);
		} else {
			fileName = info.getString("startdate");
		}
		return new Wallpaper(mkt, path, pixel, info.getString("urlbase"), fileName, info.getString("copyright"));
	}

	/**
	 * 批量获取的方法
	 * 
	 * @return Wallpaper[]
	 */
	static Wallpaper[] getWallpapers() {
		Wallpaper[] wallpapers = new Wallpaper[n];
		JSONObject[] info = BingCore.getInfo(day);
		for (int i = 0; i < n; i++) {
			String fileName = null;
			if (name.toLowerCase().equals("link")) {
				fileName = info[i].getString("urlbase").substring(11);
			} else {
				fileName = info[i].getString("startdate");
			}
			wallpapers[i] = new Wallpaper(mkt, path, pixel, info[i].getString("urlbase"), fileName,
					info[i].getString("copyright"));
		}
		return wallpapers;
	}

	public static void main(String[] args) {
		initial();
		// 处理传参
		hasArgs(args);

		// 批量获取（为了不重构，我算是心机用尽了……）
		if (n > 1) {
			Wallpaper[] wallpapers = getWallpapers();
			for (Wallpaper wallpaper : wallpapers) {
				System.out.println("还剩" + n + "张未下载");
				BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
				n--;
			}
			System.out.println("批量获取，下载完成！");
			// 批量获取完毕直接return结束程序
			return;
		}

		Wallpaper wallpaper = getWallpaper();
		BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
		System.out.println("下载结束，保存配置中");
		saveConfig();

	}

	/**
	 * 处理传参，普通参数直接修改无返回，错误参数会被丢弃 -n参数返回其状态
	 * 
	 * @param args
	 * @return 是否批量获取
	 */
	private static void hasArgs(String[] args) {
		int len = args.length;
		int index = 0;

		while (index < len) {
			System.out.println();

			switch (args[index]) {
			case "-day":
				day = Integer.parseInt(args[index + 1]);
				index += 2;
				break;

			case "-path":
				path = args[index + 1];
				index += 2;
				break;

			case "-pixle":
				path = args[index + 1];
				index += 2;
				break;

			case "-name":
				name = args[index + 1];
				index += 2;
				break;

			case "-mkt":
				mkt = args[index + 1];
				index += 2;
				break;

			case "-n":
				n = Integer.parseInt(args[index + 1]);
				index += 2;
				break;

			default:
				System.out.println("未知的参数,自动忽略该参数");
				index += 2;
				break;
			}
		}
	}
}
