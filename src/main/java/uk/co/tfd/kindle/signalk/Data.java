package uk.co.tfd.kindle.signalk;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ieb on 10/06/2020.
 */
public class Data {
    
    public enum Unit {
        RAD, MS, RATIO,M, MAP, TEXT
    }

    public enum DataType {
        SPEED, BEARING, DISTANCE, NONE, RELATIVEANGLE, LATITUDE, LONGITUDE, TEMPERATURE, PERCENTAGE, DEPTH

    }

    public enum DataKey {
        NAVIGATION_MAGNETIC_VARIATION("navigation.magneticVariation",Unit.RAD,"Magnetic Variation"),
        NAVIGATION_HEADING_TRUE("navigation.headingTrue",Unit.RAD,""),
        NAVIGATION_COURSE_OVER_GROUND_MAGNETIC("navigation.courseOverGroundMagnetic",Unit.RAD,""),
        NAVIGATION_HEADING_MAGNETIC("navigation.headingMagnetic",Unit.RAD,""),
        ENVIRONMENT_WIND_SPEED_APPARENT("environment.wind.speedApparent",Unit.MS,""),
        ENVIRONMENT_WIND_ANGLE_APPARENT("environment.wind.angleApparent",Unit.RAD,""),
        ENVIRONMENT_WIND_ANGLE_TRUE_WATER("environment.wind.angleTrueWater",Unit.RAD,""),
        ENVIRONMENT_WIND_SPEED_TRUE("environment.wind.speedTrue",Unit.MS,""),
        PERFORMANCE_LEEWAY("performance.leeway",Unit.RAD,""),
        NAVIGATION_SPEED_THROUGH_WATER("navigation.speedThroughWater",Unit.MS,""),
        PERFORMANCE_POLAR_SPEED("performance.polarSpeed",Unit.MS,""),
        PERFORMANCE_POLAR_SPEED_RATIO("performance.polarSpeedRatio",Unit.RATIO,""),
        PERFORMANCE_POLAR_VMG("performance.polarVmg",Unit.MS,""),
        PERFORMANCE_TARGET_TWA("performance.targetTwa",Unit.RAD,""),
        PERFORMANCE_TARGET_STW("performance.targetStw",Unit.MS,""),
        PERFORMANCE_TARGET_VMG("performance.targetVmg",Unit.MS,""),
        PERFORMANCE_POLAR_VMG_RATIO("performance.polarVmgRatio",Unit.RATIO,""),
        ENVIRONMENT_WIND_WIND_DIRECTION_TRUE("environment.wind.windDirectionTrue",Unit.RAD,""),
        ENVIRONMENT_WIND_WIND_DIRECTION_MAGNETIC("environment.wind.windDirectionMagnetic",Unit.RAD,""),
        PERFORMANCE_OPPOSITE_TRACK_TRUE("performance.oppositeTrackTrue",Unit.RAD,""),
        PERFORMANCE_OPPOSITE_TRACK_MAGNETIC("performance.oppositeTrackMagnetic",Unit.RAD,""),
        PERFORMANCE_OPPOSITE_HEADING_MAGNETIC("performance.oppositeHeadingMagnetic",Unit.RAD,""),
        NAVIGATION_COURSE_OVER_GROUND_TRUE("navigation.courseOverGroundTrue",Unit.RAD,""),
        PERFORMANCE_VMG("performance.vmg", Unit.MS, "current vmg at polar speed"),
        NAVIGATION_TRIP_LOG("navigation.trip.log", Unit.M, "Trip"),
        NAVIGATION_LOG("navigation.log", Unit.M, "Log"),
        NAVIGATION_DATETIME("navigation.datetime",Unit.TEXT,"Navigation time"),
        NAVIGATION_GNSS("navigation.gnss", Unit.MAP, "GPS Status"),
        STEERING_AUTOPILOT("steering.autopilot", Unit.MAP, "Auto pilot data"),
        NAVIGATION_POSITION("navigation.position",Unit.MAP,"Lat Long"),
        ENVIRONMENT_CURRENT("environment.current",Unit.MAP,"Set Drift"),
        NAVIGATION_ATTITUDE("navigation.attitude",Unit.MAP,"Pitch Roll")

        ;
        private final String id;
        private final Unit units;
        private final String description;
        private DataKey(String id, Unit units, String description) {
            this.id = id;
            this.units = units;
            this.description = description;
        }

        public static DataKey get(String id) {
            for( DataKey k : DataKey.values()) {
                if ( k.id.equals(id)) {
                    return k;
                }
            }
            return null;
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

        DecimalFormat formatDeg = new DecimalFormat("00");
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
        return displayUnits;
    }




    public static class DisplayUnits {


        private Map<DataType,DataConversion> conversions = new HashMap<DataType, DataConversion>();

        public void add(DataConversion conversion) {
            conversions.put(conversion.getDataType(), conversion);
        }

        public String toDispay(double value, DecimalFormat format, DataType dataType) {
            if ( conversions.containsKey(dataType) ) {
                return conversions.get(dataType).convert(value, format);
            }
            return format.format(value);
        }
    }


