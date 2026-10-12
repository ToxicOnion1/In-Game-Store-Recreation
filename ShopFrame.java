package com.mycompany.deza;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ShopFrame extends JFrame {

    static int[] PRICES = {800, 1600, 3200, 6400};
    static int[] COLUMNS = {5, 6, 5, 6};
    static Color[] TEXT = {new Color(0x4B4358), new Color(0x4B4358), new Color(0xE6E0EE), new Color(0xD8D0E0)};

    static final double SELL_RATIO = 0.5;   // 0.5 = 50% refund, 1.0 = full refund

    static double[][] AREAS = {
        {0.023, 0.155, 0.438, 0.341},
        {0.474, 0.030, 0.489, 0.466},
        {0.025, 0.532, 0.444, 0.431},
        {0.478, 0.532, 0.498, 0.431}
    };

    static String[][] ITEMS = {
        {"Extra Charge", "Extra Spirit", "Golden Goose Egg", "Mystic Burst",
         "Mystic Expansion", "Mystic Regen", "Rusted Barrel", "Spirit Strike"},

        {"Arcane Surge", "Bullet Resist", "Cold Front", "Compress CD", "Duration Ext.",
         "Improved Spirit", "Mystic Slow", "Mystic Vuln."},

        {"Decay", "Disarming Hex", "Greater Expansion", "Knockdown", "Radiant Regen",
         "Rapid Recharge", "Silence Wave", "Spirit Snatch"},

        {"Arctic Blast", "Boundless Spirit", "Escalating Exp.",
         "Ethereal Shift", "Lightning Scroll", "Magic Carpet", "Spirit Burn",
         "Transcendent CD"}
    };

    private int balance = 20000;
    private JLabel balanceLabel = new JLabel();
    private InventoryPanel inventory = new InventoryPanel();
    private JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
    private javax.swing.Timer clearTimer;
    private final Map<String, StoreItem> storeItems = new HashMap<>();

    public boolean tryBuy(String name, int price) {
        if (inventory.isFull()) {
            showMessage("Inventory is full!", false);
            return false;
        }
        if (balance < price) {
            showMessage("Not enough souls for " + name + "! Need " + (price - balance) + " more.", false);
            return false;
        }
        balance -= price;
        updateBalance();
        showMessage("Purchased " + name + " for " + price + " souls!", true);
        return true;
    }

    public void addToInventory(String name, int price, Image icon) {
        inventory.addItem(name, price, icon);
    }

    public void sellItem(int index) {
        String name = inventory.getItemName(index);
        int refund = (int) (inventory.getItemPrice(index) * SELL_RATIO);

        inventory.removeItem(index);
        balance += refund;
        updateBalance();

        StoreItem card = storeItems.get(name);
        if (card != null) card.setOwned(false);

        showMessage("Sold " + name + " for " + refund + " souls.", true);
    }

    private void updateBalance() {
        balanceLabel.setText("Souls: " + balance);
    }

    public void showMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success ? new Color(0x7CFC9A) : new Color(0xFF8A80));
        if (clearTimer != null) clearTimer.stop();
        clearTimer = new javax.swing.Timer(3000, e -> messageLabel.setText(" "));
        clearTimer.setRepeats(false);
        clearTimer.start();
    }

    public ShopFrame() {
        setTitle("Curiosity Catalog");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inventory.setSlotClickListener(this::sellItem);

        BackgroundPanel bg = new BackgroundPanel("/shop_background.png");
        bg.setPreferredSize(new Dimension(1100, 860));

        for (int i = 0; i < 4; i++) {
            double[] a = AREAS[i];
            bg.addAt(makeTier(i), a[0], a[1], a[2], a[3]);
        }

        JLabel bigBadge = makePriceBadge(3);
        bigBadge.setFont(new Font("SansSerif", Font.BOLD, 26));
        bigBadge.setHorizontalAlignment(SwingConstants.CENTER);
        bigBadge.setBorder(null);
        bg.addAt(bigBadge, 0.477, 0.499, 0.100, 0.060);
        bg.setComponentZOrder(bigBadge, 0);

        JLabel title = new JLabel("CURIOSITY CATALOG", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(0xF0E8F8));
        bg.addAt(title, 0.10, 0.04, 0.38, 0.07);

        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(0x7FFFE0));
        balanceLabel.setOpaque(true);
        balanceLabel.setBackground(Color.BLACK);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateBalance();
        bg.addAt(balanceLabel, 0.02, 0.02, 0.12, 0.05);

        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(new Color(0x2B2631));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inventory, BorderLayout.CENTER);
        bottom.add(messageLabel, BorderLayout.SOUTH);

        add(bg, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel makePriceBadge(int t) {
        JLabel price = new JLabel(String.valueOf(PRICES[t]));
        price.setOpaque(true);
        price.setBackground(Color.BLACK);
        price.setForeground(new Color(0x7FFFE0));
        price.setFont(new Font("SansSerif", Font.BOLD, 20));
        price.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        return price;
    }

    private JPanel makeTier(int t) {
        JPanel box = new JPanel(new BorderLayout());
        box.setOpaque(false);

        JLabel tier = new JLabel("TIER " + (t + 1));
        tier.setFont(new Font("SansSerif", Font.BOLD, 15));
        tier.setForeground(TEXT[t]);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);
        if (t == 3) {
            header.add(Box.createHorizontalStrut(125));
        } else {
            header.add(makePriceBadge(t));
        }
        header.add(tier);

        JPanel grid = new JPanel(new GridLayout(0, COLUMNS[t], 5, 5));
        grid.setOpaque(false);
        for (String name : ITEMS[t]) {
            StoreItem item = new StoreItem(name, PRICES[t], this);
            storeItems.put(name, item);
            grid.add(item);
        }

        JPanel gridWrap = new JPanel(new BorderLayout());
        gridWrap.setOpaque(false);
        gridWrap.setBorder(BorderFactory.createEmptyBorder(8, 16, 10, 16));
        gridWrap.add(grid, BorderLayout.NORTH);

        box.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        box.add(header, BorderLayout.NORTH);
        box.add(gridWrap, BorderLayout.CENTER);
        return box;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShopFrame());
    }
}