package uk.co.tfd.kindle.signalk.widgets;


import uk.co.tfd.kindle.signalk.Data;
import uk.co.tfd.kindle.signalk.Util;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.co.tfd.kindle.signalk.Data.AttitudeDataValue;
import static uk.co.tfd.kindle.signalk.Data.DataValue;

/**
 * Created by ieb on 10/06/2020.
 */
public class EInkAttitude extends EInkTextBox {


    private String pitch = "-";
    private String roll = "-";
    private DecimalFormat pitchDataFormat = new DecimalFormat("A0.0\u00B0;F0.0\u00B0");
    private DecimalFormat rollDataFormat = new DecimalFormat("S0.0\u00B0;P0.0\u00B0");

    public EInkAttitude(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits, Data.Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        options.put("dataType", Data.DataType.RELATIVEANGLE);
        List<String> sources = new ArrayList<String>();
        sources.add(Data.DataKey.NAVIGATION_ATTITUDE.id);
        options.put("sources", sources);
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        if (Data.DataKey.NAVIGATION_ATTITUDE.equals(data.key)) {
            if ( data instanceof AttitudeDataValue) {
                AttitudeDataValue attitudeData = (AttitudeDataValue) data;
                String newPitch = "pitch: " + this.displayUnits.toDispay(attitudeData.getPitch(), pitchDataFormat, Data.DataType.RELATIVEANGLE);
                String newRoll =  "roll : " + this.displayUnits.toDispay(attitudeData.getRoll(), rollDataFormat, Data.DataType.RELATIVEANGLE);
                if ( !newPitch.equals(pitch) || !newRoll.equals(roll)) {
                    pitch = newPitch;
                    roll = newRoll;
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.key+" expected "+Data.DataKey.NAVIGATION_ATTITUDE );
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        Util.drawString(pitch, boxWidth / 2, boxHeight / 2, normalFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        Util.drawString(roll, boxWidth / 2, boxHeight/2, normalFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
        this.drawBaseLine("attitude", "deg", g2);
    }


}
