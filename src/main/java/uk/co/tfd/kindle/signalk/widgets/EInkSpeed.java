package uk.co.tfd.kindle.signalk.widgets;

import uk.co.tfd.kindle.signalk.Data;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by ieb on 15/06/2020.
 */
public class EInkSpeed extends EInkTextBox {

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
    @Override
    public void onUpdate(Data.DataValue d) {
        if (Data.DataType.SPEED.equals(d.key.type) ) {
            super.onUpdate(d);
        }
    }

}
