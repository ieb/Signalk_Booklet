# SignalK EInk Booklet

This is a Kindle Booklet for SignalK that allows you to open a book on a Kindle Paperwhite that displays pages
of graphical instruments that display real time data from a SignalK server on the network.

Kindle UIs are Java Swing applications running inside a lightweight OSGi Framework.

It recognises the mimetype of the "book" and launches the registered Booklet application (SignalkBooklet.java) which then 
reads the json inside the book file, configures the UI with pages of widgets (EInkTextBox.java).

It then either starts network discovery using mDNS to find a Signalk server on the network, or attempts to connect to a 
list of host port combinations from the book json. Kindles come configured with an iptables firewall enabled by default. 
This firewall only allows inbound packets destined for Amazons proprietary services when on the same network as other
amazon devices. The iptables firewall blocks mDNS. Details on how to enable mDNS are below.

When the booklet is running navigation is by finger swipes left and right to change pages.

Multiple books may be added to the kindle over the USB Mount, placed in the documents folder. see config.json for 
an example of configuration.

Dont expect fantastic graphics animations. Kindle screens are great in full sunlight, but the eink technology is very slow
and requires careful slow redrawing to avoid shadows and ink left behind. The Paperwhite 4 is IP68 waterproof and the battery
lasts for many many hours. 

![](screenshots/IMG_20200626_090431.jpg)
![](screenshots/IMG_20200626_090444.jpg)
![](screenshots/IMG_20200626_090450.jpg)
![](screenshots/IMG_20200626_090456.jpg)
![](screenshots/IMG_20200626_090502.jpg)

# Developing

You can run locally (Main.java)

# Kindle Architecture

see https://wiki.mobileread.com/wiki/Kindle_Touch_Hacking#Architecture worth reading to understand the internals before 
trying to use this project.

# Kindle setup

