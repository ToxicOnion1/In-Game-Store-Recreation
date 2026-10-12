package com.mycompany.deza;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BackgroundPanel extends JPanel {

    private Image image;
    private final Map<Component, double[]> areas = new HashMap<>();

    public BackgroundPanel(String path) {
        setLayout(null);
        try {
            image = ImageIO.read(getClass().getResource(path));
        } catch (Exception e) {
            image = null;
        }
    }

    public void addAt(Component c, double fx, double fy, double fw, double fh) {
        areas.put(c, new double[]{fx, fy, fw, fh});
        add(c);
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        for (Map.Entry<Component, double[]> e : areas.entrySet()) {
            double[] a = e.getValue();
            e.getKey().setBounds((int) (a[0] * w), (int) (a[1] * h),
                                 (int) (a[2] * w), (int) (a[3] * h));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}