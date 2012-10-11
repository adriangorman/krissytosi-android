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

package com.krissytosi.api.parse.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.krissytosi.api.domain.Portfolio;
import com.krissytosi.api.parse.PortfolioParser;

public class JsonPortfolioParser implements PortfolioParser {

	@Override
	public List<Portfolio> parsePortfolios(String response) {
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		if (response != null && !"".equalsIgnoreCase(response)) {
			try {
				JSONObject rootJson = new JSONObject(response);
				// TODO - iterate through the response.
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return portfolios;
	}
}