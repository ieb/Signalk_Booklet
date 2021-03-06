package uk.co.tfd.kindle.signalk.widgets;

import uk.co.tfd.kindle.signalk.Data;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class EInkDepth extends EInkTextBox {

    public EInkDepth(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits, Data.Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        ((Map<String, String> )options.get("labels")).put("br", "m");
        options.put("withStats", true);
        options.put("dataFormat", new DecimalFormat("#0.00"));
        return options;
    }
    @Override
    public void onUpdate(Data.DataValue d) {
        if (Data.DataType.DEPTH.equals(d.key.type) ) {
            super.onUpdate(d);
        }
    }

}