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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.awt.event.ActionEvent;
import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;

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
		frmBingWallpaper.setTitle("Bing Wallpaper Downloader");
		frmBingWallpaper.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(GUI.class.getResource("/top/zewenchen/bingWallpaper/bing_logo.png")));
		frmBingWallpaper.getContentPane().setBackground(new Color(255, 255, 255));
		frmBingWallpaper.setBackground(new Color(255, 255, 255));
		frmBingWallpaper.setBounds(100, 100, 969, 710);
		frmBingWallpaper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmBingWallpaper.setJMenuBar(menuBar);

		JButton button = new JButton("关于");
		menuBar.add(button);
		frmBingWallpaper.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("壁纸路径");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(10, 555, 71, 21);
		frmBingWallpaper.getContentPane().add(lblNewLabel);

		text_path = new JTextField();
		text_path.setBounds(83, 555, 757, 21);
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
		btn_chosesPath.setBounds(850, 555, 93, 23);
		frmBingWallpaper.getContentPane().add(btn_chosesPath);

		JButton btn_download = new JButton("开始下载");
		btn_download.setBackground(SystemColor.controlHighlight);
		btn_download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Bing.path = text_path.getText();
				//TODO 这里要做一下路径校验
				System.out.println("下载路径已修改为 " + Bing.path);
			}
		});
		btn_download.setBounds(10, 587, 933, 38);
		frmBingWallpaper.getContentPane().add(btn_download);

		// 预览图片
//		JLabel pic = new JLabel();
//		pic.setBackground(new Color(0, 0, 0));
//		pic.setBounds(0, 0, 960, 540);
//		frmBingWallpaper.getContentPane().add(pic);

		// String imageUrl = "<html>验证码<img
		// src='https://cn.bing.com/th?id=OHR.LofotenSurfing_ZH-CN5901239545_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp'
		// height='540px' width='960px' /><html>";
		// pic.setText(imageUrl);

		String imgPath = "E:/eclipse-workspace/bingWallpaper/src/top/zewenchen/bingWallpaper/Default_1920x1080.jpg";
		// String urlStr =
		// "https://cn.bing.com/th?id=OHR.LofotenSurfing_ZH-CN5901239545_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp";

		// 获取图像
		try {
			// URL url = new URL(urlStr);
			Toolkit toolKit = Toolkit.getDefaultToolkit();
			//TODO　这里要读入网络图片
			BufferedImage image = ImageIO.read(new FileInputStream(imgPath));
			// BufferedImage image = (BufferedImage) toolKit.getImage(url);
			ScaleIcon icon = new ScaleIcon(new ImageIcon(image));
			JLabel pic = new JLabel(icon);
			pic.setBackground(new Color(0, 0, 0));
			pic.setBounds(0, 0, 960, 540);
			frmBingWallpaper.getContentPane().add(pic);
		} catch (Exception e1) { 
			e1.printStackTrace();
		}

		//TODO 下一个版本加入上一天和下一天
		
	}
}
