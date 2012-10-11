/*
   Copyright 2012 Sean O' Shea

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.krissytosi.api.parse;

import com.krissytosi.api.parse.json.JsonParserFactory;

public class ParserFactoryImpl {

	private static ParserFactory instance;

	protected ParserFactoryImpl() {

	}

	public static ParserFactory getInstance() {
		if (instance == null) {
			// TODO - if the server returned anything other than json, this is
			// where we'd
			// make a decision on what parser to use.
			synchronized (ParserFactoryImpl.class) {
				instance = new JsonParserFactory();
			}
		}
		return instance;
	}
}