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

package com.krissytosi.api.services.http;

import com.krissytosi.api.domain.Portfolio;
import com.krissytosi.api.modules.ApiModule;
import com.krissytosi.api.parse.PortfolioParser;
import com.krissytosi.api.services.PortfolioService;

import dagger.ObjectGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP specific implementation of a {@link PortfolioService}.
 */
public class PortfolioServiceImpl extends HttpService implements
        PortfolioService {

    private PortfolioParser parser;

    /**
     * The base url which this service is targeting.
     */
    private String baseUrl;

    @Override
    public List<Portfolio> getPortfolios() {
        // prepare the options.
        Map<String, String> options = new HashMap<String, String>();
        options.put("q", "portfolios");
        // prepare the url
        String portfolioUrl = createUrl(baseUrl, options);
        // execute the request
        String response = doGet(portfolioUrl);
        // parse & return the response
        return getPortfolioParser().parsePortfolios(response);
    }

    // Getters/Setters

    private PortfolioParser getPortfolioParser() {
        if (parser == null) {
            ObjectGraph objectGraph = ObjectGraph.create(new ApiModule());
            parser = objectGraph.get(PortfolioParser.class);
        }
        return parser;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}