package bg.croiseeOgiveJava;

public class MainCroiseeOgives {

	public static void main(String[] args) {
		double h = 2.5;
		double r = 2.0;
		Chainette chainette = new Chainette(h, r);
		CroiseeOgivesCanvas croiseeOgivesCanvas = new CroiseeOgivesCanvas(chainette);
	}

}
