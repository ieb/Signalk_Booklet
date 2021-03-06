package uk.co.tfd.kindle.signalk;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.signalk.widgets.EInkTextBox;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ieb on 10/06/2020.
 */
public class Data {
    
    public enum Unit {
        RAD, MS, RATIO,M, MAP, K, TEXT, PA, RH,HZ, V, A
    }

    public enum DataType {
        SPEED, BEARING, DISTANCE, NONE, RELATIVEANGLE, LATITUDE, LONGITUDE,
         TEMPERATURE, PERCENTAGE, DEPTH, ATMOSPHERICPRESSURE, PRESSURE,
        HUMIDITY, FREQUENCY, RPM, VOLTAGE, CURRENT

    }

    public static class DataKey {
        public static Map<String, DataKey> values = new HashMap<>();


        public static DataKey NAVIGATION_MAGNETIC_VARIATION = new DataKey("navigation.magneticVariation",Unit.RAD, DataType.BEARING, "Magnetic Variation");
        public static DataKey NAVIGATION_HEADING_TRUE = new DataKey("navigation.headingTrue",Unit.RAD, DataType.BEARING, "");
        public static DataKey NAVIGATION_COURSE_OVER_GROUND_MAGNETIC = new DataKey("navigation.courseOverGroundMagnetic",Unit.RAD,DataType.BEARING, "");
        public static DataKey NAVIGATION_HEADING_MAGNETIC = new DataKey("navigation.headingMagnetic",Unit.RAD,DataType.BEARING, "");
        public static DataKey ENVIRONMENT_WIND_SPEED_APPARENT = new DataKey("environment.wind.speedApparent",Unit.MS,DataType.SPEED,"");
        public static DataKey ENVIRONMENT_WIND_ANGLE_APPARENT = new DataKey("environment.wind.angleApparent",Unit.RAD,DataType.RELATIVEANGLE, "");
        public static DataKey ENVIRONMENT_WIND_ANGLE_TRUE_WATER = new DataKey("environment.wind.angleTrueWater",Unit.RAD,DataType.RELATIVEANGLE,"");
        public static DataKey ENVIRONMENT_WIND_SPEED_TRUE = new DataKey("environment.wind.speedTrue",Unit.MS,DataType.SPEED,"");
        public static DataKey PERFORMANCE_LEEWAY = new DataKey("performance.leeway",Unit.RAD,DataType.RELATIVEANGLE,"");
        public static DataKey NAVIGATION_SPEED_THROUGH_WATER = new DataKey("navigation.speedThroughWater",Unit.MS,DataType.SPEED,"");
        public static DataKey PERFORMANCE_POLAR_SPEED = new DataKey("performance.polarSpeed",Unit.MS,DataType.SPEED,"");
        public static DataKey PERFORMANCE_POLAR_SPEED_RATIO = new DataKey("performance.polarSpeedRatio",Unit.RATIO,DataType.PERCENTAGE,"");
        public static DataKey PERFORMANCE_POLAR_VMG = new DataKey("performance.polarVmg",Unit.MS,DataType.SPEED,"");
        public static DataKey PERFORMANCE_TARGET_TWA = new DataKey("performance.targetTwa",Unit.RAD,DataType.RELATIVEANGLE,"");
        public static DataKey PERFORMANCE_TARGET_STW = new DataKey("performance.targetStw",Unit.MS,DataType.SPEED,"");
        public static DataKey PERFORMANCE_TARGET_VMG = new DataKey("performance.targetVmg",Unit.MS,DataType.SPEED,"");
        public static DataKey PERFORMANCE_POLAR_VMG_RATIO = new DataKey("performance.polarVmgRatio",Unit.RATIO,DataType.PERCENTAGE,"");
        public static DataKey ENVIRONMENT_WIND_WIND_DIRECTION_TRUE = new DataKey("environment.wind.windDirectionTrue",Unit.RAD,DataType.BEARING,"");
        public static DataKey ENVIRONMENT_WIND_WIND_DIRECTION_MAGNETIC = new DataKey("environment.wind.windDirectionMagnetic",Unit.RAD,DataType.BEARING,"");
        public static DataKey PERFORMANCE_OPPOSITE_TRACK_TRUE = new DataKey("performance.oppositeTrackTrue",Unit.RAD,DataType.BEARING,"");
        public static DataKey PERFORMANCE_OPPOSITE_TRACK_MAGNETIC = new DataKey("performance.oppositeTrackMagnetic",Unit.RAD,DataType.BEARING,"");
        public static DataKey PERFORMANCE_OPPOSITE_HEADING_MAGNETIC = new DataKey("performance.oppositeHeadingMagnetic",Unit.RAD,DataType.BEARING,"");
        public static DataKey NAVIGATION_COURSE_OVER_GROUND_TRUE = new DataKey("navigation.courseOverGroundTrue",Unit.RAD,DataType.BEARING,"");
        public static DataKey PERFORMANCE_VMG = new DataKey("performance.vmg", Unit.MS,DataType.SPEED, "current vmg at polar speed");
        public static DataKey NAVIGATION_TRIP_LOG = new DataKey("navigation.trip.log", Unit.M, DataType.DISTANCE,"Trip");
        public static DataKey NAVIGATION_LOG = new DataKey("navigation.log", Unit.M, DataType.DISTANCE,"Log");
        public static DataKey NAVIGATION_DATETIME = new DataKey("navigation.datetime",Unit.TEXT,DataType.NONE,"Navigation time");
        public static DataKey NAVIGATION_GNSS = new DataKey("navigation.gnss", Unit.MAP, DataType.NONE,"GPS Status");
        public static DataKey STEERING_AUTOPILOT = new DataKey("steering.autopilot", Unit.MAP, DataType.NONE, "Auto pilot data");
        public static DataKey NAVIGATION_POSITION = new DataKey("navigation.position",Unit.MAP,DataType.NONE,"Lat Long");
        public static DataKey ENVIRONMENT_CURRENT = new DataKey("environment.current",Unit.MAP,DataType.NONE,"Set Drift");
        public static DataKey NAVIGATION_ATTITUDE = new DataKey("navigation.attitude",Unit.MAP,DataType.NONE,"Pitch Roll");
        public static DataKey NAVIGATION_RATEOFTURN = new DataKey("navigation.rateOfTurn",Unit.RAD,DataType.RELATIVEANGLE,"Rate of turn, radians/s");
        public static DataKey STEERING_RUDDERANGLE = new DataKey("steering.rudderAngle",Unit.RAD,DataType.RELATIVEANGLE,"Rudder angle");
        public static DataKey NAVIGATION_SPEEDOVERGROUND = new DataKey("navigation.speedOverGround",Unit.MS,DataType.SPEED,"Sog");
        public static DataKey ENVIRONMENT_WATER_TEMPERATURE = new DataKey("environment.water.temperature",Unit.K,DataType.TEMPERATURE,"Water Temperature");
        public static DataKey NAVIGATION_SPEEDTHROUGHWATERREFERENCETYPE = new DataKey("navigation.speedThroughWaterReferenceType",Unit.TEXT,DataType.NONE,"Sensor Type");
        public static DataKey ENVIRONMENT_DEPTH_BELOWTRANSDUCER = new DataKey("environment.depth.belowTransducer",Unit.M,DataType.DEPTH,"Depth");
        public final String id;
        public final Unit units;
        public final String description;
        public final DataType type;

