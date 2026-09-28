package bg.croiseeOgiveJava;
/**
 * 
 * 
 * https://fr.wikipedia.org/wiki/Cha%C3%AEnette
 */
/**
 * https://fr.wikipedia.org/wiki/Cha%C3%AEnette
 */
public class Chainette {

    private final double a;
    private final double x0;
    private final double y0;

    /**
     * Construit une chaînette symétrique passant par :
     * - (-r, 0)
     * - ( 0, h)
     * - ( r, 0)
     *
     * @param h hauteur au centre
     * @param r demi-distance horizontale entre les points d'ancrage
     */
    public Chainette(double h, double r) {
        this.x0 = 0.0;
        this.y0 = h - Math.sqrt(r * r + h * h); // pour que y(0) = h
        this.a = (r * r - h * h) / (2.0 * h);   // valide si h > 0
    }

    // y(x) = a * cosh((x - x0) / a) + y0
    public double y(double x) {
        return a * Math.cosh((x - x0) / a) + y0;
    }

    public double getA() {
        return a;
    }

    public double getX0() {
        return x0;
    }

    public double getY0() {
        return y0;
    }
}