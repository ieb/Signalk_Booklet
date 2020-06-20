DELETE FROM "handlerIds" WHERE handlerId='uk.co.tfd.kindle.signalk';
DELETE FROM "properties" WHERE handlerId='uk.co.tfd.kindle.signalk';
DELETE FROM "associations" WHERE handlerId='uk.co.tfd.kindle.signalk';

DELETE FROM "mimetypes" WHERE ext='signalk';
DELETE FROM "extenstions" WHERE ext='signalk';
DELETE FROM "properties" WHERE value='Signalk';
DELETE FROM "associations" WHERE contentId='GL:*.signalk';