To use this app you need to get a developer key onto the Kindle. Recent Kindles have USBNetworking that allows you to 
login as root over IP (default password mario, as per https://www.sven.de/kindle/), put the root drive into writable and
install your developer key. You can also use packages from https://wiki.mobileread.com/wiki/Kindle_Touch_Hacking#Architecture 
to automate this however you may need to disable firmware updates to prevent your key from being deleted.

All Jars running on a Kindle must be signed by a key loaded onto the kindle so this step is required.

# Developer setup

The maven build produces an installable package that can be installed with the MPRI package installed that is part of KUAL, 
however it has some dependencies. 

## Kindle SDK

AFAIK, Amazon doesn't distribute a Kindle SDK, and I can't distribute it, so you have to extract the API Jars from your Kindle. Copy the contents of 
/opt/amazon/ebook on the Kindle which should be symlinked to opt_amazon_ebook. That tree contains the SDK. This version
of the code is being built against Firmware version 5.10.0.1. Some of the classes are obfuscated and you may have to adjust
the code here if your Firmware is different.

## Java version

The Java version on a Kindle is an embedded JVM based on 1.8 (cvm). It has a cut down list of classes so many standard libraries
wont work. You will find a list of all classes in /usr/java/lib/classlist. The version of Swing is different from
a standard JDK with some methods missing and some behaviours different. Font point sizes dont correlate well to a standard
JDK after taking into account screen resolution. There are a number of Jars loaded into the OSGi framework, however Booklets
do not seem to be loaded as OSGi bundles and the normal classpath resolution process doesnt appear to work. You cant 
embed libraries into a jar and must unpack all the classes into the Jar that are required for a booklet. Simple JSON is 
available, but even simple things like parsing yaml with standard libraries doesnt work.

## Kindle tool

The final packaging and signing is performed using kindletool which can be found here https://github.com/NiLuJe/KindleTool

## Building

Building is achieved using mvn clean install. It will build the jar, build the package and put the result in target


## Installation

Copy the install image onto the kindle and run the MRPI package script. This will install the package and restart the Kindle UI.


eg 

        mvn clean install && \\
           scp  target/Update_Signalk_uk.co.tfd.kindle.signalk_976ed_install.bin  root@192.168.15.244:/mnt/us/mrpackages && \\
           ssh root@192.168.15.244  "/mnt/us/extensions/MRInstaller/bin/mrinstaller.sh launch_installer"


192.168.15.244 is the USBNet IP address of the Kindle when plugged in.

## Logs

slf4j logs to /var/tmp/signalk.log The slf4j settings are in the SignalkBooklet class. They must be set before any SLF4J classes
are created. 

## Booklets

Create files in /mnt/us/documents called .signalk containing json (eg /mnt/us/documents/OnDeck.signalk), 
see src/test/resources/config.json for an example.

## SignalK servers

Statis configuration is achieved with a list of IP and ports in the json file. It will try each one in turn and connect to the first.
Alternatively if you dont specify a list and fix the iptables firewall mDSN will work.

To fix the firewall.

    iptables -A INPUT   -m pkttype --pkt-type multicast -j ACCEPT
    iptables -A FORWARD -m pkttype --pkt-type multicast -j ACCEPT
    iptables -A OUTPUT  -m pkttype --pkt-type multicast -j ACCEPT
    
 to fix the firewall on restart
 
 
     mntroot rw
     vi /etc/sysconfig/iptables
     
     # Add the following lines in each chain
     -A INPUT   -m pkttype --pkt-type multicast -j ACCEPT
     -A FORWARD -m pkttype --pkt-type multicast -j ACCEPT
     -A OUTPUT  -m pkttype --pkt-type multicast -j ACCEPT
     
     # save and mount root ro
     
     mntrood ro
     
  
 # Customisation
  
 The app comes with a set of default datavalues and widgets, however you can configure more datavalues and attach widgets to them. 
 DataValues have a path in the store which input values map to so that data updates with a matching
 path are collected by the data value. Some datavalues may collect updates from multiple paths. The datavalue will have SI units, and a type that determines how its units are treated and displayed.

 instruments display datavalues. An instrument has a key, a widget class and a path in the store that identifies which datavalue it displays.

 Screens are built using default and custom instruments.


 
 
     {
          "servers" : [ // only required if mDNS is firewalled, otherwise omit.
            {
              "host": "192.168.4.1", // first server to probe
              "port": 8375
            },
            {
              "host": "192.168.1.134",
              "port": 8375
            },
            {
              "host": "x43543-3.local",
              "port": 8375
            }
          ],

         "datavalues": {  // customised data values
             "temperature.engine" : { // primary key in store.
                 "paths": [ // optional additonal paths, may be empty, but is required
                         "additonalpath"
                 ],
                 "unit": "M", // units one of RAD, MS, RATIO,M, MAP, K, TEXT
                 "dataType": "depth", // datatype one of SPEED, BEARING, DISTANCE, NONE, RELATIVEANGLE, LATITUDE, LONGITUDE, TEMPERATURE, PERCENTAGE, DEPTH
                 "description": "example",
                 "dataClass": "DoubleDataValue" // one of DataValue, DoubleDataValue, CircularDataValue,
                                                // PilotDataValue, FixDataValue, PossitionDataValue, 
                                                // CurrentDataValue, AttitudeDataValue
             }
         },
         "instruments": { // customised instruments
             "awscorrected": {
                "widget": "EInkTemperature", // one of EInkTextBox, EInkAttitide, EInkBearing, EInkCurrent, EInkDepth, EInkDistance, 
                // EInkLOg, EInkPilot, EInkPossiton, EInkRatio,
                // EInkRelativeAngle, EInkSpeed, EInkTemperature
                "path": "temperature.engine" // Path to data in datavalues.
 
             }
 
         }
 
     }
  "screensize": { "w":1072, "h":1448 }, // only for non kindle displays
  "pages" : [  // list of pages
    {
        /*
        the values of instruments may be one of the following and any custom instruments defined.

        awa, Apparent Wind Angle
        aws, Apparent Wind Speed
        twa, True Wind Angle
        tws, True Wind Speed
        stw, Speed Through Water
        dbt, Depth Below Transducer
        vmg, Velocity Made good into or down wind.
        var, Variation
        hdt, Heading True
        cogm, Course over ground magnetic
        hdm, Heading True
        lee, Leeway (angle)
        pstw, Polar Speed Through Water
        psratio, Polar Speed Ratio 
        pvmg, Polar VMG
        ttwa, target optional true wind angle upwind or downwind
        tstw, target stw at ttwa
        tvmg, target vmg at ttwa
        pvmgr, polar vmg ratio
        twdt, true wind direction true
        twdm, true wind direction magnetic
        tackt, heading on opposite tack true
        tackm, heading on opposite tack magnetic
        ophdm, target opposite heading true
        cogt, course over ground true
        rot, rate of turn
        rudder, rudder angle
        sog, speed over grount
        twater, water temperature
        stwref, stw sensor type
        blank, blank widget
        log,  log
        attitude, pitch and roll
        current, set and drift
        fix, gps fix information
        pilot, pilot information
        position, possition 
        */


      "instruments" : [  // rows and columns of instruments, using custom and pre-defined widgets.
        [ "awa", "twa", "stw", "psratio" ], 
        [ "aws", "tws", "pstw", "pvmg" ], 
        [ "cogt", "sog", "attitude", "lee" ], 
        [ "position", "fix", "log", "dbt" ],
        [ "position", "fix", "log", "dbt" ]
      ],
      "vspace" : 5, // space between widgets
      "hspace" : 5,
      "id" : "page1",  // page ID
      "rotate": true // if true, rotate into landscape.
    },
    {
      "vspace" : 5,
      "instruments" : [
        [ "awa" ],
        [ "aws" ]
      ],
      "id" : "page2",
      "hspace" : 5
    },
    {
      "id" : "page3",
      "hspace" : 5,
      "vspace" : 5,
      "instruments" : [
        [ "awa", "twa" ],
        [ "blank", "blank" ],
        [ "blank", "blank" ],
        [ "blank", "blank" ]
      ]
    },
    {
      "id" : "page6",
      "hspace" : 5,
      "vspace" : 5,
      "rotate": true,
      "instruments" : [
        [ "awa", "twa" ],
        [ "blank", "blank" ]
      ]
    }
  ]




  