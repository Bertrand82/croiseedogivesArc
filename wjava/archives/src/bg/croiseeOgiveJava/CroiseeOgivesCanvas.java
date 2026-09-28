package bg.croiseeOgiveJava;



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

/**
 * Composant Swing affichant une chaînette.
 */
public class CroiseeOgivesCanvas extends JPanel {

    private static final int MARGE = 50;
    private static final int NOMBRE_SEGMENTS = 500;

    private Chainette chainette;

    private Color couleurCourbe = new Color(40, 90, 180);
    private Color couleurAxes = new Color(130, 130, 130);
    private Color couleurPoints = new Color(190, 40, 40);

    public CroiseeOgivesCanvas(double h, double r) {
        this(new Chainette(h, r));
    }

    public CroiseeOgivesCanvas(Chainette chainette) {
        if (chainette == null) {
            throw new IllegalArgumentException(
                    "La chaînette ne peut pas être null."
            );
        }

        this.chainette = chainette;

        setPreferredSize(new Dimension(800, 500));
        setBackground(Color.WHITE);
    }

    public void setChainette(Chainette chainette) {
        if (chainette == null) {
            throw new IllegalArgumentException(
                    "La chaînette ne peut pas être null."
            );
        }

        this.chainette = chainette;
        repaint();
    }

    public Chainette getChainette() {
        return chainette;
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

            dessinerChainette(g2);
        } finally {
            g2.dispose();
        }
    }

    private void dessinerChainette(Graphics2D g2) {
        double r = chainette.getR();
        double h = chainette.getH();

        int largeurDisponible = getWidth() - 2 * MARGE;
        int hauteurDisponible = getHeight() - 2 * MARGE;

        if (largeurDisponible <= 0 || hauteurDisponible <= 0) {
            return;
        }

        /*
         * L'échelle est identique en X et Y afin de ne pas
         * déformer la chaînette.
         */
        double echelleX = largeurDisponible / (2.0 * r);
        double echelleY = hauteurDisponible / h;
        double echelle = Math.min(echelleX, echelleY);

        double largeurCourbe = 2.0 * r * echelle;
        double hauteurCourbe = h * echelle;

        double origineX = (getWidth() - largeurCourbe) / 2.0;
        double origineY = (getHeight() - hauteurCourbe) / 2.0;

        dessinerAxes(g2, origineX, origineY, largeurCourbe, hauteurCourbe);
        dessinerCourbe(g2, origineX, origineY, echelle);
        dessinerPointsRemarquables(g2, origineX, origineY, echelle);
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

        // Ligne passant par les deux points d'ancrage.
        int yAncrage = (int) Math.round(origineY);

        g2.drawLine(
                (int) Math.round(origineX),
                yAncrage,
                (int) Math.round(origineX + largeurCourbe),
                yAncrage
        );

        // Axe vertical passant par x = 0.
        int xCentre = (int) Math.round(origineX + largeurCourbe / 2.0);

        g2.drawLine(
                xCentre,
                (int) Math.round(origineY),
                xCentre,
                (int) Math.round(origineY + hauteurCourbe)
        );
    }

    private void dessinerCourbe(
            Graphics2D g2,
            double origineX,
            double origineY,
            double echelle) {

        double r = chainette.getR();

        Path2D.Double chemin = new Path2D.Double();

        for (int i = 0; i <= NOMBRE_SEGMENTS; i++) {
            double proportion = i / (double) NOMBRE_SEGMENTS;
            double x = -r + proportion * 2.0 * r;
            double y = chainette.y(x);

            double ecranX = origineX + (x + r) * echelle;

            /*
             * En Swing, l'axe Y est dirigé vers le bas.
             * Le point (0, h) apparaît donc sous les ancrages.
             */
            double ecranY = origineY + y * echelle;

            if (i == 0) {
                chemin.moveTo(ecranX, ecranY);
            } else {
                chemin.lineTo(ecranX, ecranY);
            }
        }

        g2.setColor(couleurCourbe);
        g2.setStroke(
                new BasicStroke(
                        3.0f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                )
        );
        g2.draw(chemin);
    }

    private void dessinerPointsRemarquables(
            Graphics2D g2,
            double origineX,
            double origineY,
            double echelle) {

        double r = chainette.getR();
        double h = chainette.getH();

        dessinerPoint(
                g2,
                origineX,
                origineY,
                "(-r, 0)"
        );

        dessinerPoint(
                g2,
                origineX + r * echelle,
                origineY + h * echelle,
                "(0, h)"
        );

        dessinerPoint(
                g2,
                origineX + 2.0 * r * echelle,
                origineY,
                "(r, 0)"
        );
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
        g2.fillOval(
                ecranX - rayon,
                ecranY - rayon,
                2 * rayon,
                2 * rayon
        );

        g2.drawString(texte, ecranX + 8, ecranY - 8);
    }

    private void dessinerInformations(Graphics2D g2) {
        String texte = String.format(
                "h = %.2f   r = %.2f   a = %.4f",
                chainette.getH(),
                chainette.getR(),
                chainette.getA()
        );

        FontMetrics metriques = g2.getFontMetrics();
        int x = (getWidth() - metriques.stringWidth(texte)) / 2;
        int y = getHeight() - 15;

        g2.setColor(Color.DARK_GRAY);
        g2.drawString(texte, x, y);
    }

    public void setCouleurCourbe(Color couleurCourbe) {
        this.couleurCourbe = couleurCourbe;
        repaint();
    }

    /**
     * Exemple d'utilisation.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = new JFrame("Chaînette");

            CroiseeOgivesCanvas canvas =
                    new CroiseeOgivesCanvas(100.0, 250.0);

            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setContentPane(canvas);
            fenetre.pack();
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });
    }
}