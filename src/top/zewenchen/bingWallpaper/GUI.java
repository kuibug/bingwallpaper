package top.zewenchen.bingWallpaper;

import top.zewenchen.util.ConsoleTextArea;
import top.zewenchen.util.ScaleIcon;
import top.zewenchen.util.Utils;

import java.awt.Font;
import java.awt.Image;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import java.io.File;
import java.io.IOException;

public class GUI {

	private JFrame frmBingWallpaper;
	private JTextField text_path;
	private int dayU = 0;
	static Wallpaper wallpaper;
	static Toolkit toolKit;
	static JLabel pic = null;
	static JTextArea pic_info;
	ConsoleTextArea log = null;
	static JButton btn_download;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmBingWallpaper.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 新建一个线程区优化加载速度
		new Thread(() -> {
			Bing.initial();
			changePic(0);
		}).start();
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBingWallpaper = new JFrame();
		frmBingWallpaper.setResizable(false);
		frmBingWallpaper.setAutoRequestFocus(false);
		frmBingWallpaper.setTitle("Bing Wallpaper Downloader");
		frmBingWallpaper.setIconImage(Toolkit.getDefaultToolkit().getImage("./bing_logo.png"));
		frmBingWallpaper.getContentPane().setBackground(new Color(255, 255, 255));
		frmBingWallpaper.setBackground(new Color(255, 255, 255));
		// frmBingWallpaper.setBounds(50, 50,745, 600 );

		frmBingWallpaper.setSize(744, 650);
		frmBingWallpaper.setLocationRelativeTo(null);
		frmBingWallpaper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 添加监听
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventPostProcessor((KeyEventPostProcessor) this.getMyKeyEventHandler());

		// 菜单栏
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

		JMenu menu = new JMenu("使用帮助");
		menu.setFont(new Font("宋体", Font.PLAIN, 12));
		menuBar.add(menu);

