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
public class PanelCroiseeOgivesCanvas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MARGE_ = 50;
	private static final int NOMBRE_SEGMENTS = 500;

	private final List<ICourbe> courbes;

	private final Color couleurCourbe = new Color(40, 90, 180);
	private Color couleurAxes = new Color(130, 130, 130);
	private Color couleurPoints = new Color(190, 40, 40);

	public PanelCroiseeOgivesCanvas() {
		this(new ArrayList<>());
	}

	public PanelCroiseeOgivesCanvas(List<ICourbe> chainettes) {
		if (chainettes == null) {
			throw new IllegalArgumentException("La liste de chaînettes ne peut pas être null.");
		}

		this.courbes = new ArrayList<>(chainettes);
		setPreferredSize(new Dimension(800, 500));
		setBackground(Color.WHITE);
	}

	public void addChainette(Chainette chainette) {

		this.courbes.add(chainette);
		repaint();
	}

	public void clearChainettes() {
		this.courbes.clear();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			dessinerTout(g2);
		} finally {
			g2.dispose();
		}
	}

	private void dessinerTout(Graphics2D g2) {
		if (courbes.isEmpty()) {
			g2.setColor(Color.DARK_GRAY);
			g2.drawString("Aucune chaînette à afficher", 20, 20);
			return;
		}

		// Calcul des bornes globales
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

		for (ICourbe c : courbes) {
			double r = c.getR();
			double h = c.getH();

			minX = Math.min(minX, -r);
			maxX = Math.max(maxX, r);
			minY = Math.min(minY, 0.0);
			maxY = Math.max(maxY, h);
		}

		int largeurDisponible = getWidth() - 2 * MARGE_;
		int hauteurDisponible = getHeight() - 4 * MARGE_;

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
		double origineY = (getHeight() - hauteurCourbe) / 2.0 - MARGE_ - 20;

		dessinerAxes(g2, origineX, origineY, largeurCourbe, hauteurCourbe);
		dessinerCourbes(g2, origineX, origineY, minX, maxY, echelle);
		dessinerInformations(g2);
	}

	private void dessinerAxes(Graphics2D g2, double origineX, double origineY, double largeurCourbe,
			double hauteurCourbe) {

		g2.setColor(couleurAxes);
		g2.setStroke(new BasicStroke(1.0f));

		// Axe horizontal y = 0
		int yZero = (int) Math.round(origineY + hauteurCourbe);
		g2.drawLine((int) Math.round(origineX), yZero, (int) Math.round(origineX + largeurCourbe), yZero);

		// Axe vertical x = 0
		int xZero = (int) Math.round(origineX - minXToScreenOffset());
		g2.drawLine(xZero, (int) Math.round(origineY), xZero,
				(int) Math.round(origineY + hauteurCourbe + 2 * MARGE_ + 50));
	}

	// Ici on garde simple : x=0 au centre de la zone globale si les courbes sont
	// symétriques.
	private double minXToScreenOffset() {
		return 0.0;
	}

	private void dessinerCourbes(Graphics2D g2, double origineX, double origineY, double minX, double maxY,
			double echelle) {

		g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (ICourbe courbe : courbes) {
			if (courbe.isDisplay()) {
				g2.setColor(courbe.getColor());

				double r = courbe.getR();
				double h = courbe.getH();

				Path2D.Double chemin = new Path2D.Double();
				double kk = 1.2;
				boolean init = true;
				for (int i = 0; i <= NOMBRE_SEGMENTS; i++) {
					double proportion = i / (double) NOMBRE_SEGMENTS;
					double x = -kk * r + proportion * (2.0 * kk * r);
					Double y = courbe.y(x);
					if (y != null) {
						double ecranX = origineX + (x - minX) * echelle;
						double ecranY = origineY + (maxY - y) * echelle;

						if (init) {
							chemin.moveTo(ecranX, ecranY);
							init = false;
						} else {
							chemin.lineTo(ecranX, ecranY);
						}
					}
				}

				g2.draw(chemin);

				// Points remarquables
				dessinerPoint(g2, origineX + (-r - minX) * echelle, origineY + (maxY - 0.0) * echelle, "(" + r + ")");
				dessinerPoint(g2, origineX + (r - minX) * echelle, origineY + (maxY - 0.0) * echelle, "");
				
				dessinerPoint(g2, origineX + (0.0 - minX) * echelle, origineY + (maxY - h) * echelle, "(0, " + h + ")");
				if (courbe.getCenter() != null) {
					g2.setColor(courbe.getColor());
					dessinerPoint(g2, origineX + (courbe.getCenter()- minX) * echelle, origineY + (maxY - 0.0) * echelle, "Centre");
					dessinerPoint(g2, origineX + (-courbe.getCenter()- minX) * echelle, origineY + (maxY - 0.0) * echelle, "Centre");
									
				}else {
					dessinerPoint(g2, origineX + (-0 - minX) * echelle, origineY + (maxY - 0.0) * echelle, "");

				}
			}
		}
	}

	private void dessinerPoint(Graphics2D g2, double x, double y, String texte) {

		int rayon = 5;
		int ecranX = (int) Math.round(x);
		int ecranY = (int) Math.round(y);

		//g2.setColor(couleurPoints);
		g2.fillOval(ecranX - rayon, ecranY - rayon, 2 * rayon, 2 * rayon);
		g2.drawString(texte, ecranX + 8, ecranY - 8);
	}

	private void dessinerInformations(Graphics2D g2) {
		String texte = "Nombre de chaînettes : " + courbes.size();

		FontMetrics metriques = g2.getFontMetrics();
		int x = (getWidth() - metriques.stringWidth(texte)) / 2;
		int y = getHeight() - 15;

		g2.setColor(Color.DARK_GRAY);
		g2.drawString(texte, x, y);
	}

	// Test rapide
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			List<ICourbe> liste = new ArrayList<>();
			liste.add(new Chainette(100, 250,"1"));
			liste.add(new Chainette(60, 150,"2"));
			liste.add(new Chainette(40, 100,"3"));

			JFrame fenetre = new JFrame("Chaînettes");
			fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			fenetre.setContentPane(new PanelCroiseeOgivesCanvas(liste));
			fenetre.pack();
			fenetre.setLocationRelativeTo(null);
			fenetre.setVisible(true);
		});
	}
}