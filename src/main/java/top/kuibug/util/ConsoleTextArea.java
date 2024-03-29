package top.kuibug.util;

import top.kuibug.util.LoopedStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * 持续检测print流，重定向到JTextArea中
 * @author 俞良松
 * @webPage https://www.ibm.com/developerworks/cn/java/l-console/index.html#author
 */
public class ConsoleTextArea extends JTextArea {
	
	private static final long serialVersionUID = 1L;

	public ConsoleTextArea() throws IOException {
		final LoopedStreams ls = new LoopedStreams();
		// 重定向System.out和System.err
		PrintStream ps = new PrintStream(ls.getOutputStream());
		System.setOut(ps);
		System.setErr(ps);
		startConsoleReaderThread(ls.getInputStream());
	} // ConsoleTextArea()

	private void startConsoleReaderThread(InputStream inStream) {
		final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		new Thread(() -> {
			StringBuilder sb = new StringBuilder();
			try {
				String s;
				Document doc = getDocument();
				while ((s = br.readLine()) != null) {
					boolean caretAtEnd;
					caretAtEnd = getCaretPosition() == doc.getLength();
					sb.setLength(0);
					append(sb.append(s).append('\n').toString());
					if (caretAtEnd)
						setCaretPosition(doc.getLength());
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "从BufferedReader读取错误：" + e);
				System.exit(1);
			}
		}).start();
	} // startConsoleReaderThread()
}