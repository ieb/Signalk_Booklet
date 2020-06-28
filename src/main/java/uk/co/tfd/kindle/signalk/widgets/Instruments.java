package uk.co.tfd.kindle.signalk.widgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.signalk.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class Instruments {
    private static final Logger log = LoggerFactory.getLogger(Instrument.class);
    private final Instrument<EInkTextBox> blank;
    Map<String, Instrument> map = new HashMap<String, Instrument>();




    public void addConfiguration(Map<String, Object> configuration) {
        // any custom instruments here.
        if (configuration.containsKey("instruments")) {
            Map<String, Map<String, Object>> instruments = (Map<String, Map<String, Object>>) configuration.get("instruments");
            for(Map.Entry<String, Map<String, Object>> e :  instruments.entrySet() ) {
                Map<String, Object> instrument = e.getValue();
                try {
                    Class<? extends EInkTextBox> widgeClass = (Class<? extends EInkTextBox>) Class.forName("uk.co.tfd.kindle.signalk.widgets." + instrument.get("widget"));
                    map.put(e.getKey(), new Instrument(e.getKey(), widgeClass, (String) instrument.get("path")));
                } catch (Exception ex) {
                    log.error("Failed to add {} {} ",e.getKey(), ex.getMessage());
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }


    public static class Instrument<T extends EInkTextBox> {
        private final Class<T> widget;
        private final Map<String, Object> options;
        private final Constructor<T> constructor;

        public Instrument(Class<T> widget) throws NoSuchMethodException {
            this("",widget);
        }


        public Instrument(String name, Class<T> widget, String ... sources) throws NoSuchMethodException {
            this.widget = widget;
            this.options = new HashMap<String, Object>();
            Map<String, Object> labels = new HashMap<String, Object>();
            labels.put("bl",name);
            options.put("labels", labels);
            if ( sources != null) {
                options.put("sources", Arrays.asList(sources));
            }
            this.constructor = this.widget.getConstructor(boolean.class, Map.class, Data.DisplayUnits.class, Data.Store.class);

        }
        public T create(boolean rotation, Data.DisplayUnits displayUnits, Data.Store store) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return constructor.newInstance(rotation, this.options, displayUnits, store);

        }
    }
    public Instruments() throws NoSuchMethodException {
        map.put("awa", new Instrument("awa", EInkRelativeAngle.class, "environment.wind.angleApparent"));
        map.put("aws", new Instrument("aws", EInkSpeed.class, "environment.wind.speedApparent"));
        map.put("twa", new Instrument("twa", EInkRelativeAngle.class, "environment.wind.angleTrueWater"));
        map.put("tws", new Instrument("tws", EInkSpeed.class, "environment.wind.speedTrue"));
        map.put("stw", new Instrument("stw", EInkSpeed.class, "navigation.speedThroughWater"));
        map.put("dbt", new Instrument("dbt", EInkDepth.class, "environment.depth.belowTransducer"));
        map.put("vmg", new Instrument("vmg", EInkSpeed.class, "performance.vmg"));


        map.put("var", new Instrument("var", EInkRelativeAngle.class, "navigation.magneticVariation"));
        map.put("hdt", new Instrument("hdt", EInkBearing.class, "navigation.headingTrue"));
        map.put("cogm", new Instrument("cogm", EInkBearing.class, "navigation.courseOverGroundMagnetic"));
        map.put("hdm", new Instrument("hdm", EInkBearing.class, "navigation.headingMagnetic"));
        map.put("lee", new Instrument("leeway", EInkRelativeAngle.class, "performance.leeway"));
        map.put("pstw", new Instrument("polar stw", EInkSpeed.class, "performance.polarSpeed"));
        map.put("psratio", new Instrument("psratio", EInkRatio.class, "performance.polarSpeedRatio"));
        map.put("pvmg", new Instrument("polar vmg", EInkSpeed.class, "performance.polarVmg"));
        map.put("ttwa", new Instrument("ttwa", EInkRelativeAngle.class, "performance.targetTwa"));
        map.put("tstw", new Instrument("tstw", EInkSpeed.class, "performance.targetStw"));
        map.put("tvmg", new Instrument("tvmg", EInkSpeed.class, "performance.targetVmg"));
        map.put("pvmgr", new Instrument("polar vmg ratio", EInkRatio.class, "performance.polarVmgRatio"));
        map.put("twdt", new Instrument("twdt", EInkBearing.class, "environment.wind.windDirectionTrue"));
        map.put("twdm", new Instrument("twdm", EInkBearing.class, "environment.wind.windDirectionMagnetic"));
        map.put("tackt", new Instrument("tackt", EInkBearing.class, "performance.oppositeTrackTrue"));
        map.put("tackm", new Instrument("tackm", EInkBearing.class, "performance.oppositeTrackMagnetic"));
        map.put("ophdm", new Instrument("ophdm", EInkBearing.class, "performance.oppositeHeadingMagnetic"));
        map.put("cogt", new Instrument("cogt", EInkBearing.class, "navigation.courseOverGroundTrue"));
        map.put("rot", new Instrument("rot", EInkRelativeAngle.class, "navigation.rateOfTurn"));
        map.put("rudder", new Instrument("rudder", EInkRelativeAngle.class, "steering.rudderAngle"));
        map.put("sog", new Instrument("sog", EInkSpeed.class, "navigation.speedOverGround"));
        map.put("twater", new Instrument("water temp", EInkTemperature.class, "environment.water.temperature"));
        map.put("stwref", new Instrument("stwref", EInkTextBox.class, "navigation.speedThroughWaterReferenceType"));
        blank = new Instrument("blank",EInkTextBox.class);
        /*
        - navigation.trip.log
        - navigation.log

         */
        map.put("log", new Instrument(EInkLog.class));
        /*
        - navigation.attitude

         */
        map.put("attitude", new Instrument(EInkAttitude.class));
        /*
        - environment.current
         */
        map.put("current", new Instrument(EInkCurrent.class));
        /*
        navigation.gnss
         */
        map.put("fix", new Instrument(EInkFix.class));
        /*
         - steering.autopilot
         */
        map.put("pilot", new Instrument(EInkPilot.class));
        /*
        - navigation.datetime
        - navigation.gnss
         */
        map.put("position", new Instrument(EInkPossition.class));
    }

    public EInkTextBox create(String key, boolean rotation, Data.DisplayUnits displayUnits, Data.Store store) {
        Instrument i = map.get(key);
        if ( i != null ) {
            try {
                return i.create(rotation, displayUnits, store);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                log.error(e.getMessage(), e);
            } catch (InstantiationException e) {
                log.error(e.getMessage(), e);
            }
        }
        try {
            return blank.create(rotation, displayUnits, store);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }




}
