package bg.croiseeOgiveJava;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelControlCourbe extends JPanel {

	ICourbe courbe ;
	JLabel labelTitre = new JLabel();
	JLabel labelCouleur = new JLabel(" x ");
	JCheckBox checkBoxDisplay = new JCheckBox("display");

	public PanelControlCourbe(ICourbe courbe) {
		this.courbe = courbe;
		labelTitre.setText(courbe.getTitre());
		
		labelCouleur.setOpaque(true);
		labelCouleur.setBackground(courbe.getColor());
		labelCouleur.setForeground(courbe.getColor()); // optionnel, si tu veux aussi changer le texte
		labelCouleur.setText(" x ");
		
		this.checkBoxDisplay.setSelected(courbe.isDisplay());
		this.checkBoxDisplay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				courbe.setDisplay(!courbe.isDisplay());
				PanelControlCourbe.this.getParent().getParent().repaint();
				
			}
		});
		this.add(labelCouleur);
		this.add(labelTitre);
		this.add(this.checkBoxDisplay);
	}

}
