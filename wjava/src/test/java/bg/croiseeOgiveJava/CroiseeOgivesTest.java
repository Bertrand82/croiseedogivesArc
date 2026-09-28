package bg.croiseeOgiveJava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CroiseeOgivesTest {

    private static final double EPSILON = 1e-9;

    @Test
    void laChainettePasseParLesTroisPoints() {
        double h = 2.5;
        double r = 2.0;
        Chainette chainette = new Chainette(h, r);

        assertEquals(h, chainette.y(0.0), EPSILON);
        assertEquals(0.0, chainette.y(-r), EPSILON);
        assertEquals(0.0, chainette.y(r), EPSILON);
    }

    @Test
    void laChainetteEstSymetrique() {
        Chainette chainette = new Chainette(2.5, 2.0);

        assertEquals(chainette.y(-1.0), chainette.y(1.0), EPSILON);
    }
}