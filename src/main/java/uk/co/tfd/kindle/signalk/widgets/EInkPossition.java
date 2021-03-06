package uk.co.tfd.kindle.signalk.widgets;

import uk.co.tfd.kindle.signalk.Data;
import uk.co.tfd.kindle.signalk.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import static uk.co.tfd.kindle.signalk.Data.DataValue;
import static uk.co.tfd.kindle.signalk.Data.PossitionDataValue;

/**
 * Created by ieb on 10/06/2020.
 */

public class EInkPossition extends EInkTextBox {


    private String longitude = "---\u00B0--.---\u2032W";
    private String latitude = "--\u00B0--.---\u2032N";
    private String date = "-";


    public EInkPossition(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits, Data.Store store) {
        super(rotate, updateOptions(options), displayUnits, store);
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        java.util.List<String> sources = new ArrayList<String>();
        sources.add(Data.DataKey.NAVIGATION_POSITION.id);
        sources.add(Data.DataKey.NAVIGATION_DATETIME.id);
        options.put("sources", sources);

        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        String newLongitude = longitude;
        String newLatitude = latitude;
        String newDate = date;
        if (Data.DataKey.NAVIGATION_POSITION.equals(data.key) &&
                data instanceof PossitionDataValue) {
            PossitionDataValue fixData = (PossitionDataValue) data;
            newLongitude = this.displayUnits.toDispay(fixData.getLongitude(), null, Data.DataType.LONGITUDE);
            newLatitude = this.displayUnits.toDispay(fixData.getLatitude(), null, Data.DataType.LATITUDE);
        } else if (Data.DataKey.NAVIGATION_DATETIME.equals(data.key)) {
            newDate = data.getText();
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.key+" expected one of "+Data.DataKey.NAVIGATION_POSITION+","+ Data.DataKey.NAVIGATION_DATETIME );
        }
        if ( !newLongitude.equals(longitude) ||
                !newLatitude.equals(latitude) ||
                !newDate.equals(date) ) {
            longitude = newLongitude;
            latitude =  newLatitude;
            date = newDate;
            return true;
        }
        return false;
    }



    @Override
    void renderInstrument(Graphics2D g2) {
        Util.drawString(latitude, boxWidth / 2, boxHeight/2, normalFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        Util.drawString(longitude, boxWidth / 2, boxHeight/2, normalFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
        Util.drawString(date, borderPadding, smallLineSpace, smallFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        this.drawBaseLine("pos", "lat/lon", g2);

    }




}

