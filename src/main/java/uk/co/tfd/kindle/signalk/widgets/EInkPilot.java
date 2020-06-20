package uk.co.tfd.kindle.signalk.widgets;

import uk.co.tfd.kindle.signalk.Data;
import uk.co.tfd.kindle.signalk.Util;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static uk.co.tfd.kindle.signalk.Data.DataValue;
import static uk.co.tfd.kindle.signalk.Data.PilotDataValue;

/**
 * Created by ieb on 09/06/2020.
 */
public class EInkPilot extends EInkTextBox {


    private String autoPilotState = "-";
    private String pilotHeading = "-";
    private DecimalFormat headingFormat = new DecimalFormat("0.0 \u00B0T");

    public EInkPilot(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits, Data.Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        Map<String, String> labels = new HashMap<>();
        labels.put("bl","Pilot");
        labels.put("br","deg");
        options.put("labels",labels);
        java.util.List<String> sources = new ArrayList<String>();
        sources.add(Data.DataKey.STEERING_AUTOPILOT.id);
        options.put("sources", sources);
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        if ( Data.DataKey.STEERING_AUTOPILOT.equals(data.key) &&
                data instanceof PilotDataValue ) {
            PilotDataValue pilotData = (PilotDataValue) data;
            String newPilotState = pilotData.getState();
            String newPilotHeading = this.displayUnits.toDispay(pilotData.getHeading(), headingFormat, Data.DataType.BEARING);;
            if ( !newPilotState.equals(autoPilotState) || !newPilotHeading.equals(pilotHeading)) {
                autoPilotState = newPilotState;
                pilotHeading = newPilotHeading;
                return true;
            }
            return false;
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.key+" expected "+Data.DataKey.STEERING_AUTOPILOT );
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        Util.drawString(this.autoPilotState, boxWidth / 2, boxHeight / 2, mediumFont, Util.HAlign.CENTER, Util.VAlign.BASELINE, g2);
        Util.drawString(this.pilotHeading, boxWidth / 2, boxHeight, mediumFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
    };
}