    public interface Listener<T extends Observable> {

        void onUpdate(T d);
    }

    public static class Observable {
        private Listener[] listeners = new Listener[0];
        private Set<Listener> listenerSet = new HashSet<Listener>();
        public void addListener(Listener l) {
            listenerSet.add(l);
            listeners = listenerSet.toArray(new Listener[listenerSet.size()]);
        }
        public void removeListener(Listener l) {
            listenerSet.remove(l);
            listeners = listenerSet.toArray(new Listener[listenerSet.size()]);
        }
        protected void fireUpdate() {
            for (Listener listener: listeners) {
                listener.onUpdate(this);
            }
        }

    }


    public static class Store extends Observable {
        Map<String, DataValue> state = new HashMap<String, DataValue>();

        public Store() {
            loadStore();
        }


        private void loadStore() {
            // special mappings.
            state.put(DataKey.NAVIGATION_GNSS.toString(), new FixDataValue(DataKey.NAVIGATION_GNSS));
            state.put(DataKey.STEERING_AUTOPILOT.toString(), new PilotDataValue(DataKey.STEERING_AUTOPILOT));
            state.put(DataKey.NAVIGATION_DATETIME.toString(), new DataValue(DataKey.NAVIGATION_DATETIME));
            state.put(DataKey.NAVIGATION_POSITION.toString(), new PossitionDataValue((DataKey.NAVIGATION_POSITION)));
            state.put(DataKey.ENVIRONMENT_CURRENT.toString(), new CurrentDataValue((DataKey.ENVIRONMENT_CURRENT)));
            state.put(DataKey.NAVIGATION_ATTITUDE.toString(), new AttitudeDataValue((DataKey.NAVIGATION_ATTITUDE)));
            for(DataKey k : DataKey.values()) {
                if (!state.containsKey(k.toString())) {
                    if (Unit.RAD.equals(k.units)) {
                        state.put(k.toString(), new CircularDataValue(k));
                    } else {
                        state.put(k.toString(), new DoubleDataValue(k));
                    }
                }
            }
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

    }





    public static class DataValue extends Observable {
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
                if ( dateString == null) {
                    this.timestamp = System.currentTimeMillis();
                } else {
                    this.timestamp = df.parse(dateString).getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
                this.timestamp = System.currentTimeMillis();
            }
        }




        public void update(Map<String, Object> input) {
            updateTimestamp((String)input.get("timestamp"));
            this.text = String.valueOf(Util.resolve(input, "value", ""));
            this.source = "input";
            this.fireUpdate();
        }
        public void update(String v, long ts) {
            this.text = v;
            this.timestamp = ts;
            this.source = "calculated";
            this.fireUpdate();
        }

        boolean isEmpty() {
            return "empty".equals(this.source);
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

    }

    public static class DoubleDataValue extends DataValue {
        double value = 0.0;
        protected double[] values = new double[100];
        protected int ilast = 0;
        protected int ifirst = 0;
        protected double mean;
        protected double stdev;
        protected double min;
        protected double max;

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
            if ( o instanceof Long) {
                this.value = (1.0*(long)o);
            } else if ( o instanceof Double ) {
                this.value = (double) o;
            } else {
                this.value = 0;
            }
            this.calcStats();
            this.fireUpdate();
        }

        public void update(double v, long ts) {
            this.value = v;
            this.timestamp = ts;
            this.source = "calculated";
            this.calcStats();
            this.fireUpdate();
        }
        protected void calcStats() {
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
        protected void calcStats() {

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

        private String state;
        private double heading;

        public PilotDataValue(DataKey k) {
            super(k);
        }

        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            this.state = Util.resolve(input, "state.value", "-");
            this.heading = Util.resolve(input, "target.headingMagnetic.value", 0.0);
            this.fireUpdate();
        }

        public String getState() {
            return state;
        }

        public double getHeading() {
            return heading;
        }
    }



    public static class FixDataValue extends DataValue {

        private String methodQuality;
        private String type;
        private long satellites;
        private String integrity;
        private double horizontalDilution;

        public FixDataValue(DataKey k) {
            super(k);
        }
        public void update(Map<String, Object> input) {
            updateTimestamp((String) input.get("timestamp"));
            this.methodQuality = Util.resolve(input, "methosQuality.value", "-");
            this.horizontalDilution = Util.resolve(input, "horizontalDilution.value", 0.0);
            this.type = Util.resolve(input, "type.value", "-");
            this.satellites = Util.resolve(input, "satellites.value", 0L);
            this.integrity = Util.resolve(input, "integrity.value", "-");
            this.fireUpdate();
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
            this.longitude = Util.resolve(input,"value.longitude", this.longitude);
            this.latitude = Util.resolve(input,"value.latitude", this.latitude);
            this.fireUpdate();
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
            this.drift = Util.resolve(input,"current.value.drift",this.drift);
            this.set = Util.resolve(input,"current.value.setTrue",this.set);
            this.fireUpdate();
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
