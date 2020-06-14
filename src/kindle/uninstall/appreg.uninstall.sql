DELETE FROM "handlerIds" WHERE handlerId='uk.co.tfd.kindle.helloworld';
DELETE FROM "properties" WHERE handlerId='uk.co.tfd.kindle.helloworld';
DELETE FROM "associations" WHERE handlerId='uk.co.tfd.kindle.helloworld';

DELETE FROM "mimetypes" WHERE ext='hello';
DELETE FROM "extenstions" WHERE ext='hello';
DELETE FROM "properties" WHERE value='HelloWorld';
DELETE FROM "associations" WHERE contentId='GL:*.hello';
