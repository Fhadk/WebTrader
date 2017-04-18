/*
 * Copyright (C) 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.charlieparkerstraat.betfair;

import com.betfair.sports.api.CountryCodeResult;
import com.betfair.sports.api.MarketFilter;
import com.charlieparkerstraat.Action;
import com.charlieparkerstraat.ActionContext;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_IS_NULL;
import com.charlieparkerstraat.BaseJob;
import com.charlieparkerstraat.Dispatcher;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;

public class ListCountriesJob extends BaseJob {

    private static final Logger LOG = Logger.getLogger(ListCountriesJob.class.getName());

    /**
     * Sample request and response
     *
     * <pre>
     * Request
     * [{
     *      "jsonrpc": "2.0",
     *      "method": "SportsAPING/v1.0/listCountries",
     *      "params": {
     *          "filter":{
     *              "exchangeIds":["1"],
     *              "eventTypeIds":["1"],
     *              "turnInPlayEnabled":true,
     *              "marketBettingTypes":["ODDS"]
     *          },
     *          "locale":"en"
     *      },
     *      "id": 1
     * }]
     *
     * Response
     * [{
     *      "jsonrpc":"2.0",
     *      "result":[
     *          {"countryCode":"PL","marketCount":164},
     *          {"countryCode":"UA","marketCount":101},
     *          {"countryCode":"CH","marketCount":139},
     *          {"countryCode":"GB","marketCount":1789},
     *          {"countryCode":"DK","marketCount":284},
     *          {"countryCode":"BG","marketCount":116},
     *          {"countryCode":"FR","marketCount":559},
     *          {"countryCode":"BE","marketCount":162},
     *          {"countryCode":"RU","marketCount":222},
     *          {"countryCode":"GR","marketCount":411},
     *          {"countryCode":"DE","marketCount":554},
     *          {"countryCode":"PT","marketCount":777},
     *          {"countryCode":"TR","marketCount":329},
     *          {"countryCode":"IT","marketCount":714},
     *          {"countryCode":"ES","marketCount":743},
     *          {"countryCode":"AU","marketCount":47},
     *          {"countryCode":"IE","marketCount":1},
     *          {"countryCode":"US","marketCount":221},
     *          {"countryCode":"BR","marketCount":833},
     *          {"countryCode":"HU","marketCount":208},
     *          {"countryCode":"MX","marketCount":351},
     *          {"countryCode":"GT","marketCount":107},
     *          {"countryCode":"CL","marketCount":155},
     *          {"countryCode":"CO","marketCount":248},
     *          {"countryCode":"PA","marketCount":90},
     *          {"countryCode":"PY","marketCount":69},
     *          {"countryCode":"CR","marketCount":92},
     *          {"countryCode":"SV","marketCount":92},
     *          {"countryCode":"PE","marketCount":94},
     *          {"countryCode":"NI","marketCount":46},
     *          {"countryCode":"FJ","marketCount":46},
     *          {"countryCode":"JP","marketCount":236},
     *          {"countryCode":"HK","marketCount":115},
     *          {"countryCode":"KR","marketCount":25},
     *          {"countryCode":"VN","marketCount":184},
     *          {"countryCode":"CN","marketCount":92},
     *          {"countryCode":"SG","marketCount":69},
     *          {"countryCode":"TH","marketCount":23},
     *          {"countryCode":"CZ","marketCount":299},
     *          {"countryCode":"SK","marketCount":92},
     *          {"countryCode":"AM","marketCount":92},
     *          {"countryCode":"HR","marketCount":92},
     *          {"countryCode":"AZ","marketCount":69},
     *          {"countryCode":"GE","marketCount":92},
     *          {"countryCode":"EE","marketCount":46},
     *          {"countryCode":"NL","marketCount":377},
     *          {"countryCode":"SE","marketCount":140},
     *          {"countryCode":"MK","marketCount":46},
     *          {"countryCode":"TN","marketCount":46},
     *          {"countryCode":"MT","marketCount":46},
     *          {"countryCode":"CM","marketCount":92},
     *          {"countryCode":"IN","marketCount":69},
     *          {"countryCode":"ID","marketCount":23},
     *          {"countryCode":"SI","marketCount":46},
     *          {"countryCode":"GH","marketCount":46},
     *          {"countryCode":"MA","marketCount":92},
     *          {"countryCode":"LU","marketCount":23},
     *          {"countryCode":"VE","marketCount":142},
     *          {"countryCode":"IS","marketCount":69},
     *          {"countryCode":"GI","marketCount":46},
     *          {"countryCode":"AT","marketCount":24},
     *          {"countryCode":"EC","marketCount":69},
     *          {"countryCode":"IL","marketCount":92},
     *          {"countryCode":"SA","marketCount":23},
     *          {"countryCode":"RO","marketCount":153},
     *          {"countryCode":"BO","marketCount":96},
     *          {"countryCode":"UY","marketCount":69},
     *          {"countryCode":"BZ","marketCount":23},
     *          {"countryCode":"HN","marketCount":23},
     *          {"countryCode":"CA","marketCount":31},
     *          {"countryCode":"CY","marketCount":23},
     *          {"countryCode":"SY","marketCount":23},
     *          {"countryCode":"JO","marketCount":46},
     *          {"countryCode":"OM","marketCount":23},
     *          {"countryCode":"IQ","marketCount":23},
     *          {"countryCode":"BH","marketCount":23},
     *          {"countryCode":"FI","marketCount":46},
     *          {"countryCode":"AR","marketCount":29}
     *      ],
     *      "id":1
     * }]
     * </pre>
     *
     * @return
     */
    @Override
    public Action getAction() {
        return (final ActionContext actionContext) -> {
            try {
                final BetfairClient<BetfairClientParameters> client = actionContext.getClient();
                if (client != null) {
                    final MarketFilter filter = new MarketFilter();
                    final List<CountryCodeResult> countries = client.listCountries(filter);
                    Dispatcher.getInstance(actionContext.getServletContext(), actionContext.getScheduler()).dispatch(countries, CountryCodeResult.class);
                } else {
                    LOG.log(Level.WARNING, MSG_BETFAIR_CLIENT_IS_NULL);
                }
            }
            catch (Throwable ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        };
    }
}
