package bg.croiseeOgiveJava;

import lombok.Getter;

/**
 * Chaînette (arc) symétrique passant par (-r, 0), (0, h) et (r, 0).
 * https://fr.wikipedia.org/wiki/Cha%C3%AEnette
 */
@Getter
public class Chainette {

    private final double h;
    private final double r;
    private final double a;

    public Chainette(double h, double r) {
       
        this.h = h;
        this.r = r;
        this.a = calculerA(h, r);
    }

    /** y(x) = h - a (cosh(x/a) - 1) */
    public double y(double x) {
        return h - a * (Math.cosh(x / a) - 1.0);
    }

    /**
     * Résout h = a (cosh(r/a) - 1).
     * Avec t = r/a : h/r = (cosh(t) - 1) / t, fonction croissante de t.
     */
    private static double calculerA(double h, double r) {
        double rapport = h / r;
        double bas = 0.0;
        double haut = 1.0;

        while (f(haut) < rapport) {
            haut *= 2.0;
            if (haut > 700) {
                throw new IllegalArgumentException("h/r trop grand");
            }
        }

        for (int i = 0; i < 200; i++) {
            double milieu = (bas + haut) / 2.0;
            if (f(milieu) < rapport) {
                bas = milieu;
            } else {
                haut = milieu;
            }
        }

        double t = (bas + haut) / 2.0;
        return r / t;
    }

    /** (cosh(t) - 1) / t, calculé de façon précise pour les petits t */
    private static double f(double t) {
        if (t == 0.0) {
            return 0.0;
        }
        double s = Math.sinh(t / 2.0);
        return 2.0 * s * s / t;
    }

    public static void main(String[] args) {
        Chainette c = new Chainette(100, 250);
        System.out.println("a     = " + c.getA());
        System.out.println("y(-r) = " + c.y(-250));
        System.out.println("y(0)  = " + c.y(0));
        System.out.println("y(r)  = " + c.y(250));
    }
}