package bg.croiseeOgiveJava;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainSwing {

	 /**
     * Exemple d'utilisation.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame jframe = new JFrame("Croisée d'ogives");

            QuartPoint quartPoint = new QuartPoint(100);
            TiersPoint tiersPoints  = new TiersPoint(100);
            List<ICourbe> liste = new ArrayList<>();
            liste.add(new Chainette(140, 100,"1"));
            liste.add(new Chainette(160, 120,"2")); 
            liste.add(new Chainette(tiersPoints.getH(), 100,"3")); 
            liste.add(tiersPoints);
            liste.add(new TiersPointExtrados(tiersPoints, 20));
            liste.add(quartPoint);
            liste.add(new QuartPointExtrados(quartPoint, 20));
            liste.add(new ProjectionCroisee(100));
            PanelCroiseeOgivesCanvas canvas =  new PanelCroiseeOgivesCanvas(liste);
            PanelControlGeneral panelControl = new PanelControlGeneral(liste);
            JPanel panelGlobal = new JPanel(new BorderLayout());
            panelGlobal.add(canvas, BorderLayout.CENTER);
            panelGlobal.add(panelControl,BorderLayout.WEST);
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jframe.setContentPane(panelGlobal);
            jframe.pack();
            jframe.setLocationRelativeTo(null);
            jframe.setVisible(true);
        });
    }
}
