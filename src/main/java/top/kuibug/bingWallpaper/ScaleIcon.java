package top.kuibug.bingWallpaper;


import javax.swing.*;
import java.awt.*;

/**
 * 自动将Icon缩放到和窗口一样大小
 *
 * @author craftsman
 * @date 2019年9月27日
 */
public class ScaleIcon implements Icon {

    private final Icon icon;

    public ScaleIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        float wid = c.getWidth();
        float hei = c.getHeight();
        int iconWid = icon.getIconWidth();
        int iconHei = icon.getIconHeight();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.scale(wid / iconWid, hei / iconHei);
        icon.paintIcon(c, g2d, 0, 0);
    }

}