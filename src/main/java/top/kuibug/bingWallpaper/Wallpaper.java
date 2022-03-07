package top.kuibug.bingWallpaper;

import top.kuibug.util.LogUtil;

import java.net.MalformedURLException;
import java.net.URL;

public class Wallpaper {
    private final static String BING_SITE = "https://cn.bing.com";
    private final static String PIXEL_1080 = "_1920x1080.jpg";
    private final static String PIXEL_720 = "_1366x768.jpg";
    private final static String PIXEL_UHD = "_UHD.jpg";
    /** 文件名 */
    private final String fileName;
    /** 版权信息 */
    private final String copyright;
    /** 图片地址 */
    private String path;
    /** 下载链接 */
    private URL url;

    /**
     * 最基本的构造方法（使用默认参数，中国版，1080）
     *
     * @param path
     * @param url
     * @param fileName
     * @param copyright
     */
    public Wallpaper(String path, String url, String fileName, String copyright) {
        this("cn", path, "1080", url, fileName, copyright);
    }

    /**
     * 最全参数的构造方法
     *
     * @param mkt
     * @param path
     * @param pixel
     * @param baseUrl
     * @param fileName
     * @param copyright
     */
    public Wallpaper(String mkt, String path, String pixel, String baseUrl, String fileName, String copyright) {

        // 国际版和中国版分开文件夹存放
        this.path = path + "/" + mkt;
        // 分辨率
        switch (pixel) {
            case "720":
                pixel = PIXEL_720;
                break;
            case "1080":
                pixel = PIXEL_1080;
                break;
            case "UHD":
                pixel = PIXEL_UHD;
                break;
            default:
                if (pixel.indexOf('x') < 0) {
                    LogUtil.error("分辨率格式错误，长宽之间使用小写字母x分隔：" + pixel);
                } else {
                    pixel = '_' + pixel + ".jpg";
                }
                break;
        }


        // 链接解析进来的
        try {
            this.url = new URL(BING_SITE + baseUrl + pixel);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.fileName = fileName + pixel;
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

    public String getFileName() {
        return fileName;
    }

    public String getCopyright() {
        return copyright;
    }

    @Override
    public String toString() {
        return "Wallpaper [path=" + path + ", url=" + url + ", name=" + fileName + ", copyright=" + copyright + "]";
    }

}