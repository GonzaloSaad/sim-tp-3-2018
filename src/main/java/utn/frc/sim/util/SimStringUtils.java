package utn.frc.sim.util;

import java.text.DecimalFormat;

public class SimStringUtils {

    public static String getDoubleStringFormat(double value, int places) {
        DecimalFormat df = new DecimalFormat(getDoubleFormatPatter(places));
        return df.format(MathUtils.round(value, places));

    }

    private static String getDoubleFormatPatter(int places) {
        StringBuilder sb = new StringBuilder("#0.");
        for (int i = 0; i < places; i++) {
            sb.append("0");
        }
        return sb.toString();
    }
}