		JMenuItem menuItem_1 = new JMenuItem("帮助文档");
		menuItem_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				String url = "http://zewenchenkmfoxm.coding.me/bingWallpaper";
				Utils.browserUrl(url);
			}
		});
		menuItem_1.setFont(new Font("宋体", Font.PLAIN, 12));
		menu.add(menuItem_1);

		JMenuItem mntmFqa = new JMenuItem("FQA");
		mntmFqa.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				String url = "http://zewenchenkmfoxm.coding.me/bingWallpaper";
				Utils.browserUrl(url);
			}
		});
		mntmFqa.setFont(new Font("Arial", Font.PLAIN, 12));
		menu.add(mntmFqa);

		JMenu menu_1 = new JMenu("关于");
		menu_1.setFont(new Font("宋体", Font.PLAIN, 12));
		menuBar.add(menu_1);

		JMenuItem menuItem = new JMenuItem("关于下载器");
		menuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				String url = "http://zewenchenkmfoxm.coding.me/bingWallpaper";
				Utils.browserUrl(url);
				// System.out.println("我他喵执行了");
			}
		});
		menuItem.setFont(new Font("宋体", Font.PLAIN, 12));
		menu_1.add(menuItem);
		frmBingWallpaper.getContentPane().setLayout(null);

		// 预览图片
		toolKit = frmBingWallpaper.getToolkit();
		// 获取图像
		try {
			pic = new JLabel("loading……");
			pic.setHorizontalAlignment(JLabel.CENTER);
			pic.setFont(new Font("Monospaced", Font.BOLD, 28));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		pic.setBounds(8, 8, 720, 405);
		frmBingWallpaper.getContentPane().add(pic);

		// 图片的版权信息
		pic_info = new JTextArea(2, 40);
		pic_info.setLineWrap(true); // 激活自动换行功能
		pic_info.setWrapStyleWord(true); // 激活断行不断字功能
		pic_info.setFont(new Font("宋体", Font.PLAIN, 12));
		pic_info.setBounds(18, 422, 710, 21);
		pic_info.append("图片信息，请认真看待版权问题！");
		pic_info.setEditable(false);
		// System.out.println(wallpaper.getCopyright());
		frmBingWallpaper.getContentPane().add(pic_info);

		// 分隔线
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.GRAY);
		separator.setBounds(8, 453, 720, 8);
		frmBingWallpaper.getContentPane().add(separator);

		// 图片下方的
		JLabel lblNewLabel = new JLabel("壁纸路径");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(8, 464, 71, 21);
		frmBingWallpaper.getContentPane().add(lblNewLabel);

		// 路径
		text_path = new JTextField(Bing.path);
		text_path.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("试图修改路径");
				// 失焦的时候将填写的路径保存
				String text = text_path.getText();
				if (!text.endsWith("/"))
					text = text + "/";
				Bing.path = text;
				wallpaper.setPath(text);
			}
		});

		text_path.setBounds(81, 464, 436, 21);
		frmBingWallpaper.getContentPane().add(text_path);
		text_path.setColumns(10);

		JButton btn_chosesPath = new JButton("选择");
		btn_chosesPath.setBackground(SystemColor.controlHighlight);
		btn_chosesPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				// 设置当前路径为桌面路径,否则将我的文档作为默认路径
				FileSystemView fsv = FileSystemView.getFileSystemView();
				jfc.setCurrentDirectory(fsv.getHomeDirectory());
				// JFileChooser.FILES_AND_DIRECTORIES 选择路径和文件
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				// 用户选择的路径或文件
				if (jfc.showOpenDialog(frmBingWallpaper) == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					if (file.isDirectory()) {
						System.out.println("文件夹:" + file.getAbsolutePath());
						// 确认完毕后更改显示并更改下载路径
						text_path.setText(file.getAbsolutePath());
						Bing.path = file.getAbsolutePath();
						wallpaper.setPath(file.getAbsolutePath());
					} else if (file.isFile()) {
						System.out.println("这是个文件:" + file.getAbsolutePath());
					}
				}
			}
		});
		btn_chosesPath.setBounds(532, 463, 93, 23);
		frmBingWallpaper.getContentPane().add(btn_chosesPath);

		// 巨大的下载按钮
		btn_download = new JButton("开始下载");
		btn_download.setBackground(SystemColor.controlHighlight);
		btn_download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Utils.judeDir(Bing.path);
				System.out.println("下载路径已修改为 " + Bing.path);
				BingCore.downloadPicture(wallpaper.getUrl(), wallpaper.getPath(), wallpaper.getName());
				btn_download.setText("下载完成！");
			}
		});
		btn_download.setBounds(635, 463, 95, 23);
		btn_download.setEnabled(false);
		frmBingWallpaper.getContentPane().add(btn_download);

		// 实时日志
		try {
			log = new ConsoleTextArea();
			log.setFont(new Font("Monospaced", Font.PLAIN, 13));
		} catch (IOException e) {
			System.err.println("不能创建LoopedStreams：" + e);
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

	/**
	 * 监听处理
	 * 
	 * @return
	 */
	public KeyEventPostProcessor getMyKeyEventHandler() {
		return new KeyEventPostProcessor() {
			public boolean postProcessKeyEvent(KeyEvent e) {

				// PAGE_UP
				if (e.paramString().charAt(4) == 'P' && e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
					System.out.println("正在尝试切换到上一天的壁纸");

					if (dayU < 7) {
						dayU++;
						new Thread(() -> {
							changePic(dayU);
						}).start();
					} else {
						System.out.println("已经到尽头了，再怎么翻也没有啦~(￣▽￣)~*");
					}

				}
				// PAGE_DOWN
				if (e.paramString().charAt(4) == 'P' && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
					System.out.println("正在尝试切换到下一天的壁纸");

					if (dayU > 0) {
						dayU--;
						new Thread(() -> {
							changePic(dayU);
						}).start();
					} else {
						System.out.println("已经是最新的啦！\\(0^◇^0)/");
					}

				}
				return true;
			}

		};
	}

	/**
	 * 刷新预览
	 * 
	 * @param i
	 */
	static void changePic(int i) {
		try {
			wallpaper = Bing.getWallpaper(i);
		} catch (Exception e) {
			pic.setText("壁纸获取失败，请检查网络！");
		}
		Image image = toolKit.getImage(wallpaper.getUrl());
		ScaleIcon icon = new ScaleIcon(new ImageIcon(image));
		// 刷新预览图
		pic.setIcon(icon);
		// 刷新信息
		pic_info.setText(wallpaper.getCopyright());
		btn_download.setEnabled(true);
		System.out.println("壁纸加载成功！当前日期代码" + i);
	}
}
