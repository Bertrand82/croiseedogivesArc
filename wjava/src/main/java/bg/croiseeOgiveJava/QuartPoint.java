package bg.croiseeOgiveJava;

import java.awt.Color;

import lombok.Getter;

@Getter
public class QuartPoint implements ICourbe{

	double r;
	double h;
	double xCentre ;
	double rCentre ;
	Color color = Color.GREEN;


	public QuartPoint(double r) {
		this.r = r;
		
		 xCentre = (r/2.);
		 rCentre =r+xCentre;
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

}
