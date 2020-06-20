package uk.co.tfd.kindle.signalk.widgets;

import uk.co.tfd.kindle.signalk.Data;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by ieb on 15/06/2020.
 */
public class EInkRelativeAngle extends EInkTextBox {

    public EInkRelativeAngle(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits, Data.Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        ((Map<String, String> )options.get("labels")).put("br", "deg");
        options.put("withStats", true);
        options.put("dataFormat", new DecimalFormat("S#0.0\u00B0;P#0.0\u00B0"));
        return options;
    }
    @Override
    public void onUpdate(Data.DataValue d) {
        if (Data.DataType.RELATIVEANGLE.equals(d.key.type) ) {
            super.onUpdate(d);
        }
    }
}
