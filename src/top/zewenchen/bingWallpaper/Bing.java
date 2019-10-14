package top.zewenchen.bingWallpaper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import top.zewenchen.util.Utils;

public class Bing {
	final static String BING_SITE = "https://cn.bing.com";
	final static String PIXEL_1080 = "_1920x1080.jpg";
	final static String PIXEL_720 = "_1366x768.jpg";

	static String path = "./BingWallpaper";// 图片地址
	static String name = "link";// 使用
	static String mkt = "cn";// 区域
	static String pixel = "720";// 区域
	static int n = 1;// 批量获取
	static String cookie;// cookie
	static int day = 0;// 发布日期

	public static void initial() {
		try {
			// 初始化的时候将文件中的变量读取进来
			JSONObject object = JSON.parseObject(Utils.readFileContent("config.json"));
			if (object.getString("mkt") != null)
				mkt = object.getString("mkt");
			if (object.getString("path") != null)
				path = object.getString("path");
			if (object.getString("name") != null)
				name = object.getString("name");
			if (object.getString("pixel") != null)
				pixel = object.getString("pixel");
			if (object.getString("cookie") != null)
				cookie = object.getString("cookie");
			if (object.getString("up_key") != null)
				GUI.UP_KEY = object.getInteger("up_key");
			if (object.getString("down_key") != null)
				GUI.DOWD_KEY = object.getInteger("down_key");
			System.out.println("读取配置文件中的参数\n\t区域：" + mkt + "；\n\t保存路径：" + path + "；\n\t命名方式：" + name + "；\n\t图片质量："
					+ pixel + "；\n\tcookie：" + cookie + "；\n\tup_key：" + GUI.UP_KEY + "；\n\tdown_key：" + GUI.DOWD_KEY);
		} catch (Exception e) {
			System.out.println("config.json文件不存在或配置错误，将使用默认配置");
		}
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
		buffer.append("\",\n\"up_key\": \"");
		buffer.append(GUI.UP_KEY);
		buffer.append("\",\n\"down_key\": \"");
		buffer.append(GUI.DOWD_KEY);
		buffer.append("\"\n}");

		String str = buffer.toString();
		System.out.println("正在保存配置\n" + str);
		Utils.writeFile(str, "config.json");
	}

	// 默认偷懒
	static Wallpaper getWallpaper() {
		return getWallpaper(0);
	}

	// 偷懒获取
	static Wallpaper getWallpaper(int day) {

		JSONObject info = BingCore.getInfo(day, 1)[0];
		String fileName = null;
		if (name.toLowerCase().equals("link")) {
			fileName = info.getString("urlbase").substring(11);
		} else {
			fileName = info.getString("startdate");
		}
		return new Wallpaper(mkt, path, pixel, info.getString("urlbase"), fileName, info.get("copyright").toString());
	}

	/**
	 * 批量获取
	 * 
	 * @return Wallpaper[]
	 */
	static Wallpaper[] getWallpapers(int day, int n) {
		System.out.println(day + n);
		System.out.println("总共" + n + "张壁纸，正在获取下载链接，请稍等！");
		Wallpaper[] wallpapers = new Wallpaper[n];
		// 获取信息
		JSONObject[] info = BingCore.getInfo(day, n);
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

		// 开始下载
		for (Wallpaper wallpaper : wallpapers) {
			System.out.println("还剩" + n + "张未下载");
			BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
			n--;
		}
		System.out.println("批量获取，下载完成！");
		return wallpapers;
	}

	/**
	 * 处理传参，普通参数直接修改无返回，错误参数会被丢弃
	 * 
	 * @param args
	 * @return 是否批量获取
	 */
	private static void hasArgs(String[] args) {

		int len = args.length;
		int index = 0;
		int tmp;

		while (index < len) {
			System.out.println();

			switch (args[index]) {
			case "-day":
				tmp = Integer.parseInt(args[index + 1]);
				if (tmp > 0)
					day = tmp;
				break;

			case "-path":
				path = args[index + 1];
				break;

			case "-pixle":
				path = args[index + 1];
				break;

			case "-name":
				name = args[index + 1];
				break;

			case "-mkt":
				mkt = args[index + 1];
				break;

			case "-n":
				tmp = Integer.parseInt(args[index + 1]);
				if (tmp > 1)
					n = tmp;
				break;

			default:
				System.out.println("未知的参数,自动忽略该参数");
				break;
			}
			index += 2;
		}
	}

	/**
	 * 从命令行启动的入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		initial();
		// 处理传参
		hasArgs(args);

		// 批量获取（为了不重构，我算是心机用尽了……）
		if (n > 1) {
			getWallpapers(day, n);
			return;
		}

		Wallpaper wallpaper = getWallpaper();
		BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
		System.out.println("下载结束，保存配置中");
		saveConfig();
	}
}
