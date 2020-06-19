package uk.co.tfd.kindle.signalk;

import junit.framework.Assert;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by ieb on 08/06/2020.
 */

public class CalcTests {

    private Data.Store store;
    private Calcs calcs;
    public static String TEST_JSON = "{\"uuid\":\"urn:mrn:signalk:uuid:c0d79334-4e25-4245-8892-54e8ccc80222\",\"name\":\"Lona\",\"environment\":{\"wind\":{\"speedApparent\":{\"meta\":{\"units\":\"m/s\",\"description\":\"Apparent wind speed\"},\"value\":4.98,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:01:25.754\",\"pgn\":130306},\"angleApparent\":{\"meta\":{\"units\":\"rad\",\"description\":\"Apparent wind angle, negative to port\"},\"value\":-0.49858530717958605,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:01:25.754\",\"pgn\":130306}},\"current\":{\"meta\":{\"description\":\"Direction and strength of current affecting the vessel\"},\"value\":{\"setTrue\":3.5367,\"drift\":0.83},\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.274\",\"pgn\":130577},\"water\":{\"temperature\":{\"meta\":{\"units\":\"K\",\"description\":\"Current water temperature\"},\"value\":286.75,\"$source\":\"Captured CAN.105.0\",\"timestamp\":\"2018-05-19T11:01:25.558\",\"pgn\":130312,\"values\":{\"Captured CAN.105\":{\"value\":286.75,\"pgn\":130310,\"timestamp\":\"2018-05-19T11:01:25.557\"},\"Captured CAN.105.0\":{\"value\":286.75,\"timestamp\":\"2018-05-19T11:01:25.558\",\"pgn\":130312}}}},\"depth\":{\"belowTransducer\":{\"meta\":{\"units\":\"m\",\"description\":\"Depth below Transducer\"},\"value\":4.88,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:01:25.554\",\"pgn\":128267}}},\"navigation\":{\"speedOverGround\":{\"meta\":{\"units\":\"m/s\",\"description\":\"Vessel speed over ground. If converting from AIS 'HIGH' value, set to 102.2 (Ais max value) and add warning in notifications\"},\"value\":2.62,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.672\",\"pgn\":129026},\"courseOverGroundTrue\":{\"meta\":{\"units\":\"rad\",\"description\":\"Course over ground (true)\"},\"value\":2.9077,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.672\",\"pgn\":129026},\"position\":{\"meta\":{\"description\":\"The position of the vessel in 2 or 3 dimensions (WGS84 datum)\"},\"value\":{\"longitude\":1.3120291,\"latitude\":51.9006501},\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.778\",\"pgn\":129025},\"headingMagnetic\":{\"meta\":{\"units\":\"rad\",\"description\":\"Current magnetic heading of the vessel, equals 'headingCompass adjusted for magneticDeviation'\"},\"value\":2.6588,\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:01:25.766\",\"pgn\":127250},\"rateOfTurn\":{\"meta\":{\"units\":\"rad/s\",\"description\":\"Rate of turn (+ve is change to starboard). If the value is AIS RIGHT or LEFT, set to +-0.0206 rads and add warning in notifications\"},\"value\":0.00764206,\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:01:25.766\",\"pgn\":127251},\"attitude\":{\"meta\":{\"description\":\"Vessel attitude: roll, pitch and yaw\"},\"value\":{\"yaw\":2.6589,\"pitch\":0.0105,\"roll\":0.0544},\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:01:25.769\",\"pgn\":127257},\"magneticVariation\":{\"meta\":{\"units\":\"rad\",\"description\":\"The magnetic variation (declination) at the current position that must be added to the magnetic heading to derive the true heading. Easterly variations are positive and Westerly variations are negative (in Radians).\"},\"value\":0.0012,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.600\",\"pgn\":127258},\"datetime\":{\"meta\":{\"description\":\"Time and Date from the GNSS Positioning System\"},\"value\":\"2018-05-19T11:01:24.05180Z\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.777\",\"pgn\":129029},\"gnss\":{\"satellites\":{\"meta\":{\"description\":\"Number of satellites\"},\"value\":12,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.777\",\"pgn\":129029},\"horizontalDilution\":{\"meta\":{\"description\":\"Horizontal Dilution of Precision\"},\"value\":0.85,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.777\",\"pgn\":129029},\"type\":{\"meta\":{\"description\":\"Fix type\"},\"value\":\"GPS+SBAS/WAAS\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.777\",\"pgn\":129029},\"methodQuality\":{\"meta\":{\"description\":\"Quality of the satellite fix\"},\"value\":\"GNSS Fix\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.777\",\"pgn\":129029},\"integrity\":{\"meta\":{\"description\":\"Integrity of the satellite fix\"},\"value\":\"no Integrity checking\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:01:25.777\",\"pgn\":129029}},\"speedThroughWater\":{\"meta\":{\"units\":\"m/s\",\"description\":\"Vessel speed through the water\"},\"value\":2.14,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:01:25.555\",\"pgn\":128259},\"speedThroughWaterReferenceType\":{\"value\":\"Paddle wheel\",\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:01:25.555\",\"pgn\":128259},\"trip\":{\"log\":{\"meta\":{\"units\":\"m\",\"description\":\"Total distance traveled on this trip / since trip reset\"},\"value\":149771,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:01:25.557\",\"pgn\":128275}},\"log\":{\"meta\":{\"units\":\"m\",\"description\":\"Total distance traveled\"},\"value\":149641,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:01:25.557\",\"pgn\":128275}},\"steering\":{\"rudderAngle\":{\"meta\":{\"units\":\"rad\",\"description\":\"Current rudder angle, +ve is rudder to Starboard\"},\"value\":-0.0445,\"$source\":\"Captured CAN.172\",\"timestamp\":\"2018-05-19T11:01:25.784\",\"pgn\":127245,\"values\":{\"Captured CAN.204\":{\"value\":-0.0454,\"pgn\":127245,\"timestamp\":\"2018-05-19T11:01:25.769\"},\"Captured CAN.172\":{\"value\":-0.0445,\"timestamp\":\"2018-05-19T11:01:25.784\",\"pgn\":127245}}},\"autopilot\":{\"target\":{\"headingMagnetic\":{\"meta\":{\"units\":\"rad\",\"description\":\"Target heading for autopilot, relative to Magnetic North\"},\"value\":2.7122,\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:01:25.771\",\"pgn\":65360}},\"state\":{\"meta\":{\"description\":\"Autopilot state\"},\"value\":\"auto\",\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:01:25.472\",\"pgn\":65379}}},\"notifications\":{\"server\":{\"newVersion\":{\"value\":{\"state\":\"alert\",\"method\":[],\"message\":\"A new version (1.30.0) of the server is available\"},\"$source\":\"signalk-server\",\"timestamp\":\"2020-06-08T13:46:03.540Z\"}}}}";
    public static String TEST_UPDATE_JSON = "{\"uuid\":\"urn:mrn:signalk:uuid:c0d79334-4e25-4245-8892-54e8ccc80222\",\"name\":\"Lona\",\"environment\":{\"wind\":{\"speedApparent\":{\"meta\":{\"units\":\"m/s\",\"description\":\"Apparent wind speed\"},\"value\":4.62,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:00:23.747\",\"pgn\":130306},\"angleApparent\":{\"meta\":{\"units\":\"rad\",\"description\":\"Apparent wind angle, negative to port\"},\"value\":-0.49858530717958605,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:00:23.747\",\"pgn\":130306}},\"current\":{\"meta\":{\"description\":\"Direction and strength of current affecting the vessel\"},\"value\":{\"setTrue\":3.3554,\"drift\":0.9},\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.646\",\"pgn\":129291},\"water\":{\"temperature\":{\"meta\":{\"units\":\"K\",\"description\":\"Current water temperature\"},\"value\":286.75,\"$source\":\"Captured CAN.105.0\",\"timestamp\":\"2018-05-19T11:00:23.551\",\"pgn\":130312,\"values\":{\"Captured CAN.105\":{\"value\":286.75,\"pgn\":130310,\"timestamp\":\"2018-05-19T11:00:23.551\"},\"Captured CAN.105.0\":{\"value\":286.75,\"timestamp\":\"2018-05-19T11:00:23.551\",\"pgn\":130312}}}},\"depth\":{\"belowTransducer\":{\"meta\":{\"units\":\"m\",\"description\":\"Depth below Transducer\"},\"value\":5.24,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:00:23.547\",\"pgn\":128267}}},\"navigation\":{\"speedOverGround\":{\"meta\":{\"units\":\"m/s\",\"description\":\"Vessel speed over ground. If converting from AIS 'HIGH' value, set to 102.2 (Ais max value) and add warning in notifications\"},\"value\":2.98,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.644\",\"pgn\":129026},\"courseOverGroundTrue\":{\"meta\":{\"units\":\"rad\",\"description\":\"Course over ground (true)\"},\"value\":2.9007,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.644\",\"pgn\":129026},\"position\":{\"meta\":{\"description\":\"The position of the vessel in 2 or 3 dimensions (WGS84 datum)\"},\"value\":{\"longitude\":1.3114478,\"latitude\":51.9021397},\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.745\",\"pgn\":129025},\"headingMagnetic\":{\"meta\":{\"units\":\"rad\",\"description\":\"Current magnetic heading of the vessel, equals 'headingCompass adjusted for magneticDeviation'\"},\"value\":2.7165,\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:00:23.769\",\"pgn\":127250},\"rateOfTurn\":{\"meta\":{\"units\":\"rad/s\",\"description\":\"Rate of turn (+ve is change to starboard). If the value is AIS RIGHT or LEFT, set to +-0.0206 rads and add warning in notifications\"},\"value\":-0.00000709,\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:00:23.771\",\"pgn\":127251},\"attitude\":{\"meta\":{\"description\":\"Vessel attitude: roll, pitch and yaw\"},\"value\":{\"yaw\":2.7166,\"pitch\":0.0337,\"roll\":0.0841},\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:00:23.774\",\"pgn\":127257},\"magneticVariation\":{\"meta\":{\"units\":\"rad\",\"description\":\"The magnetic variation (declination) at the current position that must be added to the magnetic heading to derive the true heading. Easterly variations are positive and Westerly variations are negative (in Radians).\"},\"value\":0.0012,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.166\",\"pgn\":127258},\"datetime\":{\"meta\":{\"description\":\"Time and Date from the GNSS Positioning System\"},\"value\":\"2018-05-19T11:00:22.00600Z\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.445\",\"pgn\":126992},\"gnss\":{\"satellites\":{\"meta\":{\"description\":\"Number of satellites\"},\"value\":12,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.349\",\"pgn\":129029},\"horizontalDilution\":{\"meta\":{\"description\":\"Horizontal Dilution of Precision\"},\"value\":0.85,\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.349\",\"pgn\":129029},\"type\":{\"meta\":{\"description\":\"Fix type\"},\"value\":\"GPS+SBAS/WAAS\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.349\",\"pgn\":129029},\"methodQuality\":{\"meta\":{\"description\":\"Quality of the satellite fix\"},\"value\":\"GNSS Fix\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.349\",\"pgn\":129029},\"integrity\":{\"meta\":{\"description\":\"Integrity of the satellite fix\"},\"value\":\"no Integrity checking\",\"$source\":\"Captured CAN.3\",\"timestamp\":\"2018-05-19T11:00:23.349\",\"pgn\":129029}},\"speedThroughWater\":{\"meta\":{\"units\":\"m/s\",\"description\":\"Vessel speed through the water\"},\"value\":2.25,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:00:23.548\",\"pgn\":128259},\"speedThroughWaterReferenceType\":{\"value\":\"Paddle wheel\",\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:00:23.548\",\"pgn\":128259},\"trip\":{\"log\":{\"meta\":{\"units\":\"m\",\"description\":\"Total distance traveled on this trip / since trip reset\"},\"value\":149623,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:00:23.550\",\"pgn\":128275}},\"log\":{\"meta\":{\"units\":\"m\",\"description\":\"Total distance traveled\"},\"value\":149456,\"$source\":\"Captured CAN.105\",\"timestamp\":\"2018-05-19T11:00:23.550\",\"pgn\":128275}},\"steering\":{\"rudderAngle\":{\"meta\":{\"units\":\"rad\",\"description\":\"Current rudder angle, +ve is rudder to Starboard\"},\"value\":-0.041,\"$source\":\"Captured CAN.172\",\"timestamp\":\"2018-05-19T11:00:23.796\",\"pgn\":127245,\"values\":{\"Captured CAN.204\":{\"value\":-0.0419,\"pgn\":127245,\"timestamp\":\"2018-05-19T11:00:23.775\"},\"Captured CAN.172\":{\"value\":-0.041,\"timestamp\":\"2018-05-19T11:00:23.796\",\"pgn\":127245}}},\"autopilot\":{\"target\":{\"headingMagnetic\":{\"meta\":{\"units\":\"rad\",\"description\":\"Target heading for autopilot, relative to Magnetic North\"},\"value\":2.7122,\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:00:23.776\",\"pgn\":65360}},\"state\":{\"meta\":{\"description\":\"Autopilot state\"},\"value\":\"auto\",\"$source\":\"Captured CAN.204\",\"timestamp\":\"2018-05-19T11:00:23.476\",\"pgn\":65379}}},\"notifications\":{\"server\":{\"newVersion\":{\"value\":{\"state\":\"alert\",\"method\":[],\"message\":\"A new version (1.30.0) of the server is available\"},\"$source\":\"signalk-server\",\"timestamp\":\"2020-06-08T13:46:03.540Z\"}}}}";

