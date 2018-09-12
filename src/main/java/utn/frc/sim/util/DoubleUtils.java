package utn.frc.sim.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DoubleUtils {

    private static final String INFINITY = "Infinity";
    private static final String NAN = "NaN";

    public static final String regex = "^[+-]?(([1-9]\\d*)|0)(\\.\\d+)?$";

    /**
     * Metodo que redondea un double en una
     * cierta cantidad de decimales.
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String roundString(double value, int places) {
        if (Double.isInfinite(value)) {
            return INFINITY;
        } else if (Double.isNaN(value)) {
            return NAN;
        } else {
            return getDoubleStringFormat(value, places);
        }
    }

    /**
     * Metodo que devuelve un double formateado en
     * string con cuatro decimales.
     */
    public static String getDoubleWithFourPlaces(double value) {
        return getDoubleStringFormat(value, 4);
    }

    /**
     * Metodo que formatea un double en un string
     * con una cierta cantidad de decimales.
     */
    private static String getDoubleStringFormat(double value, int places) {
        DecimalFormat df = new DecimalFormat(getDoubleFormatPattern(places));
        return df.format(round(value, places));

    }

    /**
     * Metodo que devuelte el patron de double
     * segun la cantidad de decimales.
     */
    private static String getDoubleFormatPattern(int places) {
        StringBuilder sb = new StringBuilder("#0.");
        for (int i = 0; i < places; i++) {
            sb.append("0");
        }
        return sb.toString();
    }
}
