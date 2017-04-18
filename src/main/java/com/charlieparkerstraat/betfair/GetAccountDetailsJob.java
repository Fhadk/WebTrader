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

import com.betfair.account.api.AccountDetailsResponse;
import com.charlieparkerstraat.Action;
import com.charlieparkerstraat.ActionContext;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_IS_NULL;
import com.charlieparkerstraat.BaseJob;
import com.charlieparkerstraat.Dispatcher;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;

public class GetAccountDetailsJob extends BaseJob {

    private static final Logger LOG = Logger.getLogger(GetAccountDetailsJob.class.getName());

    /**
     * Sample request and response
     *
     * <pre>
     * Request
     * [{
     *      "jsonrpc": "2.0",
     *      "method": "AccountAPING/v1.0/getAccountDetails",
     *      "params": {},
     *      "id": 1
     * }]
     *
     * Response
     * [{
     *      "jsonrpc":"2.0",
     *      "result": {
     *          "currencyCode":"EUR",
     *          "firstName":"Erica",
     *          "lastName":"Kukasira",
     *          "localeCode":"en",
     *          "region":"GBR",
     *          "timezone":"CET",
     *          "discountRate":0.0,
     *          "pointsBalance":777,
     *          "countryCode":"NL"
     *      },
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
                    AccountDetailsResponse accountDetails = client.getAccountDetails();
                    Dispatcher.getInstance(actionContext.getServletContext(), actionContext.getScheduler()).dispatch(accountDetails, AccountDetailsResponse.class);
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
