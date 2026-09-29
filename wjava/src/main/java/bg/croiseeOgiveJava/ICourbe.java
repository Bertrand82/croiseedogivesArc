package bg.croiseeOgiveJava;

import java.awt.Color;

public interface ICourbe {

	
	 public Double y(double x);
	 
	 public double getR();
	 public double getH();

	 public Color getColor();

	 public String getTitre();

	 public boolean isControlable();

	 public boolean isDisplay();

	 public void setDisplay(boolean b);
}
