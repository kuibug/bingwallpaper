package top.zewenchen.bingWallpaper;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.GridLayout;
import java.awt.Color;

public class Di extends JDialog {

	/**
	 * v0.0.1
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public Di() {
		setBounds(100, 100, 375, 127);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(255, 255, 255));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JTextPane date_pane = new JTextPane();
		date_pane.setLocation(6, 5);
		date_pane.setText("起始日期");
		date_pane.setSize(56, 27);
		contentPanel.add(date_pane);

		JComboBox<Integer> date = new JComboBox<Integer>();
		date.setBounds(72, 5, 80, 27);
		date.addItem(0);
		date.addItem(1);
		date.addItem(2);
		date.addItem(3);
		date.addItem(4);
		date.addItem(5);
		date.addItem(6);
		date.addItem(7);
		contentPanel.add(date);

		JTextPane date_Disg = new JTextPane();
		date_Disg.setText("0 = 今天，1 = 昨天，以此类推");
		date_Disg.setBounds(162, 5, 187, 27);
		contentPanel.add(date_Disg);

		JTextPane size_pane = new JTextPane();
		size_pane.setBounds(6, 32, 56, 27);
		size_pane.setText("获取数量");
		contentPanel.add(size_pane);

		JComboBox<Integer> size = new JComboBox<Integer>();
		size.setBounds(72, 32, 80, 27);
		size.addItem(2);
		size.addItem(3);
		size.addItem(4);
		size.addItem(5);
		size.addItem(6);
		size.addItem(7);
		contentPanel.add(size);

		JTextPane textPane = new JTextPane();
		textPane.setText("以起始日期开始往回获取 N 张");
		textPane.setBounds(162, 32, 187, 27);
		contentPanel.add(textPane);

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton downloadButton = new JButton("下载");
			buttonPane.setLayout(new GridLayout(0, 1, 0, 0));
			buttonPane.add(downloadButton);
			getRootPane().setDefaultButton(downloadButton);
			downloadButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int day = (int) date.getSelectedItem();
					int n = (int) size.getSelectedItem();
					Wallpaper[] wallpapers = Bing.getWallpapers(day, n);
					Bing.downloadPictures(wallpapers,n);
					downloadButton.setText("下载完毕！");
					System.out.println();
					// dispose();
				}
			});
		}

	}
}
