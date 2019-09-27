package top.zewenchen.bingWallpaper;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI {

	private JFrame frmBingWallpaper;
	private JTextField text_path;

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
		frmBingWallpaper.getContentPane().setFont(new Font("幼圆", Font.PLAIN, 12));
		frmBingWallpaper.setFont(new Font("幼圆", Font.PLAIN, 12));
		frmBingWallpaper.setAutoRequestFocus(false);
		frmBingWallpaper.setTitle("Bing Wallpaper Downloader");
		frmBingWallpaper.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(GUI.class.getResource("/top/zewenchen/bingWallpaper/bing_logo.png")));
		frmBingWallpaper.getContentPane().setBackground(new Color(255, 255, 255));
		frmBingWallpaper.setBackground(new Color(255, 255, 255));
		frmBingWallpaper.setBounds(50, 50, 980, 700);
		frmBingWallpaper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("幼圆", Font.PLAIN, 12));
		frmBingWallpaper.setJMenuBar(menuBar);

		JMenu menu_2 = new JMenu("设置");
		menu_2.setFont(new Font("宋体", Font.PLAIN, 12));
		menu_2.setBackground(new Color(240, 240, 240));
		menuBar.add(menu_2);

		JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem("下载图片质量");
		radioButtonMenuItem.setFont(new Font("宋体", Font.PLAIN, 12));
		menu_2.add(radioButtonMenuItem);

		JRadioButtonMenuItem radioButtonMenuItem_1 = new JRadioButtonMenuItem("下载图片日期设定");
		radioButtonMenuItem_1.setFont(new Font("宋体", Font.PLAIN, 12));
		menu_2.add(radioButtonMenuItem_1);

		JRadioButtonMenuItem radioButtonMenuItem_2 = new JRadioButtonMenuItem("注册系统任务计划");
		radioButtonMenuItem_2.setFont(new Font("宋体", Font.PLAIN, 12));
		menu_2.add(radioButtonMenuItem_2);

		JMenu menu = new JMenu("使用帮助");
		menu.setFont(new Font("宋体", Font.PLAIN, 12));
		menuBar.add(menu);

		JMenuItem menuItem_1 = new JMenuItem("常用设置说明");
		menuItem_1.setFont(new Font("宋体", Font.PLAIN, 12));
		menu.add(menuItem_1);

		JMenuItem mntmFqa = new JMenuItem("FQA");
		mntmFqa.setFont(new Font("Arial", Font.PLAIN, 12));
		menu.add(mntmFqa);

		JMenu menu_1 = new JMenu("关于");
		menu_1.setFont(new Font("宋体", Font.PLAIN, 12));
		menuBar.add(menu_1);

		JMenuItem menuItem = new JMenuItem("关于下载器");
		menuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});
		menuItem.setFont(new Font("宋体", Font.PLAIN, 12));
		menu_1.add(menuItem);
		frmBingWallpaper.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("壁纸路径");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(8, 570, 71, 21);
		frmBingWallpaper.getContentPane().add(lblNewLabel);

		text_path = new JTextField();
		text_path.setBounds(81, 570, 757, 21);
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
						text_path.setText(file.getAbsolutePath());
					} else if (file.isFile()) {
						System.out.println("文件:" + file.getAbsolutePath());

					}
				}
			}
		});
		btn_chosesPath.setBounds(848, 570, 93, 23);
		frmBingWallpaper.getContentPane().add(btn_chosesPath);

		JButton btn_download = new JButton("开始下载");
		btn_download.setBackground(SystemColor.controlHighlight);
		btn_download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Bing.path = text_path.getText();
				// TODO 这里要做一下路径校验
				System.out.println("下载路径已修改为 " + Bing.path);
			}
		});
		btn_download.setBounds(8, 602, 933, 38);
		frmBingWallpaper.getContentPane().add(btn_download);

		// 预览图片
		String urlStr = "https://cn.bing.com/th?id=OHR.BardenasDesert_ZH-CN1357611840_1920x1080.jpg";

		// 获取图像
		try {
			URL url = new URL(urlStr);
			Toolkit toolKit = frmBingWallpaper.getToolkit();
			Image image = toolKit.getImage(url);
			ScaleIcon icon = new ScaleIcon(new ImageIcon(image));
			JLabel pic = new JLabel(icon);
			pic.setBounds(8, 8, 960, 540);
			frmBingWallpaper.getContentPane().add(pic);

			JSeparator separator = new JSeparator();
			separator.setForeground(Color.GRAY);
			separator.setBounds(0, 558, 953, 2);
			frmBingWallpaper.getContentPane().add(separator);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// TODO 下一个版本加入上一天和下一天

	}
}
