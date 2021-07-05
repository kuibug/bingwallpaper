package top.kuibug.bingWallpaper;

import top.kuibug.util.ConsoleTextArea;
import top.kuibug.util.LogUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class GUI {

    private JFrame frmBingWallpaper;
    private JTextField text_path; // 下载路径
    private ConsoleTextArea log; // 日志预览
    private int dayU = 0; // 日期标记

    static Wallpaper wallpaper; // 初始化一个公用对象
    static Toolkit toolKit;
    static JLabel pic; // 图片显示区域
    static JTextArea pic_info; // 图片版权等信息显示区域
    static JButton btn_download;// 下载按钮

    static int UP_KEY = KeyEvent.VK_F7;
    static int DOWD_KEY = KeyEvent.VK_F8;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GUI window = new GUI();
            window.frmBingWallpaper.setVisible(true);
            // 这玩意挪回来，容易丢失Core的初始化日志
            Core.initial();
            changePic(0);
            System.out.println("Notice：默认以F7切换上一天壁纸，F8切换下一天壁纸！");
        });
    }

    /**
     * Create the application.
     */
    public GUI() {
        frmBingWallpaper = new JFrame();
        frmBingWallpaper.setResizable(false);
        frmBingWallpaper.setAutoRequestFocus(false);
        frmBingWallpaper.setTitle("Bing Wallpaper Downloader");
        frmBingWallpaper.setIconImage(Toolkit.getDefaultToolkit().getImage("bing_logo.png"));
        frmBingWallpaper.getContentPane().setBackground(new Color(255, 255, 255));
        frmBingWallpaper.setBackground(new Color(255, 255, 255));
        frmBingWallpaper.setSize(744, 650);
        frmBingWallpaper.setLocationRelativeTo(null);
        frmBingWallpaper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 添加监听
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventPostProcessor(this.getMyKeyEventHandler());

        // ===========================菜单栏======================================
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(new Font("宋体", Font.PLAIN, 12));
        frmBingWallpaper.setJMenuBar(menuBar);

        JMenu menu_2 = new JMenu("设置");
        menu_2.setFont(new Font("宋体", Font.PLAIN, 12));
        menu_2.setBackground(new Color(240, 240, 240));
        menuBar.add(menu_2);

        JRadioButtonMenuItem radioButtonMenuItem_2 = new JRadioButtonMenuItem("注册系统任务计划");
        radioButtonMenuItem_2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    File file = new File("./registeredAutoDownload.bat");
                    Process process = Runtime.getRuntime().exec("cmd /c start" + file);
                    System.out.println(process);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        radioButtonMenuItem_2.setFont(new Font("宋体", Font.PLAIN, 12));
        menu_2.add(radioButtonMenuItem_2);

        // =================批量获取====================
        JLabel lblNewLabel = new JLabel("  批量获取");
        lblNewLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("批量获取中...");
                Di di = new Di();
                di.setTitle("批量获取");
                di.setModal(true);
                di.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                di.setLocationRelativeTo(null);
                di.setVisible(true);
            }
        });
        lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        menuBar.add(lblNewLabel);
        frmBingWallpaper.getContentPane().setLayout(null);

        // ==========================预览图片===============================
        toolKit = frmBingWallpaper.getToolkit();
        // 获取图像
        pic = new JLabel("loading...");
        pic.setHorizontalAlignment(JLabel.CENTER);
        pic.setFont(new Font("Monospaced", Font.BOLD, 28));

        pic.setBounds(8, 8, 720, 405);
        frmBingWallpaper.getContentPane().add(pic);

        // ========================图片的版权信息==============================
        pic_info = new JTextArea(2, 40);
        pic_info.setLineWrap(true); // 激活自动换行功能
        pic_info.setWrapStyleWord(true); // 激活断行不断字功能
        pic_info.setFont(new Font("Monospaced", Font.PLAIN, 13));
        pic_info.setBounds(18, 422, 710, 21);
        pic_info.append("图片信息，请认真看待版权问题！");
        pic_info.setEditable(false);
        frmBingWallpaper.getContentPane().add(pic_info);

        // =====================分隔线==========================
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.GRAY);
        separator.setBounds(8, 453, 720, 8);
        frmBingWallpaper.getContentPane().add(separator);

        // =====================label===========================
        JLabel label = new JLabel("壁纸路径");
        label.setFont(new Font("宋体", Font.PLAIN, 14));
        label.setBounds(8, 464, 71, 21);
        frmBingWallpaper.getContentPane().add(label);

        // =====================路径==========================
        text_path = new JTextField(Core.path);
        text_path.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                // 失焦的时候将填写的路径保存
                String text = text_path.getText();

                // 手动输入地址‘/’自动补全
                if (!text.endsWith("/"))
                    text = text + "/";
                Core.path = text;
                wallpaper.setPath(text);
            }
        });

        text_path.setBounds(81, 464, 436, 21);
        frmBingWallpaper.getContentPane().add(text_path);

        // =====================路径选择按钮=============================
        JButton btn_chosePath = new JButton("选择");
        btn_chosePath.setBackground(SystemColor.controlHighlight);
        btn_chosePath.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            FileSystemView fsv = FileSystemView.getFileSystemView();
            jfc.setCurrentDirectory(fsv.getHomeDirectory());
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            // 用户选择的路径或文件
            if (jfc.showOpenDialog(frmBingWallpaper) == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                if (file.isDirectory()) {
                    System.out.println("文件夹:" + file.getAbsolutePath());
                    // 确认完毕后更改显示并更改下载路径
                    text_path.setText(file.getAbsolutePath());
                    Core.path = file.getAbsolutePath();
                    wallpaper.setPath(file.getAbsolutePath());
                    System.out.println("info:下载路径已修改为 " + Core.path);
                } else if (file.isFile()) {
                    LogUtil.error("这是个文件:" + file.getAbsolutePath());
                }
            }
        });
        btn_chosePath.setBounds(532, 463, 93, 23);
        frmBingWallpaper.getContentPane().add(btn_chosePath);

        // =============================变小的下载按钮=============================
        btn_download = new JButton("开始下载");
        btn_download.setBackground(SystemColor.controlHighlight);
        btn_download.addActionListener(e -> {
            Core.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
            btn_download.setText("下载完成！");
        });
        btn_download.setBounds(635, 463, 95, 23);
        // 初始状态不可用，防止下载错误
        btn_download.setEnabled(false);
        frmBingWallpaper.getContentPane().add(btn_download);

        // =========================实时日志==================================
        try {
            log = new ConsoleTextArea();
            log.setFont(new Font("Monospaced", Font.PLAIN, 13));
            log.setEditable(false);
        } catch (IOException e) {
            LogUtil.error("err:无法创建LoopedStreams：" + e);
            System.exit(1);
        }
        JScrollPane scroll = new JScrollPane(log);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        log.setBounds(8, 511, 720, 39);
        scroll.setBounds(8, 495, 720, 95);
        scroll.setViewportView(log);
        frmBingWallpaper.getContentPane().add(scroll);
    }


    // ================================监听处理================================================

    /**  图片切换按键监听 */
    public KeyEventPostProcessor getMyKeyEventHandler() {
        return e -> {
            //这里改成KEY_RELEASED，防止按下之后疯狂切换
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                System.out.println("info：key = '" + KeyEvent.getKeyText(e.getKeyCode())
                                + "', keycode = " + e.getKeyCode());
                // 上翻
                if (e.getKeyCode() == UP_KEY) {
                    if (dayU < 7) {
                        System.out.println("正在尝试切换到上一天的壁纸");
                        dayU++;
                        new Thread(() -> changePic(dayU)).start();
                    } else {
                        LogUtil.info("已经到尽头了，再怎么翻也没有啦~(￣▽￣)~*");
                    }
                }
                // 下翻
                if (e.getKeyCode() == DOWD_KEY) {
                    if (dayU > 0) {
                        dayU--;
                        System.out.println("正在尝试切换到下一天的壁纸");
                        new Thread(() -> changePic(dayU)).start();
                    } else {
                        LogUtil.info("已经是最新的啦！\\(0^◇^0)/");
                    }
                }
            }

            return true;
        };
    }

    /**
     * 刷新处理，刷新整个界面
     *
     * @param i 日期标记
     */
    static void changePic(int i) {
        wallpaper = Core.getWallpaper(i);
        Image image = toolKit.getImage(wallpaper.getUrl());
        ScaleIcon icon = new ScaleIcon(new ImageIcon(image));
        // 刷新预览图
        pic.setIcon(icon);
        // 刷新版权信息
        pic_info.setText(wallpaper.getCopyright());
        // 刷新按钮状态
        btn_download.setEnabled(true);
        btn_download.setText("开始下载");

        LogUtil.info("壁纸加载成功！当前日期代码" + i);
    }
}
