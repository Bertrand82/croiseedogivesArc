package bg.croiseeOgiveJava;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuartPointExtrados implements ICourbe{

	QuartPoint quartPoint;
	Color color = Color.GREEN;
	double epaisseur;
	double h ;
	double r;
	double xCentre;
	double rCentre;
	private final String titre="QuartPoint";
	boolean controlable= false;
	

	public QuartPointExtrados(QuartPoint quartPoint, double epaisseur) {
		this.quartPoint = quartPoint;
		this.epaisseur = epaisseur;
		this.color = quartPoint.getColor();
		
		
		 this.r = quartPoint.getR()+epaisseur;
		 this.xCentre = quartPoint.getXCentre();
		 this.rCentre = quartPoint.getRCentre()+epaisseur;
		 this.h = y(0);
	}

	public Double y(double x) {
		Double y;
		if (x < -r ) {
			y = null;
		}else if (x > r) {
			y = null;
		}else {
			if (x < 0) {
				x =-x;
			}
			double xx = xCentre + x;
			double yy2 = rCentre*rCentre-xx*xx;
			y = Math.sqrt(yy2);
		}
		return y;
	}

	@Override
	public boolean isDisplay() {
	
		return quartPoint.isDisplay();
	}

	@Override
	public void setDisplay(boolean b) {
		this.quartPoint.setDisplay(b);
		
	}

	@Override
	public Double getCenter() {
		return null;
	}

}
