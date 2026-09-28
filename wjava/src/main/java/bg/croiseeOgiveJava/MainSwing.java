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

            List<ICourbe> liste = new ArrayList<>();
            liste.add(new Chainette(140, 100));
            liste.add(new Chainette(160, 120)); 
            liste.add(new TiersPoint(100));
            liste.add(new TiersPoint(120));
            liste.add(new ProjectionCroisee(100.));
            liste.add(new ProjectionCroisee(120.));
            
            CroiseeOgivesCanvas canvas =
                    new CroiseeOgivesCanvas(liste);

            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setContentPane(canvas);
            fenetre.pack();
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });
    }
}
