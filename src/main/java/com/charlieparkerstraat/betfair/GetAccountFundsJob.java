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

import com.betfair.account.api.AccountFundsResponse;
import com.betfair.account.api.Wallet;
import com.charlieparkerstraat.Action;
import com.charlieparkerstraat.ActionContext;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_IS_NULL;
import com.charlieparkerstraat.BaseJob;
import com.charlieparkerstraat.Dispatcher;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;

public class GetAccountFundsJob extends BaseJob {

    private static final Logger LOG = Logger.getLogger(GetAccountFundsJob.class.getName());

    /**
     * Sample request response.
     * <pre>
     * Request
     * [{
     *      "jsonrpc": "2.0",
     *      "method": "AccountAPING/v1.0/getAccountFunds",
     *      "params": {
     *          "wallet":"UK"
     *      },
     *      "id": 1
     * }]
     *
     * Response
     * [{
     *      "jsonrpc":"2.0",
     *      "result": {
     *          "availableToBetBalance":1641.26,
     *          "exposure":-3416.475,
     *          "retainedCommission":0.0,
     *          "exposureLimit":-10000.0,
     *          "discountRate":0.0,
     *          "pointsBalance":777,
     *          "wallet":"UK"
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
                    Dispatcher.getInstance(actionContext.getServletContext(), actionContext.getScheduler()).dispatch(client.getAccountFunds(Wallet.UK), AccountFundsResponse.class);
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
