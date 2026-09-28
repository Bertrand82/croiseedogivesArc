package bg.croiseeOgiveJava;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant Swing affichant une liste de chaînettes.
 */
public class CroiseeOgivesCanvas extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MARGE = 50;
    private static final int NOMBRE_SEGMENTS = 500;

    private final List<Chainette> chainettes;

    private  final Color couleurCourbe = new Color(40, 90, 180);
    private Color couleurAxes = new Color(130, 130, 130);
    private Color couleurPoints = new Color(190, 40, 40);

    public CroiseeOgivesCanvas() {
        this(new ArrayList<>());
    }

    public CroiseeOgivesCanvas(List<Chainette> chainettes) {
        if (chainettes == null) {
            throw new IllegalArgumentException("La liste de chaînettes ne peut pas être null.");
        }

        this.chainettes = new ArrayList<>(chainettes);
        setPreferredSize(new Dimension(800, 500));
        setBackground(Color.WHITE);
    }

    public void addChainette(Chainette chainette) {
        if (chainette == null) {
            throw new IllegalArgumentException("La chaînette ne peut pas être null.");
        }

        this.chainettes.add(chainette);
        repaint();
    }

    public void clearChainettes() {
        this.chainettes.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            dessinerTout(g2);
        } finally {
            g2.dispose();
        }
    }

    private void dessinerTout(Graphics2D g2) {
        if (chainettes.isEmpty()) {
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Aucune chaînette à afficher", 20, 20);
            return;
        }

        // Calcul des bornes globales
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Chainette c : chainettes) {
            double r = c.getR();
            double h = c.getH();

            minX = Math.min(minX, -r);
            maxX = Math.max(maxX, r);
            minY = Math.min(minY, 0.0);
            maxY = Math.max(maxY, h);
        }

        int largeurDisponible = getWidth() - 2 * MARGE;
        int hauteurDisponible = getHeight() - 2 * MARGE;

        if (largeurDisponible <= 0 || hauteurDisponible <= 0) {
            return;
        }

        double largeurMonde = maxX - minX;
        double hauteurMonde = maxY - minY;

        double echelleX = largeurDisponible / largeurMonde;
        double echelleY = hauteurDisponible / hauteurMonde;
        double echelle = Math.min(echelleX, echelleY);

        double largeurCourbe = largeurMonde * echelle;
        double hauteurCourbe = hauteurMonde * echelle;

        double origineX = (getWidth() - largeurCourbe) / 2.0;
        double origineY = (getHeight() - hauteurCourbe) / 2.0;

        dessinerAxes(g2, origineX, origineY, largeurCourbe, hauteurCourbe);
        dessinerChainettes(g2, origineX, origineY, minX, maxY, echelle);
        dessinerInformations(g2);
    }

    private void dessinerAxes(
            Graphics2D g2,
            double origineX,
            double origineY,
            double largeurCourbe,
            double hauteurCourbe) {

        g2.setColor(couleurAxes);
        g2.setStroke(new BasicStroke(1.0f));

        // Axe horizontal y = 0
        int yZero = (int) Math.round(origineY + hauteurCourbe);
        g2.drawLine(
                (int) Math.round(origineX),
                yZero,
                (int) Math.round(origineX + largeurCourbe),
                yZero
        );

        // Axe vertical x = 0
        int xZero = (int) Math.round(origineX - minXToScreenOffset());
        g2.drawLine(
                xZero,
                (int) Math.round(origineY),
                xZero,
                (int) Math.round(origineY + hauteurCourbe)
        );
    }

    // Ici on garde simple : x=0 au centre de la zone globale si les courbes sont symétriques.
    private double minXToScreenOffset() {
        return 0.0;
    }

    private void dessinerChainettes(
            Graphics2D g2,
            double origineX,
            double origineY,
            double minX,
            double maxY,
            double echelle) {

        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (Chainette chainette : chainettes) {
            g2.setColor(couleurCourbe);
            
            double r = chainette.getR();
            double h = chainette.getH();

            Path2D.Double chemin = new Path2D.Double();

            for (int i = 0; i <= NOMBRE_SEGMENTS; i++) {
                double proportion = i / (double) NOMBRE_SEGMENTS;
                double x = -2*r + proportion * (2.0 * 2 * r);
                double y = chainette.y(x);

                double ecranX = origineX + (x - minX) * echelle;
                double ecranY = origineY + (maxY - y) * echelle;

                if (i == 0) {
                    chemin.moveTo(ecranX, ecranY);
                } else {
                    chemin.lineTo(ecranX, ecranY);
                }
            }

            g2.draw(chemin);

            // Points remarquables
            dessinerPoint(g2, origineX + (-0 - minX) * echelle, origineY + (maxY - 0.0) * echelle, "");
            dessinerPoint(g2, origineX + (-r - minX) * echelle, origineY + (maxY - 0.0) * echelle, "("+r+")");
             dessinerPoint(g2, origineX + (0.0 - minX) * echelle, origineY + (maxY - h) * echelle, "(0, "+h+")");
            dessinerPoint(g2, origineX + (r - minX) * echelle, origineY + (maxY - 0.0) * echelle, "("+r+")");
        }
    }

    private void dessinerPoint(
            Graphics2D g2,
            double x,
            double y,
            String texte) {

        int rayon = 5;
        int ecranX = (int) Math.round(x);
        int ecranY = (int) Math.round(y);

        g2.setColor(couleurPoints);
        g2.fillOval(ecranX - rayon, ecranY - rayon, 2 * rayon, 2 * rayon);
        g2.drawString(texte, ecranX + 8, ecranY - 8);
    }

    private void dessinerInformations(Graphics2D g2) {
        String texte = "Nombre de chaînettes : " + chainettes.size();

        FontMetrics metriques = g2.getFontMetrics();
        int x = (getWidth() - metriques.stringWidth(texte)) / 2;
        int y = getHeight() - 15;

        g2.setColor(Color.DARK_GRAY);
        g2.drawString(texte, x, y);
    }

  

    // Test rapide
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Chainette> liste = new ArrayList<>();
            liste.add(new Chainette(100, 250));
            liste.add(new Chainette(60, 150));
            liste.add(new Chainette(40, 100));

            JFrame fenetre = new JFrame("Chaînettes");
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setContentPane(new CroiseeOgivesCanvas(liste));
            fenetre.pack();
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });
    }
}