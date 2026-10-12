package com.mycompany.deza;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class InventoryPanel extends JPanel {

    static final int SLOTS = 12;
    static final int COLUMNS = 6;
    static final int SLOT_SIZE = 44;
    static final int GAP = 8;
    static final int LEFT = 30;
    static final int TOP = 75;

    static final Color PANEL_BG = new Color(0x141414);
    static final Color SLOT_BG = new Color(0x2A2A2A);
    static final Color SLOT_FILLED = new Color(0x3A4A46);
    static final Color GREEN = new Color(0x7FFFE0);
    static final Color BADGE = new Color(0x4FBF9F);

    private final List<String> names = new ArrayList<>();
    private final List<Image> icons = new ArrayList<>();
    private final List<Integer> prices = new ArrayList<>();
    private int totalValue = 0;
    private IntConsumer slotClickListener;

    public InventoryPanel() {
        setPreferredSize(new Dimension(1100, 185));
        setBackground(PANEL_BG);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = slotAt(e.getX(), e.getY());
                if (index >= 0 && index < names.size() && slotClickListener != null) {
                    slotClickListener.accept(index);
                }
            }
        });
    }

    public void setSlotClickListener(IntConsumer listener) {
        this.slotClickListener = listener;
    }

    // returns the slot index under the mouse, or -1
    private int slotAt(int mx, int my) {
        for (int i = 0; i < SLOTS; i++) {
            int x = LEFT + (i % COLUMNS) * (SLOT_SIZE + GAP);
            int y = TOP + (i / COLUMNS) * (SLOT_SIZE + GAP);
            if (mx >= x && mx < x + SLOT_SIZE && my >= y && my < y + SLOT_SIZE) return i;
        }
        return -1;
    }

    public boolean isFull() {
        return names.size() >= SLOTS;
    }

    public void addItem(String name, int price, Image icon) {
        names.add(name);
        icons.add(icon);
        prices.add(price);
        totalValue += price;
        repaint();
    }

    public String getItemName(int index) {
        return names.get(index);
    }

    public int getItemPrice(int index) {
        return prices.get(index);
    }

    public void removeItem(int index) {
        names.remove(index);
        icons.remove(index);
        totalValue -= prices.remove(index);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // item count badge (circle)
        g2.setColor(BADGE);
        g2.fillOval(LEFT, 14, 44, 44);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        String count = String.valueOf(names.size());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(count, LEFT + (44 - fm.stringWidth(count)) / 2, 14 + 22 + fm.getAscent() / 2 - 2);

        // total value + label
        g2.setColor(GREEN);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        String value = String.valueOf(totalValue);
        g2.drawString(value, LEFT + 62, 45);
        int valueWidth = g2.getFontMetrics().stringWidth(value);

        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(0x6FBFA8));
        g2.drawString("ITEM VALUE", LEFT + 62 + valueWidth + 10, 45);

        // slots
        for (int i = 0; i < SLOTS; i++) {
            int x = LEFT + (i % COLUMNS) * (SLOT_SIZE + GAP);
            int y = TOP + (i / COLUMNS) * (SLOT_SIZE + GAP);
            boolean filled = i < names.size();

            g2.setColor(filled ? SLOT_FILLED : SLOT_BG);
            g2.fillRoundRect(x, y, SLOT_SIZE, SLOT_SIZE, 8, 8);

            if (filled) {
                Image icon = icons.get(i);
                if (icon != null) {
                    g2.drawImage(icon, x + 4, y + 4, SLOT_SIZE - 8, SLOT_SIZE - 8, null);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                    String initials = initials(names.get(i));
                    FontMetrics f = g2.getFontMetrics();
                    g2.drawString(initials, x + (SLOT_SIZE - f.stringWidth(initials)) / 2,
                                  y + (SLOT_SIZE + f.getAscent()) / 2 - 2);
                }
            }
        }
        g2.dispose();
    }

    private String initials(String name) {
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (!word.isEmpty() && sb.length() < 2) sb.append(Character.toUpperCase(word.charAt(0)));
        }
        return sb.toString();
    }
}