    @Before
    public void before() {
        store = new Data.Store();
        calcs = new Calcs(store);
        calcs.start();

    }

    @Test
    public void testCalcsStartup() {
        // FIXME calcs.onUpdate(store);
    }
    @Test
    public void testCalcsUpdate() throws ParseException {
        JSONParser parser = new JSONParser();
        Map<String, Object> update = (Map<String, Object>) parser.parse(TEST_JSON);
        store.update("", update);
        update = (Map<String, Object>) parser.parse(TEST_UPDATE_JSON);
        store.update("", update);
    }
    @Test
    public void testCalcsUpdateEnhance() throws ParseException {
        JSONParser parser = new JSONParser();
        Map<String, Object> update = (Map<String, Object>) parser.parse(TEST_JSON);
        store.update("", update);
        update = (Map<String, Object>) parser.parse(TEST_UPDATE_JSON);
        store.update("", update);
    }
    @Test
    public void testCalcsUpdateEnhanceStats() throws ParseException {
       // calcs.enhance();
        JSONParser parser = new JSONParser();
        Map<String, Object> update = (Map<String, Object>) parser.parse(TEST_JSON);
        store.update("", update);
        for(int i = 0; i < 500; i++ ) {
            update = (Map<String, Object>) parser.parse(TEST_UPDATE_JSON);
            store.update("", update);
        }
    }

