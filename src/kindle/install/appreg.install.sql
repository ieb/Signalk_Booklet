INSERT OR IGNORE INTO "handlerIds" VALUES('uk.co.tfd.kindle.helloworld');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','lipcId','uk.co.tfd.kindle.helloworld');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','jar','/opt/amazon/ebook/booklet/HelloWorldBooklet.jar');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','maxUnloadTime','45');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','maxGoTime','60');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','maxPauseTime','60');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','default-chrome-style','NH');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','unloadPolicy','unloadOnPause');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','extend-start','Y');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','searchbar-mode','transient');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.helloworld','supportedOrientation','U');

INSERT OR IGNORE INTO "mimetypes" VALUES('hello','MT:image/x.hello');
INSERT OR IGNORE INTO "extenstions" VALUES('hello','MT:image/x.hello');
INSERT OR IGNORE INTO "properties" VALUES('archive.displaytags.mimetypes','image/x.hello','HelloWorld');
INSERT OR IGNORE INTO "associations" VALUES('com.lab126.generic.extractor','extractor','GL:*.hello','true');
INSERT OR IGNORE INTO "associations" VALUES('uk.co.tfd.kindle.helloworld','application','MT:image/x.hello','true');
