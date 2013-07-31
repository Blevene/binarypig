-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements. See the NOTICE file distributed with this
-- work for additional information regarding copyright ownership. The ASF
-- licenses this file to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
-- http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
-- WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
-- License for the specific language governing permissions and limitations under
-- the License.
--
--  Copyright 2013 Endgame Inc.

%default ES_JAR_DIR '/usr/local/share/elasticsearch/lib'
%default ES_PLUGINS_DIR '/data0/elasticsearch/plugins'
%default ES_CONFIG     'elasticsearch/elasticsearch.yml'
%default INDEX      'binarypig'
%default OBJ        'pehash'
%default BATCHSIZE 1000

%default INPUT 'malware.seq'
%default OUTPUT 'hashing.out'
%default TIMEOUT_MS '180000'

register 'binarypig/target/binarypig-1.0-SNAPSHOT-jar-with-dependencies.jar';
register 'lib/wonderdog-1.0-SNAPSHOT.jar'
register $ES_JAR_DIR/*.jar; -- */

SET mapred.map.tasks.speculative.execution false;
SET mapred.job.reuse.jvm.num.tasks         1
SET mapred.cache.files /tmp/scripts#scripts;
SET mapred.create.symlink yes;

data = load '$INPUT' using com.endgame.binarypig.loaders.pehash.HashingLoader('$TIMEOUT_MS')
    as (filename:chararray, timeout:chararray, md5:chararray, sha1:chararray, sha256:chararray, sha512:chararray, pe_hash:chararray);

STORE data INTO 
   'es://$INDEX/$OBJ?id=filename&json=false&size=$BATCHSIZE' USING 
    com.infochimps.elasticsearch.pig.ElasticSearchStorage('$ES_CONFIG', '$ES_PLUGINS_DIR');

-- Now try...
-- curl -XGET "localhost:9200/binarypig/pehash/_search?pretty=true&size=1"
