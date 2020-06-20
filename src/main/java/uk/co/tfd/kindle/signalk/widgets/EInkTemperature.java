package uk.co.tfd.kindle.signalk.widgets;

import uk.co.tfd.kindle.signalk.Data;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by ieb on 15/06/2020.
 */
public class EInkTemperature extends EInkTextBox {

    public EInkTemperature(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits, Data.Store store) {
        super(rotate, updateOptions(options), displayUnits, store);
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        ((Map<String, String> )options.get("labels")).put("br", "C");
        options.put("withStats", true);
        options.put("dataFormat", new DecimalFormat("#0.0"));
        return options;
    }

    @Override
    public void onUpdate(Data.DataValue d) {
        if (Data.DataType.TEMPERATURE.equals(d.key.type) ) {
            super.onUpdate(d);
        }
    }

    boolean formatOutput(Data.DataValue data) {

        String out = this.displayUnits.toDispay(data.getValue(), dataFormat, Data.DataType.TEMPERATURE);
        String outmax = this.displayUnits.toDispay(data.getMax(), dataFormat, Data.DataType.TEMPERATURE);
        String outmin = this.displayUnits.toDispay(data.getMin(), dataFormat, Data.DataType.TEMPERATURE);
        String outmean = "\u03BC " + this.displayUnits.toDispay(data.getMean(), dataFormat, Data.DataType.TEMPERATURE);
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
