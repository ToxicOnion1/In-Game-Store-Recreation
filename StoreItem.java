package com.mycompany.deza;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class StoreItem extends JComponent {

    static final Color BG = new Color(0x4A4458);
    static final Color HOVER = new Color(0x625A78);
    static final Color BORDER = new Color(0x2B2631);
    static final Color OWNED = new Color(0x5CC76B);

    private final String name;
    private final int price;
    private final ShopFrame shop;
    private boolean hovered = false;
    private boolean owned = false;
    private Image icon;

    public StoreItem(String name, int price, ShopFrame shop) {
        this.name = name;
        this.price = price;
        this.shop = shop;
        setPreferredSize(new Dimension(90, 80));

        String file = name.toLowerCase().replace(" ", "_").replace(".", "") + ".png";
        try {
            icon = ImageIO.read(getClass().getResource("/icons/" + file));
        } catch (Exception e) {
            icon = null;
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                Container parent = getParent();
                if (parent != null) {
                    parent.setComponentZOrder(StoreItem.this, 0);
                }
                repaintAround();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaintAround();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (owned) {
                    shop.showMessage(name + " is already owned.", false);
                } else if (shop.tryBuy(name, price)) {
                    owned = true;
                    shop.addToInventory(name, price, icon);
                    repaintAround();
                }
            }
        });
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
        repaintAround();
    }

    private void repaintAround() {
        if (getParent() != null) getParent().repaint();
        else repaint();
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        double scale = hovered ? 1.15 : 1.0;
        int newW = (int) (w * scale);
        int newH = (int) (h * scale);
        int x = (w - newW) / 2;
        int y = (h - newH) / 2;

        g2.setColor(hovered ? HOVER : BG);
        g2.fillRect(x, y, newW, newH);

        g2.setColor(owned ? OWNED : BORDER);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x + 1, y + 1, newW - 3, newH - 3);

        if (icon != null) {
            int textAreaHeight = 28;
            int iconSize = Math.min(newW - 16, newH - textAreaHeight - 10);
            int ix = x + (newW - iconSize) / 2;
            int iy = y + 6;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(icon, ix, iy, iconSize, iconSize, null);

            g2.setColor(Color.WHITE);
            drawFittedText(g2, x, y + newH - textAreaHeight, newW, textAreaHeight, hovered ? 11 : 10);
        } else {
            g2.setColor(Color.WHITE);
            drawFittedText(g2, x, y, newW, newH, hovered ? 14 : 12);
        }

        if (owned) {
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(x, y, newW, newH);
        }

        g2.dispose();
    }

    private void drawFittedText(Graphics2D g2, int x, int y, int w, int h, int startSize) {
        int maxWidth = w - 14;
        int size = startSize;
        List<String> lines;
        FontMetrics fm;

        while (true) {
            g2.setFont(new Font("SansSerif", Font.BOLD, size));
            fm = g2.getFontMetrics();
            lines = wrap(name, fm, maxWidth);

            boolean fits = lines.size() <= 2;
            for (String line : lines) {
                if (fm.stringWidth(line) > maxWidth) fits = false;
            }

            if (fits || size <= 8) break;
            size--;
        }

        int lineHeight = fm.getHeight();
        int totalHeight = lineHeight * lines.size();
        int ty = y + (h - totalHeight) / 2 + fm.getAscent();

        for (String line : lines) {
            int tx = x + (w - fm.stringWidth(line)) / 2;
            g2.drawString(line, tx, ty);
            ty += lineHeight;
        }
    }

    private List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");

        if (words.length == 2) {
            lines.add(words[0]);
            lines.add(words[1]);
            return lines;
        }

        String current = "";
        for (String word : words) {
            String test = current.isEmpty() ? word : current + " " + word;
            if (fm.stringWidth(test) <= maxWidth || current.isEmpty()) {
                current = test;
            } else {
                lines.add(current);
                current = word;
            }
        }
        lines.add(current);
        return lines;
    }
}