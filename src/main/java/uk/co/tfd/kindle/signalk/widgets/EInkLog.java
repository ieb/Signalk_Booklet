package uk.co.tfd.kindle.signalk.widgets;

/**
 * Created by ieb on 09/06/2020.
 */

import uk.co.tfd.kindle.signalk.Data;

import static uk.co.tfd.kindle.signalk.Data.*;



import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by ieb on 09/06/2020.
 */
public class EInkLog extends EInkTextBox {


    private String trip = "-.-";
    private String log = "-.-";
    private DecimalFormat tripFormat = new DecimalFormat("0.#");
    private DecimalFormat logFormat = new DecimalFormat("0.#");

    public EInkLog(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
        super(rotate, updateOptions(options), displayUnits );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        Map<String, String> labels = new HashMap<>();
        labels.put("bl","Log");
        labels.put("br","Nm");
        options.put("labels",labels);
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        String newTrip = trip;
        String newLog = log;
        if ( DataKey.NAVIGATION_LOG.equals(data.key)) {
            newLog = this.displayUnits.toDispay(data.getValue(), logFormat, DataType.DISTANCE);
        } else if ( DataKey.NAVIGATION_TRIP_LOG.equals(data.key)) {
            newTrip = this.displayUnits.toDispay(data.getValue(), tripFormat, DataType.DISTANCE);
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.key+" expected one of "+Data.DataKey.NAVIGATION_LOG+","+DataKey.NAVIGATION_TRIP_LOG );
        }
        if ( !newTrip.equals(trip) || !newLog.equals(log)) {
            trip = newTrip;
            log = newLog;
            return true;
        }
        return false;
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        this.twoLineLeft(trip, log, g2);
    }


}