        public DataKey(String id, Unit units, DataType type, String description) {
            this.id = id;
            this.units = units;
            this.description = description;
            this.type = type;
            values.put(id, this);
        }

        @Override
        public boolean equals(Object obj) {
            if ( obj instanceof  DataKey ) {
                return id.equals(((DataKey)obj).id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return id;
        }
    }


    public interface DataConversion {

        String convert(double value, DecimalFormat format);

        DataType getDataType();
    }

    public static class DepthInM implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public DataType getDataType() {
            return DataType.DEPTH;
        }
    }

    public static class DistanceInNm implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*0.000539957);  // m -> Nm
        }

        @Override
        public DataType getDataType() {
            return DataType.DISTANCE;
        }
    }

    public static class SpeedInKn implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*1.94384);  // m/s -> Kn
        }

        @Override
        public DataType getDataType() {
            return DataType.SPEED;
        }
    }

    public static class BearingInDeg implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            // 0 - 360
            return format.format(Calcs.correctBearing(value)*180/Math.PI);
        }

        @Override
        public DataType getDataType() {
            return DataType.BEARING;
        }
    }

    public static class RelativeAngleInDeg implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            // - PI to +PI
            if ( value < Math.PI ) {
                value = value+Math.PI*2;
            }
            if ( value > Math.PI ) {
                value = value-Math.PI*2;
            }
            return format.format(value*180/Math.PI);
        }

        @Override
        public DataType getDataType() {
            return DataType.RELATIVEANGLE;
        }
    }

    public static class LatitudeDisplay implements DataConversion {

        DecimalFormat formatDeg = new DecimalFormat("00");
        DecimalFormat formatMin = new DecimalFormat("00.000");

        @Override
        public String convert(double latitude, DecimalFormat format) {
            String NS = "N";
            if ( latitude < 0) {
                latitude = -latitude;
                NS = "S";
            }
            double d = Math.floor(latitude);
            double m = (60.0*(latitude-d));
            return formatDeg.format(d) + "\u00B0" + formatMin.format(m) + "\u2032" + NS;
        }

        @Override
        public DataType getDataType() {
            return DataType.LATITUDE;
        }
    }

    public static class LongitudeDisplay implements DataConversion {

        DecimalFormat formatDeg = new DecimalFormat("000");
        DecimalFormat formatMin = new DecimalFormat("00.000");

        @Override
        public String convert(double longitude, DecimalFormat format) {
            String EW = "E";
            if (longitude < 0) {
                longitude = -longitude;
                EW = "W";
            }
            double d = Math.floor(longitude);
            double m = (60.0*(longitude-d));
            return formatDeg.format(d) + "\u00B0" + formatMin.format(m) + "\u2032" + EW;
        }

        @Override
        public DataType getDataType() {
            return DataType.LONGITUDE;
        }
    }

    public static class TemperatureDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value-273.15);
        }

        @Override
        public DataType getDataType() {
            return DataType.TEMPERATURE;
        }
    }
    public static class PercentageDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*100.0);
        }

        @Override
        public DataType getDataType() {
            return DataType.PERCENTAGE;
        }
    }
    public static class HumidityDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public DataType getDataType() {
            return DataType.HUMIDITY;
        }
    }

    public static class AtmosphericPressureDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*0.01);
        }

        @Override
        public DataType getDataType() {
            return DataType.ATMOSPHERICPRESSURE;
        }
    }

    public static class PressureDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value/100000.0);
        }

        @Override
        public DataType getDataType() {
            return DataType.PRESSURE;
        }
    }

    public static class RpmDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*60.0);
        }

        @Override
        public DataType getDataType() {
            return DataType.RPM;
        }
    }

    public static class FrequencyDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public DataType getDataType() {
            return DataType.FREQUENCY;
        }
    }
    public static class VoltageDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public DataType getDataType() {
            return DataType.VOLTAGE;
        }
    }

    public static class CurrentDisplay implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public DataType getDataType() {
            return DataType.CURRENT;
        }
    }

    public static class NoConversion implements DataConversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public DataType getDataType() {
            return DataType.NONE;
        }
    }






    public static DisplayUnits createSIDisplayUnits() {
        DisplayUnits displayUnits = new DisplayUnits();
        displayUnits.add(new DepthInM());
        displayUnits.add(new DistanceInNm());
        displayUnits.add(new SpeedInKn());
        displayUnits.add(new BearingInDeg());
        displayUnits.add(new RelativeAngleInDeg());
        displayUnits.add(new LatitudeDisplay());
        displayUnits.add(new LongitudeDisplay());
        displayUnits.add(new TemperatureDisplay());
        displayUnits.add(new PercentageDisplay());
        displayUnits.add(new HumidityDisplay());
        displayUnits.add(new AtmosphericPressureDisplay());
        displayUnits.add(new RpmDisplay());
        displayUnits.add(new FrequencyDisplay());
        displayUnits.add(new VoltageDisplay());
        displayUnits.add(new CurrentDisplay());
        displayUnits.add(new NoConversion());
        return displayUnits;
    }




    public static class DisplayUnits {
        private static final Logger log = LoggerFactory.getLogger(DisplayUnits.class);

        private Map<DataType,DataConversion> conversions = new HashMap<DataType, DataConversion>();

        public void add(DataConversion conversion) {
            conversions.put(conversion.getDataType(), conversion);
        }

        public String toDispay(double value, DecimalFormat format, DataType dataType) {
            if ( conversions.containsKey(dataType) ) {
                return conversions.get(dataType).convert(value, format);
            }
            log.warn("Conversion Not found for data type {} {} ", dataType, value);
            Exception e = new Exception("Traceback");
            log.warn("Traceback",e);
            return format.format(value);
        }
    }


    public interface Listener<T extends Observable> {

        void onUpdate(T d);
    }

    public static class Observable extends StatusUpdates {
        private Listener[] listeners = new Listener[0];
        private Set<Listener> listenerSet = new HashSet<Listener>();
        private long debounce = 500;
        private long nextUpdate = 0;

        public void addListener(Listener l) {
            listenerSet.add(l);
            listeners = listenerSet.toArray(new Listener[listenerSet.size()]);
        }
        public void removeListener(Listener l) {
            listenerSet.remove(l);
            listeners = listenerSet.toArray(new Listener[listenerSet.size()]);
        }
        protected void fireUpdate() {

            if ( nextUpdate < System.currentTimeMillis()) {
                nextUpdate = System.currentTimeMillis()+debounce;
                for (Listener listener: listeners) {
                    listener.onUpdate(this);
                }
            }
        }

    }


    public static class Store extends Observable {
        private static final Logger log = LoggerFactory.getLogger(Store.class);
        Map<String, DataValue> state = new HashMap<String, DataValue>();
        private Timer timer;

        public Store() {

            loadStore();
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    calcStats();
                }
            });

        }




        private void loadStore() {
            // special mappings.

                    /*
                steering.autopilot.state
        steering.autopilot.target.headingMagnetic
        navigation.gnss.methodQuality
        navigation.gnss.type
        navigation.gnss.horizontalDilution
        navigation.gnss.satellites

         */

            state.put(DataKey.NAVIGATION_SPEEDTHROUGHWATERREFERENCETYPE.toString(), new DataValue(DataKey.NAVIGATION_SPEEDTHROUGHWATERREFERENCETYPE));
            DataValue fixDataValue = new FixDataValue(DataKey.NAVIGATION_GNSS);
            state.put(DataKey.NAVIGATION_GNSS.toString(), fixDataValue);
            state.put(DataKey.NAVIGATION_GNSS.toString()+".methodQuality", fixDataValue);
            state.put(DataKey.NAVIGATION_GNSS.toString()+".type", fixDataValue);
            state.put(DataKey.NAVIGATION_GNSS.toString()+".horizontalDilution", fixDataValue);
            state.put(DataKey.NAVIGATION_GNSS.toString()+".satellites", fixDataValue);
            state.put(DataKey.NAVIGATION_GNSS.toString()+".integrity", fixDataValue);

            PilotDataValue pilotDataValue = new PilotDataValue(DataKey.STEERING_AUTOPILOT);
            state.put(DataKey.STEERING_AUTOPILOT.toString(), pilotDataValue);
            state.put(DataKey.STEERING_AUTOPILOT.toString()+".state", pilotDataValue);
            state.put(DataKey.STEERING_AUTOPILOT.toString()+".target.headingMagnetic", pilotDataValue);


            state.put(DataKey.NAVIGATION_DATETIME.toString(), new DataValue(DataKey.NAVIGATION_DATETIME));
            state.put(DataKey.NAVIGATION_POSITION.toString(), new PossitionDataValue((DataKey.NAVIGATION_POSITION)));
            state.put(DataKey.ENVIRONMENT_CURRENT.toString(), new CurrentDataValue((DataKey.ENVIRONMENT_CURRENT)));
            state.put(DataKey.NAVIGATION_ATTITUDE.toString(), new AttitudeDataValue((DataKey.NAVIGATION_ATTITUDE)));
            for(DataKey k : DataKey.values.values()) {
                if (!state.containsKey(k.toString())) {
                    if (Unit.RAD.equals(k.units)) {
                        state.put(k.toString(), new CircularDataValue(k));
                    } else {
                        state.put(k.toString(), new DoubleDataValue(k));
                    }
                }
            }
            this.updateStatus("Store started");
        }


        public <T extends DataValue> T  get( DataKey key) {
            if ( state.containsKey(key.toString())) {
                return (T)state.get(key.toString());
            } else {
                throw new IllegalArgumentException("DataValue  not found at "+key);
            }
        }
        public Map<String, Object> update(String root, Map<String, Object> update) {
            Map<String, Object> rejects = new HashMap<String, Object>();
            doUpdate(root, update, rejects);
            fireUpdate();
            return rejects;
        }


        public void doUpdate(String root, Map<String, Object> update, Map<String, Object> rejects) {
            for(Map.Entry<String, Object> e : update.entrySet()) {
                String path = root  + e.getKey();
                Object o = e.getValue();
                if ( state.containsKey(path) ) {
                    state.get(path).update(o);
                } else if ( o instanceof Map ) {
                    doUpdate(path + ".", (Map<String, Object>) o, rejects);
                } else {
                    rejects.put(path,o);
                }
            }
        }


        public void updateFromServer(Map<String, Object> value) {
            String path = (String) value.get("path");
            if ( path != null ) {
                DataValue dataValue = state.get(path);
                if ( dataValue == null ) {
                    log.warn("Ignoring {} {} ", path, value);
                } else {
                    dataValue.update(value);
                }
            }
        }

        public void calcStats() {
            for(Map.Entry<String, DataValue> e : state.entrySet()) {
                e.getValue().calcStats();
            }
        }

        public void start() {
            timer.start();
        }
        public void stop() {
            timer.stop();
        }


        public void addConfiguration(Map<String, Object> configuration) {
            if (configuration.containsKey("datavalues")) {
                Map<String, Map<String, Object>> instruments = (Map<String, Map<String, Object>>) configuration.get("datavalues");
                for(Map.Entry<String, Map<String, Object>> e :  instruments.entrySet() ) {
                    try {
                        Map<String, Object> instrument = e.getValue();
                        Unit units = Unit.valueOf((String) instrument.get("unit"));
                        DataType dataType = DataType.valueOf((String) instrument.get("dataType"));
                        String description = (String) instrument.get("description");
                        DataKey k = new DataKey(e.getKey(), units, dataType, description);
                        Class dataClass = Class.forName("uk.co.tfd.kindle.signalk.Data$" + (String) instrument.get("dataClass"));
                        Constructor constructor = dataClass.getConstructor(DataKey.class);
                        DataValue dv = (DataValue) constructor.newInstance(k);
                        state.put(e.getKey(), dv);
                        List<String> paths = (List<String>) instrument.get("paths");
                        for (String path : paths) {
                            state.put(path, dv);
                        }
                        log.info("Added Data Path {} {} ",e.getKey(),dv);
                    } catch (Exception ex) {
                        log.error("Unable to create datavalue {} {} ",e.getKey(), ex.getMessage());
                        log.error(ex.getMessage(), ex);
                    }
                }
            }
        }
    }





    public static class DataValue extends Observable {
        private static final Logger log = LoggerFactory.getLogger(DataValue.class);
        String source;
        public final DataKey key;
        String text;

        long timestamp = System.currentTimeMillis();

        public DataValue(DataKey k) {
            this.key = k;
            this.source = "empty";
        }

        public void update(Object input) {
            if ( input instanceof Map) {
                update((Map<String, Object>)input);
            } else if (input instanceof String ) {
                this.text = (String)input;
                this.source = "input";
            } else {
                throw new IllegalArgumentException("Unable to update "+key+" from "+input);
            }
        }


        public void updateTimestamp(String dateString) {
            SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd'T'hh:mm:ss.SSS");
            try {
                if ( dateString != null) {
                    long ts = df.parse(dateString).getTime();
                    if ( ts >  this.timestamp) {
                        this.timestamp = ts;
                    }
                }
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }




        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            String newValue = String.valueOf(Util.resolve(input, "value", this.text));
            if ( "".equals(newValue) ) {
                log.warn("update() No text value found");
            }
            this.source = "input";
            if ( !newValue.equals(this.text) ) {
                this.text = newValue;
                this.fireUpdate();
            }
        }
        public void update(String v, long ts) {
            this.text = v;
            this.timestamp = ts;
            this.source = "calculated";
            this.fireUpdate();
        }

        public void calcStats() {
        }

            public boolean isInput() {
            return "input".equals(this.source);
        }

        public String getText() {
            return text;
        }
        public double getValue() {
            return 0;
        }

        public double getMax() {
            return 0;
        }

        public double getMin() {
            return 0;
        }

        public double getStdev() {
            return 0;
        }
        public double getMean() {
            return 0;
        }

        public DataKey getKey() {
            return key;
        }

    }

    public static class DoubleDataValue extends DataValue {
        private static final Logger log = LoggerFactory.getLogger(DoubleDataValue.class);
        double value = 0.0;
        protected double[] values = new double[100];
        protected int ilast = 0;
        protected int ifirst = 0;
        protected double mean;
        protected double stdev;
        protected double min;
        protected double max;
        private long change = 0;
        private long nochange = 0;

        public double getValue() {
            return value;
        }

        public double getMax() {
            return max;
        }

        public double getMin() {
            return min;
        }

        public double getStdev() {
            return stdev;
        }

        public double getMean() {
            return mean;
        }

        public DoubleDataValue(DataKey k) {
            super(k);
            source = "empty";
        }

        @Override
        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            Object o = Util.resolve(input,"value", null);
            double newValue = 0;
            if ( o instanceof Long) {
                newValue = (1.0*(long)o);
            } else if ( o instanceof Double ) {
                newValue = (double) o;
            } else {
                log.warn("Value not recognised in datavalue update {} ", input.get("value"));
                newValue = 0;
            }
            if ( newValue != this.value) {
                this.value = newValue;
                this.fireUpdate();
                change++;
            } else {
                nochange++;
            }
        }

        public void update(double v, long ts) {
            this.value = v;
            this.timestamp = ts;
            this.source = "calculated";
            this.fireUpdate();
        }

        @Override
        public void calcStats() {
            this.values[ilast] = this.value;
            ilast = (ilast+1)%100;
            if (ifirst == ilast ) {
                ifirst = (ifirst+1)%100;
            }


            double s = 0.0;
            double n = 0.0;
            if ( ifirst < ilast ) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double)(i+1)/2.0;
                    s += this.values[i]*w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double)((i-ifirst)+1)/2.0;
                    s += this.values[i]*w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double)((100-ifirst+i)+1)/2.0;
                    s += this.values[i]*w;
                    n += w;
                }
            }

            this.mean = s/n;
            s = 0.0;
            n = 0.0;
            if ( ifirst == 0 ) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double)(i+1)/2.0;
                    s += (this.values[i]-this.mean)*(this.values[i]-this.mean)*w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double)((i-ifirst)+1)/2.0;
                    s += (this.values[i]-this.mean)*(this.values[i]-this.mean)*w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double)((100-ifirst+i)+1)/2.0;
                    s += (this.values[i]-this.mean)*(this.values[i]-this.mean)*w;
                    n += w;
                }
            }
            this.stdev = Math.sqrt(s/n);

            this.min = this.mean;
            this.max = this.mean;
            if ( ifirst == 0 ) {
                for (int i = 0; i < ilast; i++) {
                    this.min = Math.min(this.values[i],this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    this.min = Math.min(this.values[i],this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
                for (int i = ilast; i < ifirst; i++) {
                    this.min = Math.min(this.values[i],this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            }

        }
    }



    public static class CircularDataValue extends DoubleDataValue {

        private final double[] sinvalues = new double[100];
        private final double[] cosvalues = new double[100];

        public CircularDataValue(DataKey k) {
            super(k);
        }
        @Override
        public void calcStats() {

            this.values[ilast] = this.value;
            this.sinvalues[ilast] = Math.sin(this.value);
            this.cosvalues[ilast] = Math.cos(this.value);
            ilast = (ilast+1)%100;
            if (ifirst == ilast ) {
                ifirst = (ifirst+1)%100;
            }


            double s = 0.0;
            double c = 0.0;
            double n = 0.0;
            if ( ifirst < ilast ) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double)(i+1)/2.0;
                    s += this.sinvalues[i]*w;
                    c += this.cosvalues[i]*w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double)((i-ifirst)+1)/2.0;
                    s += this.sinvalues[i]*w;
                    c += this.cosvalues[i]*w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double)((100-ifirst+i)+1)/2.0;
                    s += this.sinvalues[i]*w;
                    c += this.cosvalues[i]*w;
                    n += w;
                }
            }
            this.mean = Math.atan2(s/n,c/n);

            s = 0.0;
            n = 0.0;
            if ( ifirst == 0 ) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double)(i+1)/2.0;
                    double a = this.values[i]-this.mean;
                    // find the smallest sweep from the mean.
                    if ( a > Math.PI ) {
                        a = a - 2*Math.PI;
                    } else if ( a < -Math.PI ) {
                        a = a + 2*Math.PI;
                    }
                    s += a*a*w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double)((i-ifirst)+1)/2.0;
                    double a = this.values[i]-this.mean;
                    // find the smallest sweep from the mean.
                    if ( a > Math.PI ) {
                        a = a - 2*Math.PI;
                    } else if ( a < -Math.PI ) {
                        a = a + 2*Math.PI;
                    }
                    s += a*a*w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double)((100-ifirst+i)+1)/2.0;
                    double a = this.values[i]-this.mean;
                    // find the smallest sweep from the mean.
                    if ( a > Math.PI ) {
                        a = a - 2*Math.PI;
                    } else if ( a < -Math.PI ) {
                        a = a + 2*Math.PI;
                    }
                    s += a*a*w;
                    n += w;
                }
            }
            this.stdev = Math.sqrt(s/n);

            this.min = this.mean;
            this.max = this.mean;
            if ( ifirst == 0 ) {
                for (int i = 0; i < ilast; i++) {
                    this.min = Math.min(this.values[i],this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    this.min = Math.min(this.values[i],this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
                for (int i = ilast; i < ifirst; i++) {
                    this.min = Math.min(this.values[i],this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            }
        }

    }

    public static class PilotDataValue extends DataValue {

        private String state = "-";
        private double heading = 0.0;

        public PilotDataValue(DataKey k) {
            super(k);
        }

        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            String path = (String) input.get("path");

            String newstate = this.state;
            double newheading = this.heading;
            if ( path == null || this.key.id.equals(path)) {
                newstate = Util.resolve(input, "state.value", this.state);
                updateTimestamp(Util.resolve(input,"state.timestamp",null));
                newheading = Util.resolve(input, "target.headingMagnetic.value", this.heading);
                updateTimestamp(Util.resolve(input,"target.headingMagnetic.timestamp",null));
            } else if ( path.endsWith(".state")) {
                newstate = Util.resolve(input, "value", this.state);
            } else if ( path.endsWith(".target.headingMagnetic")) {
                newheading = Util.resolve(input, "value", this.heading);
            }
            if ( !this.state.equals(newstate) ||
                    this.heading != newheading) {
                this.state = newstate;
                this.heading = newheading;
                this.fireUpdate();
            }
        }

        public String getState() {
            return state;
        }

        public double getHeading() {
            return heading;
        }
    }



    public static class FixDataValue extends DataValue {

        private String methodQuality = "-";
        private String type = "-";
        private long satellites = 0L;
        private String integrity = "-";
        private double horizontalDilution = 0.0;

        public FixDataValue(DataKey k) {
            super(k);
        }
        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            String path = (String) input.get("path");

            String newmethodQuality = this.methodQuality;
            double newhorizontalDilution = this.horizontalDilution;
            String newtype = this.type;
            long newsatellites = this.satellites;
            String newintegrity = this.integrity;
            if (path == null || key.id.equals(path)) {
                newmethodQuality = Util.resolve(input, "methodQuality.value", this.methodQuality);
                updateTimestamp(Util.resolve(input,"methodQuality.timestamp",null));
                newhorizontalDilution = Util.resolve(input, "horizontalDilution.value", this.horizontalDilution);
                updateTimestamp(Util.resolve(input,"horizontalDilution.timestamp",null));
                newtype = Util.resolve(input, "type.value", this.type);
                updateTimestamp(Util.resolve(input,"type.timestamp",null));
                newsatellites = Util.resolve(input, "satellites.value", this.satellites);
                updateTimestamp(Util.resolve(input,"satellites.timestamp",null));
                newintegrity = Util.resolve(input, "integrity.value", this.integrity);
                updateTimestamp(Util.resolve(input,"integrity.timestamp",null));
            } else if ( path.endsWith(".methodQuality")) {
                newmethodQuality = Util.resolve(input, "value", this.methodQuality);

            } else if ( path.endsWith(".horizontalDilution")) {
                newhorizontalDilution = Util.resolve(input, "value", this.horizontalDilution);
            } else if ( path.endsWith(".type")) {
                newtype = Util.resolve(input, "value", this.type);
            } else if ( path.endsWith(".satellites")) {
                newsatellites = Util.resolve(input, "value", this.satellites);
            } else if ( path.endsWith(".integrity")) {
                newintegrity = Util.resolve(input, "value", this.integrity);
            }
            if ( !this.methodQuality.equals(newmethodQuality) ||
                    this.horizontalDilution != newhorizontalDilution ||
                    !this.type.equals(newtype) ||
                    this.satellites != newsatellites ||
                    !this.integrity.equals(newintegrity)) {
                this.methodQuality = newmethodQuality;
                this.horizontalDilution = newhorizontalDilution;
                this.satellites = newsatellites;
                this.type = newtype;
                this.integrity = newintegrity;
                this.fireUpdate();

            }

        }

        public String getMethodQuality() {
            return methodQuality;
        }


        public String getType() {
            return type;
        }


        public long getSatellites() {
            return satellites;
        }


        public String getIntegrity() {
            return integrity;
        }

        public double getHorizontalDilution() {
            return horizontalDilution;
        }
    }

    public static class PossitionDataValue extends DataValue {
        private double longitude = 0;
        private double latitude = 0;

        public PossitionDataValue(DataKey k) {
            super(k);
        }

        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            double newlongitude = Util.resolve(input,"value.longitude", this.longitude);
            double newlatitude = Util.resolve(input,"value.latitude", this.latitude);
            if ( newlongitude != longitude || newlatitude != latitude) {
                longitude = newlongitude;
                latitude = newlatitude;
                this.fireUpdate();
            }
        }


        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }


    }

    public static class CurrentDataValue extends DataValue {

        private double drift;
        private double set;

        public CurrentDataValue(DataKey k) {
            super(k);
        }

        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            double newDrift = Util.resolve(input,"value.drift",this.drift);
            double newSet = Util.resolve(input,"value.setTrue",this.set);
            if ( newDrift != this.drift || newSet != this.set ) {
                this.drift = newDrift;
                this.set = newSet;
                this.fireUpdate();
            }
        }

        public double getDrift() {
            return drift;
        }

        public double getSet() {
            return set;
        }
    }
    public static class AttitudeDataValue extends DataValue {

        private double pitch;
        private double roll;

        public AttitudeDataValue(DataKey k) {
            super(k);
        }

        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            this.pitch = Util.resolve(input,"value.pitch",this.pitch);
            this.roll = Util.resolve(input,"value.roll",this.pitch);
            this.fireUpdate();
        }

        public double getPitch() {
            return pitch;
        }

        public double getRoll() {
            return roll;
        }
    }





}
