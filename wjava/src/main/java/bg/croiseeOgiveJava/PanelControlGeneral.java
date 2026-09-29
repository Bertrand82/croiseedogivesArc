package bg.croiseeOgiveJava;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelControlGeneral extends JPanel {

	
	private static final long serialVersionUID = 1L;
	JLabel labelTitle = new JLabel("Control");
	
	public PanelControlGeneral( List<ICourbe> liste) {
		this.setLayout(new GridLayout(0, 1));
		this.add(labelTitle);
		
		for(ICourbe courbe : liste) {
			if (courbe.isControlable()) {
			PanelControlCourbe panelCourbe = new PanelControlCourbe(courbe);
			this.add(panelCourbe);
			}
		}
	}
	
}
