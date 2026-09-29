package bg.croiseeOgiveJava;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainSwing {

	 /**
     * Exemple d'utilisation.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = new JFrame("Chaînette");

            QuartPoint quartPoint = new QuartPoint(100);
            TiersPoint tiersPoints  = new TiersPoint(100);
            List<ICourbe> liste = new ArrayList<>();
            liste.add(new Chainette(140, 100));
            liste.add(new Chainette(160, 120)); 
            liste.add(tiersPoints);
            liste.add(new TiersPointExtrados(tiersPoints, 20));
            liste.add(quartPoint);
            liste.add(new QuartPointExtrados(quartPoint, 20));
            liste.add(new ProjectionCroisee(100));
            CroiseeOgivesCanvas canvas =  new CroiseeOgivesCanvas(liste);

            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setContentPane(canvas);
            fenetre.pack();
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });
    }
}
