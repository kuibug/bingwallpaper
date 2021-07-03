package top.kuibug.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * 解决线程之间的冲突
 *
 * @author 俞良松
 * @webPage https://www.ibm.com/developerworks/cn/java/l-console/index.html#author
 */
public class LoopedStreams {
    private final int REFRESH_TIME = 1000;
    private final PipedOutputStream pipedOS = new PipedOutputStream();
    private boolean keepRunning = true;
    private final ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream() {
        public void close() {
            keepRunning = false;
            try {
                super.close();
                pipedOS.close();
            } catch (IOException e) {
                // 记录错误或其他处理
                // 为简单计，此处我们直接结束
                System.exit(1);
            }
        }
    };
    private final PipedInputStream pipedIS = new PipedInputStream() {
        public void close() {
            keepRunning = false;
            try {
                super.close();
            } catch (IOException e) {
                // 记录错误或其他处理
                // 为简单计，此处我们直接结束
                System.exit(1);
            }
        }
    };

    public LoopedStreams() throws IOException {
        pipedOS.connect(pipedIS);
        startByteArrayReaderThread();
    }

    public InputStream getInputStream() {
        return pipedIS;
    }

    public OutputStream getOutputStream() {
        return byteArrayOS;
    }

    private void startByteArrayReaderThread() {
        new Thread(() -> {
            while (keepRunning) {
                // 检查流里面的字节数
                if (byteArrayOS.size() > 0) {
                    byte[] buffer;
                    synchronized (byteArrayOS) {
                        buffer = byteArrayOS.toByteArray();
                        byteArrayOS.reset(); // 清除缓冲区
                    }
                    try {
                        // 把提取到的数据发送给PipedOutputStream
                        pipedOS.write(buffer, 0, buffer.length);
                    } catch (IOException e) {
                        // 记录错误或其他处理
                        // 为简单计，此处我们直接结束
                        System.exit(1);
                    }
                } else // 没有数据可用，线程进入睡眠状态
                    try {
                        // 定时查看ByteArrayOutputStream检查新数据
                        Thread.sleep(REFRESH_TIME);
                    } catch (InterruptedException ignored) {}
            }
        }).start();
    }
}