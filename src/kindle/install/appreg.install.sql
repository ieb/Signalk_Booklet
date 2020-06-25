INSERT OR IGNORE INTO "handlerIds" VALUES('uk.co.tfd.kindle.signalk');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','lipcId','uk.co.tfd.kindle.signalk');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','jar','/opt/amazon/ebook/booklet/signalk_booklet.jar');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','maxUnloadTime','45');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','maxGoTime','60');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','maxPauseTime','60');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','default-chrome-style','NH');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','unloadPolicy','unloadOnPause');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','extend-start','Y');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','searchbar-mode','transient');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.signalk','supportedOrientation','U');

INSERT OR IGNORE INTO "mimetypes" VALUES('signalk','MT:image/x.signalk');
INSERT OR IGNORE INTO "extenstions" VALUES('signalk','MT:image/x.signalk');
INSERT OR IGNORE INTO "properties" VALUES('archive.displaytags.mimetypes','image/x.signalk','Signalk');
INSERT OR IGNORE INTO "associations" VALUES('com.lab126.generic.extractor','extractor','GL:*.signalk','true');
INSERT OR IGNORE INTO "associations" VALUES('uk.co.tfd.kindle.signalk','application','MT:image/x.signalk','true');
