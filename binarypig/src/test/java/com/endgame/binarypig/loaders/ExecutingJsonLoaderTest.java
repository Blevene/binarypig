/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *  Copyright 2013 Endgame Inc.
 *
 */
package com.endgame.binarypig.loaders;

import junit.framework.TestCase;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.NonSpillableDataBag;
import org.apache.pig.data.Tuple;

public class ExecutingJsonLoaderTest extends TestCase {
	
	ExecutingJsonLoader underTest;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		underTest = new ExecutingJsonLoader("src/test/resources/scho.sh");
	}
	
	public void testIt() throws ExecException {
		
		Tuple tuple = underTest.outputToTuple(new Text("mykey"), new BytesWritable("data".getBytes()), "{}", false);
		assertEquals(3, tuple.size());
		assertEquals("mykey", tuple.get(0));
		assertEquals(false, tuple.get(1));
		assertEquals(new java.util.HashMap<>(), tuple.get(2));
		
		tuple = underTest.outputToTuple(new Text("mykey"), new BytesWritable("data".getBytes()), "{\"name\":\"val1\"}", false);
		assertEquals(3, tuple.size());
		assertEquals("mykey", tuple.get(0));
		assertEquals(false, tuple.get(1));
		assertEquals(new java.util.HashMap<String, Object>(){{
			put("name", "val1");
		}}, tuple.get(2));
		
		tuple = underTest.outputToTuple(new Text("mykey"), new BytesWritable("data".getBytes()), "{\"name\":\"val1\", MALFORMED}", false);
		assertEquals(3, tuple.size());
		assertEquals("mykey", tuple.get(0));
		assertEquals(false, tuple.get(1));
		assertEquals(new NonSpillableDataBag(), tuple.get(2));
	}
}

