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
            double cote = 400;
            double demiCote =cote/2;
            double epaisseur =20;
            QuartPoint quartPoint = new QuartPoint(demiCote);
            TiersPoint tiersPoints  = new TiersPoint(demiCote);
            ProjectionCroisee projectionCroisee = new ProjectionCroisee(demiCote);
            List<ICourbe> liste = new ArrayList<>();
            liste.add(new Chainette(projectionCroisee.getH(), demiCote,"1"));
            liste.add(new Chainette(projectionCroisee.getH()+20, demiCote+epaisseur,"2")); 
            liste.add(new Chainette(tiersPoints.getH(), demiCote,"3")); 
            liste.add(tiersPoints);
            liste.add(new TiersPointExtrados(tiersPoints, epaisseur));
            liste.add(quartPoint);
            liste.add(new QuartPointExtrados(quartPoint, epaisseur));
            liste.add(projectionCroisee);
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
