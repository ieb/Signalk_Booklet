package uk.co.tfd.kindle.signalk.widgets;


import uk.co.tfd.kindle.signalk.Data;

import java.text.DecimalFormat;
import java.util.Map;

import static uk.co.tfd.kindle.signalk.Data.DataValue;

/**
 * Created by ieb on 10/06/2020.
 */
public class EInkWidgets {


    public static class EInkRelativeAngle extends EInkTextBox {

        public EInkRelativeAngle(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
            super(rotate, updateOptions(options), displayUnits );
        }

        private static Map<String, Object> updateOptions(Map<String, Object> options) {
            ((Map<String, String> )options.get("labels")).put("br", "deg");
            options.put("dataType", Data.DataType.RELATIVEANGLE);
            options.put("withStats", true);
            options.put("dataFormat", new DecimalFormat("S #0.0 \u00B0; #0.0 \u00B0 P"));
            return options;
        }

    }

    public static class EInkSpeed extends EInkTextBox {

        public EInkSpeed(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
            super(rotate, updateOptions(options), displayUnits );
        }

        private static Map<String, Object> updateOptions(Map<String, Object> options) {
            ((Map<String, String> )options.get("labels")).put("br", "kn");
            options.put("dataType", Data.DataType.SPEED);
            options.put("withStats", true);
            options.put("dataFormat", new DecimalFormat("#0.0"));
            return options;
        }

    }

    public static class EInkDistance extends EInkTextBox {

        public EInkDistance(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
            super(rotate, updateOptions(options), displayUnits );
        }

        private static Map<String, Object> updateOptions(Map<String, Object> options) {
            ((Map<String, String> )options.get("labels")).put("br", "m");
            options.put("dataType", Data.DataType.DISTANCE);
            options.put("withStats", true);
            options.put("dataFormat", new DecimalFormat("#0.00"));
            return options;
        }

    }

    public static class EInkBearing extends EInkTextBox {

        public EInkBearing(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
            super(rotate, updateOptions(options), displayUnits );
        }

        private static Map<String, Object> updateOptions(Map<String, Object> options) {
            ((Map<String, String> )options.get("labels")).put("br", "deg");
            options.put("dataType", Data.DataType.BEARING);
            options.put("withStats", true);
            options.put("dataFormat", new DecimalFormat("0"));
            return options;
        }

    }

    public static class EInkTemperature extends EInkTextBox {

        public EInkTemperature(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
            super(rotate, updateOptions(options), displayUnits );
        }

        private static Map<String, Object> updateOptions(Map<String, Object> options) {
            ((Map<String, String> )options.get("labels")).put("br", "C");
            options.put("dataType", Data.DataType.TEMPERATURE);
            options.put("withStats", true);
            options.put("dataFormat", new DecimalFormat("#0.0"));
            return options;
        }

        boolean formatOutput(DataValue data) {

            String out = this.displayUnits.toDispay(data.getValue(), dataFormat, dataType);
            String outmax = this.displayUnits.toDispay(data.getMax(), dataFormat, dataType);
            String outmin = this.displayUnits.toDispay(data.getMin(), dataFormat, dataType);
            String outmean = "\u03BC " + this.displayUnits.toDispay(data.getMean(), dataFormat, dataType);
            String outstdev = "\u03C3 " + this.displayUnits.toDispay(data.getStdev(), dataFormat, Data.DataType.NONE);
            if (!out.equals(this.out) ||
                    !outmax.equals(this.outmax) ||
                    !outmin.equals(this.outmin) ||
                    !outmean.equals(this.outmean) ||
                    !outstdev.equals(this.outstdev)) {
                this.out = out;
                this.outmax = outmax;
                this.outmin = outmin;
                this.outmean = outmean;
                this.outstdev = outstdev;
                return true;
            }
            return false;
        }
    }


    public static class EInkRatio extends EInkTextBox {

        public EInkRatio(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
            super(rotate, updateOptions(options), displayUnits );
        }

        private static Map<String, Object> updateOptions(Map<String, Object> options) {
            ((Map<String, String>) options.get("labels")).put("br", "%");
            options.put("dataType", Data.DataType.PERCENTAGE);
            options.put("withStats", true);
            options.put("dataFormat", new DecimalFormat("#0.0"));
            return options;
        }

    }


}
