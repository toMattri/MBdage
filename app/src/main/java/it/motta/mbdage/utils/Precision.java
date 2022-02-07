package it.motta.mbdage.utils;

import java.math.BigDecimal;

public class Precision {

    public static double round(double x, int scale, int roundingMethod) {
        try {
            String value = String.format("%f",x).replace(",",".");
            BigDecimal bigDecimal = new BigDecimal(value);
            bigDecimal = bigDecimal.setScale(scale,roundingMethod);
            double rounded = Double.parseDouble(bigDecimal.toString());
            return rounded == 0D ? 0D * x : rounded;
        } catch (NumberFormatException ex) {
            if (Double.isInfinite(x))
                return x;
             else
                return Double.NaN;
        }
    }
}
