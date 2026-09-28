package bg.croiseeOgiveJava;

import java.awt.Color;

import lombok.Getter;

@Getter
public class ProjectionCroisee implements ICourbe{

	final double r;
	final double cote;
	final double diagonale;
	final double rayon ;
	final double sinus45 = Math.sin(Math.PI/4);
	final double h;
	
	Color color = Color.BLUE;


	public ProjectionCroisee(double r) {
		this.r = r;
		this.cote = 2*r;
		this.diagonale = Math.sqrt(cote*cote +cote*cote);
		this.rayon = this.diagonale/2;
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
			double xx = x/sinus45;
			double yy2 = rayon*rayon-xx*xx;
			if(yy2 < 0 ) {
				System.err.println(" y2 <0  x : "+x+"  xx :"+xx+"   rayon "+rayon);
				y=0.;
			}else {
				y = Math.sqrt(yy2);
			}
		}
		System.out.println(" x  : "+x +"  y : "+y);
		return y;
	}

}
