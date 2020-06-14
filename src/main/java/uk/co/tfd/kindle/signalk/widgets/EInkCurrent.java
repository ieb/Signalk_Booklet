package uk.co.tfd.kindle.signalk.widgets;

import uk.co.tfd.kindle.signalk.Data;

import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static uk.co.tfd.kindle.signalk.Data.CurrentDataValue;
import static uk.co.tfd.kindle.signalk.Data.DataValue;

/**
 * Created by ieb on 10/06/2020.
 */
public class EInkCurrent extends EInkTextBox {


    private String drift = "-";
    private String set = "-";
    private DecimalFormat driftFormat = new DecimalFormat("0.0 Kn");
    private DecimalFormat setFormat = new DecimalFormat("0.0 \u00B0T");

    public EInkCurrent(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
        super(rotate, updateOptions(options), displayUnits );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        Map<String, String> labels = new HashMap<>();
        labels.put("bl","Current");
        options.put("labels",labels);
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        if (Data.DataKey.ENVIRONMENT_CURRENT.equals(data.key)) {
            if (data instanceof CurrentDataValue) {
                CurrentDataValue pilotData = (CurrentDataValue) data;
                String newDrift = this.displayUnits.toDispay(pilotData.getDrift(), driftFormat, Data.DataType.SPEED);
                String newSet = this.displayUnits.toDispay(pilotData.getSet(), setFormat, Data.DataType.BEARING);
                if (!newDrift.equals(drift) || !newSet.equals(set)) {
                    drift = newDrift;
                    set = newSet;
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.key+" expected "+Data.DataKey.ENVIRONMENT_CURRENT );
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        this.twoLineLeft(drift, set, g2);
    }


}
