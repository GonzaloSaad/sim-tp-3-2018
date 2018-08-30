package utn.frc.sim.util;

import java.text.DecimalFormat;

public class SimStringUtils {

    public static String getDoubleWithFourPlaces(double value){
        return getDoubleStringFormat(value, 4);
    }

    public static String getDoubleStringFormat(double value, int places) {
        DecimalFormat df = new DecimalFormat(getDoubleFormatPattern(places));
        return df.format(MathUtils.round(value, places));

    }

    private static String getDoubleFormatPattern(int places) {
        StringBuilder sb = new StringBuilder("#0.");
        for (int i = 0; i < places; i++) {
            sb.append("0");
        }
        return sb.toString();
    }
}
