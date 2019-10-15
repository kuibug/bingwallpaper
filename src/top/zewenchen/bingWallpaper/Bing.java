package top.zewenchen.bingWallpaper;

import java.util.concurrent.CountDownLatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import top.zewenchen.util.Utils;

public class Bing {
	
	static String path = "./BingWallpaper";// 图片地址
	static String name = "link";// 使用
	static String mkt = "cn";// 区域
	static String pixel = "720";// 区域
	static int n = 1;// 批量获取
	static String cookie;// cookie
	static int day = 0;// 发布日期
	static boolean saved = false;

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

	// 偷懒获取法
	protected static Wallpaper getWallpaper() {
		return getWallpaper(0);
	}

	/**
	 * 获取单张壁纸
	 * 
	 * @param day
	 * @return Wallpaper
	 */
	protected static Wallpaper getWallpaper(int day) {
		return getWallpapers(day, 1)[0];
	}

	/**
	 * 批量下载
	 * 
	 * @param day
	 * @param n   （n > 1）
	 */
	protected static Wallpaper[] getWallpapers(int day, int n) {
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

			case "-s":
				saved = true;
				break;

			case "-n":
				tmp = Integer.parseInt(args[index + 1]);
				if (tmp > 8) {
					n = 7;
					System.out.println("-n参数超范围！自动修正为 '7' ");
				} else if (tmp < 2) {
					n = 2;
					System.out.println("-n参数超范围！自动修正为 '2' ");
				} else {
					n = tmp;
				}
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

		// 批量获取判断
		if (n > 1) {
			// 开始下载
			Wallpaper[] wallpapers = getWallpapers(day, n);
			downloadPictures(wallpapers, n);
			return;
		}

		// 只获取一张
		Wallpaper wallpaper = getWallpaper(day);
		BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
		System.out.println("  pic_info:" + wallpaper.getCopyright());

		// 判断是是否需要参数
		if (saved) {
			saveConfig();
		}
	}

	/**
	 * 多线程下载壁纸
	 * 
	 * @param wallpapers
	 * @param size
	 */
	protected static void downloadPictures(Wallpaper[] wallpapers, int size) {
		CountDownLatch pool = new CountDownLatch(size);
		// 为每张壁纸建立一个线程下载
		for (Wallpaper wallpaper : wallpapers) {
			new Thread(() -> {
				System.out.println("线程建立");
				BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
				System.out.println("\tpic_info:" + wallpaper.getCopyright());
				System.out.println();
				pool.countDown();
			}).start();
		}

		try {
			//等待所有子线程结束
			pool.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("批量获取，下载完成！共" + size + "张");
	}
}
