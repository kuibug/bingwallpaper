package top.zewenchen.bingWallpaper;

import java.net.MalformedURLException;
import java.net.URL;

public class Wallpaper {
	private final static String BING_SITE = "https://cn.bing.com";
	private final static String PIXEL_1080 = "_1920x1080.jpg";
	private final static String PIXEL_720 = "_1366x768.jpg";

	private String path;// 图片地址
	private URL url;// 下载链接
	private String name;// 文件名
	private String copyright;// 版权信息

	/**
	 * 最基本的构造方法（使用默认参数，中国版，1080）
	 * 
	 * @param path
	 * @param url
	 * @param name
	 * @param copyright
	 */
	public Wallpaper(String path, String url, String name, String copyright) {
		this("cn", path, "720", url, name, copyright);
	}

	/**
	 * 最全参数的构造方法
	 * 
	 * @param mkt
	 * @param path
	 * @param pixel
	 * @param base_url
	 * @param name
	 * @param copyright
	 */
	public Wallpaper(String mkt, String path, String pixel, String base_url, String name, String copyright) {
		// 手动传参进来的
		this.path = path + "/" + mkt; // 国际版和中国版分开文件夹存放
		if (pixel.equals("720")) {
			pixel = PIXEL_720;
		} else {
			pixel = PIXEL_1080;
		}

		// 链接解析进来的
		try {
			this.url = new URL(BING_SITE + base_url + pixel);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.name = name + pixel; // （靠外部传参确定是使用的是那种命名方式）
		this.copyright = copyright;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public URL getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public String getCopyright() {
		return copyright;
	}

	@Override
	public String toString() {
		return "Wallpaper [path=" + path + ", url=" + url + ", name=" + name + ", copyright=" + copyright + "]";
	}

}