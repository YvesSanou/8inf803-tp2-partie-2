import Monstres.Monstre;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Dessin extends JPanel {

    private ArrayList<Monstres.Monstre> users;
    private int pad = 20;
    private double maxX = 1;
    private double maxY = 1;


    public Dessin() {
        super();
        users = new ArrayList<>();

    }

    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < users.size(); i++) {

            drawnode(g, users.get(i), Color.black);

        }


    }


    private void drawnode(Graphics g, Monstres.Monstre n, Color color) {
        double posX = getPixelX(n);
        double posY = getPixelY(n);
        g.setColor(color);
        g.fillOval((int) posX, (int) posY, 8, 8);
        //g.drawString(n.getNName(),  posX,  posY);
        g.setColor(Color.red);
        // g.drawString(Integer.toString(n.getScore()),  posX+8,  posY+8);
        g.setColor(Color.black);

    }

    private double getPixelX(Monstres.Monstre n) {
        double posX = (getSize().width - 4 * pad) * n.position()[0]/ (maxX);
        return posX + pad;

    }

    private double getPixelY(Monstres.Monstre n) {
        double posY = (getSize().height - 4 * pad) * n.position()[1] / (maxY);
        return pad + posY;

    }

    public void addAndrefresh(Monstres.Monstre n) {
        if (n ==null){
            users = new ArrayList<>();
        }else {
            users.add(n);
            maxX = Collections.max(users, Comparator.comparing(a -> a.position()[0])).position()[0];
            maxY = Collections.max(users, Comparator.comparing(a -> a.position()[1])).position()[1];
        }
        repaint();

    }

}
