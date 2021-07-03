package top.kuibug.bingWallpaper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import top.kuibug.util.LogUtil;
import top.kuibug.util.Utils;

public class Core {

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
            JSONObject config = JSON.parseObject(Utils.readFileContent("config.json"));
            if (config.getString("mkt") != null)
                mkt = config.getString("mkt");
            if (config.getString("path") != null)
                path = config.getString("path");
            if (config.getString("name") != null)
                name = config.getString("name");
            if (config.getString("pixel") != null)
                pixel = config.getString("pixel");
            if (config.getString("cookie") != null)
                cookie = config.getString("cookie");
            if (config.getString("up_key") != null)
                GUI.UP_KEY = config.getInteger("up_key");
            if (config.getString("down_key") != null)
                GUI.DOWD_KEY = config.getInteger("down_key");
            System.out.println("读取配置文件中的参数\n\t区域：" + mkt + "；\n\t保存路径：" + path + "；\n\t命名方式：" + name + "；\n\t图片质量："
                    + pixel + "；\n\tcookie：" + cookie + "；\n\tup_key：" + GUI.UP_KEY + "；\n\tdown_key：" + GUI.DOWD_KEY);
        } catch (Exception e) {
            System.out.println("config.json文件不存在或配置错误，将使用默认配置");
        }
    }

    /** 将当前配置读入到配置文件中 */
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
     * -1(明天)0(今天)1(昨天)2(前天)以此类推最多回去到前7天的内容
     *
     * @param day 日期代码
     * @return Wallpaper
     */
    protected static Wallpaper getWallpaper(int day) {
        return getWallpapers(day, 1).get(0);
    }

    /**
     * 批量下载链接获取
     *
     * @param day
     * @param n   （n > 1）
     */
    protected static List<Wallpaper> getWallpapers(int day, int n) {
        System.out.println("总共" + n + "张壁纸，正在获取下载链接，请稍等！");
        List<Wallpaper> wallpapers = new ArrayList<>(n);

        // 获取信息
        JSONArray infos = Core.getInfo(day, n);
        infos.stream().map(info -> (JSONObject) info).forEach(item -> {
            String fileName;
            if (name.equalsIgnoreCase("link")) {
                fileName = item.getString("urlbase").substring(11);
            } else {
                fileName = item.getString("startdate");
            }
            wallpapers.add(new Wallpaper(mkt, path, pixel, item.getString("urlbase"), fileName, item.getString("copyright")));
        });

        return wallpapers;
    }

    /**
     * 处理传参，普通参数直接修改无返回，错误参数会被丢弃
     *
     * @param args 参数
     */
    private static void config(String[] args) {

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

                case "-pixel":
                    pixel = args[index + 1];
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
     * 通过微软API获取正确的下载地址 format 返回类型 js xml idx 图片时间 -1(明天)0(今天)1(昨天)2(前天)最多回去到前7天的内容
     *
     * @return JSONObject[]
     * @mkt 区域 en-US/zh-CN
     * @url https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN //
     * @idx:起始日期
     * @n ：获取数量
     */
    static JSONArray getInfo(int day, int n) {
        String MKT;

        boolean setCookie = false;
        // 行对参数进行判断是否合法,非法则返回上一次的值,默认开启会注入一个默认值,所以不会空
        if (day < 0 || day > 7) {
            System.out.println("信息：参数超出范围（0~7），为您显示最相近的一条");
        }

        // 国际版临时支持
        if (Core.mkt.equalsIgnoreCase("us")) {
            MKT = "en-US";
            setCookie = true;
        } else {
            MKT = "zh-CN";
        }

        String requestURL = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=" + day + "&n=" + n + "&mkt=" + MKT;
        String json = null;
        URL requestUrl;

        // 初始化链接
        try {
            requestUrl = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();

            // 国内版不能加国际版cookie否则会造成错误
            if (setCookie) {
                connection.setRequestProperty("Cookie", Core.cookie);
            }
            connection.connect();

            // 使用BufferedReader获取url的json数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            json = reader.readLine();

            // 关闭资源
            reader.close();
            connection.disconnect();

        } catch (IOException e) {
            LogUtil.error("获取图片信息出现错误!");
            e.printStackTrace();
        }

        return JSON.parseObject(json).getJSONArray("images");
    }

    /**
     * 多线程下载壁纸
     *
     * @param wallpapers
     * @param size
     */
    protected static void downloadPictures(List<Wallpaper> wallpapers, int size) {
        CountDownLatch pool = new CountDownLatch(size);
        // 为每张壁纸建立一个线程下载
        for (Wallpaper wallpaper : wallpapers) {
            new Thread(() -> {
                LogUtil.fine("线程建立");
                Core.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
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
        LogUtil.info("批量获取，下载完成！共" + size + "张");
    }

    /**
     * 下载图片
     *
     * @param url           url
     * @param path          下载路径
     * @param wallpaperName 文件名
     */
    static void downloadPicture(URL url, String path, String wallpaperName) {
        FileOutputStream fileOutputStream = null;
        try (DataInputStream dataInputStream = new DataInputStream(url.openStream())) {
            System.out.println("http_get ==> " + url);
            Utils.judeDir(path);
            // 初始化文件
            String imageName = path + "/" + wallpaperName;
            File file = new File(imageName);
            fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            // 开始写入文件
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println(new Date());
            LogUtil.info("下载进程结束");
//            System.out.println("信息：下载进程结束");
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
        config(args);

        // 批量获取判断
        if (n > 1) {
            // 开始下载
            List<Wallpaper> wallpapers = getWallpapers(day, n);
            downloadPictures(wallpapers, n);
            return;
        }

        // 只获取一张
        Wallpaper wallpaper = getWallpaper(day);
        Core.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
        System.out.println("pic_info:" + wallpaper.getCopyright());

        // 判断是是否需要参数
        if (saved) {
            saveConfig();
        }
    }
}
