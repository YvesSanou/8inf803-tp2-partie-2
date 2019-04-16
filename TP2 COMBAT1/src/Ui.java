import Monstres.Monstre;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Ui extends JFrame {

    private JFrame f;
    private Dessin p;
    private static Ui instance=null;



    public static Ui Instance(){
        if (instance == null){
            instance = new Ui();
            return instance;
        }else{
            return instance;
        }
    }
    public Ui() {
        f = new JFrame("TP Partie 2");
        p = new Dessin();
        p.setBackground(Color.white);
        f.add(p);

        f.setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }


    public void printMonsters(Monstre l){
        p.addAndrefresh(l);
    }


}