    @Test
    public void testObsevables() {
        Data.DataValue o = new Data.DataValue(Data.DataKey.NAVIGATION_DATETIME);
        final char[] check = {'0','0','0'};
        Data.Listener l1 = new Data.Listener<Data.DataValue>() {
            @Override
            public void onUpdate(Data.DataValue d) {
                check[0] = 'C';
            }
        };
        Data.Listener l2= new Data.Listener<Data.DataValue>() {
            @Override
            public void onUpdate(Data.DataValue d) {
                check[1] = 'C';

            }
        };
        Data.Listener l3 = new Data.Listener<Data.DataValue>() {
            @Override
            public void onUpdate(Data.DataValue d) {
                check[2] = 'C';

            }
        };
        Arrays.fill(check, '0');
        o.addListener(l1);
        Arrays.fill(check, '0');
        o.fireUpdate();
        Assert.assertEquals("C00", new String(check));
        Arrays.fill(check, '0');
        o.addListener(l1);
        o.fireUpdate();
        Assert.assertEquals("C00", new String(check));
        Arrays.fill(check, '0');
        o.addListener(l2);
        o.fireUpdate();
        Assert.assertEquals("CC0", new String(check));
        Arrays.fill(check, '0');
        o.removeListener(l1);
        o.addListener(l3);
        o.fireUpdate();
        Assert.assertEquals("0CC", new String(check));
        Arrays.fill(check, '0');
        o.removeListener(l1);
        o.removeListener(l2);
        o.removeListener(l3);
        o.fireUpdate();
        Assert.assertEquals("000", new String(check));
    }


}
