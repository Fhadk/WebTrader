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

import com.betfair.sports.api.CompetitionResult;
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

public class ListCompetitionsJob extends BaseJob {

    private static final Logger LOG = Logger.getLogger(ListCompetitionsJob.class.getName());

    /**
     * Sample request and response
     *
     * <pre>
     * Request
     * [{
     *      "jsonrpc": "2.0",
     *      "method": "SportsAPING/v1.0/listCompetitions",
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
     *          {"competition":{"id":"4556613","name":"Finnish A-pojat SM U19 Qualification"},"marketCount":23,"competitionRegion":"FIN"},
     *          {"competition":{"id":"12801","name":"Copa Del Rey"},"marketCount":1,"competitionRegion":"ESP"},
     *          {"competition":{"id":"832265","name":"Brazilian Pernambucano"},"marketCount":46,"competitionRegion":"BRA"},
     *          {"competition":{"id":"835595","name":"Brazilian Baiano"},"marketCount":46,"competitionRegion":"BRA"},
     *          {"competition":{"id":"454158","name":"Brazilian Mineiro"},"marketCount":46,"competitionRegion":"BRA"},
     *          {"competition":{"id":"8825999","name":"Paulista A3"},"marketCount":101,"competitionRegion":"BRA"},
     *          {"competition":{"id":"829189","name":"Brazilian Cearense"},"marketCount":37,"competitionRegion":"BRA"},
     *          {"competition":{"id":"11","name":"Dutch Jupiler League"},"marketCount":46,"competitionRegion":"NLD"},
     *          {"competition":{"id":"10532781","name":"Belize Premier League"},"marketCount":23,"competitionRegion":"BLZ"},
     *          {"competition":{"id":"15","name":"Bulgarian Premier"},"marketCount":93,"competitionRegion":"BGR"},
     *          {"competition":{"id":"17","name":"Croatian Division 1"},"marketCount":46,"competitionRegion":"HRV"},
     *          {"competition":{"id":"483094","name":"Brazilian Goiano"},"marketCount":23,"competitionRegion":"BRA"},
     *          {"competition":{"id":"21","name":"Synot Liga"},"marketCount":69,"competitionRegion":"CZE"},
     *          {"competition":{"id":"23","name":"Danish Superliga"},"marketCount":119,"competitionRegion":"DNK"},
     *          {"competition":{"id":"25","name":"Danish Division 1"},"marketCount":138,"competitionRegion":"DNK"},
     *          {"competition":{"id":"821269","name":"Tercera Division"},"marketCount":92,"competitionRegion":"ESP"},
     *          {"competition":{"id":"9404054","name":"Dutch Eredivisie"},"marketCount":170,"competitionRegion":"NLD"},
     *          {"competition":{"id":"3037492","name":"Beloften Pro League"},"marketCount":138,"competitionRegion":"BEL"},
     *          {"competition":{"id":"2519611","name":"Brazilian Paulista A2"},"marketCount":132,"competitionRegion":"BRA"},
     *          {"competition":{"id":"10861755","name":"Istria Cup (W)"},"marketCount":69,"competitionRegion":"FRA"},
     *          {"competition":{"id":"31","name":"English Premier League"},"marketCount":363,"competitionRegion":"GBR"},
     *          {"competition":{"id":"8616348","name":"Friendly"},"marketCount":92,"competitionRegion":"International"},
     *          {"competition":{"id":"7897688","name":"Liga de Ascenso"},"marketCount":23,"competitionRegion":"VEN"},
     *          {"competition":{"id":"35","name":"League One"},"marketCount":100,"competitionRegion":"GBR"},
     *          {"competition":{"id":"6480193","name":"Nicaraguan Premier Division"},"marketCount":46,"competitionRegion":"NIC"},
     *          {"competition":{"id":"8584102","name":"Championnat National U19"},"marketCount":253,"competitionRegion":"FRA"},
     *          {"competition":{"id":"37","name":"League Two"},"marketCount":59,"competitionRegion":"GBR"},
     *          {"competition":{"id":"868392","name":"Russian Youth League"},"marketCount":46,"competitionRegion":"RUS"},
     *          {"competition":{"id":"6294086","name":"Hong Kong 1st Division"},"marketCount":46,"competitionRegion":"HKG"},
     *          {"competition":{"id":"39","name":"National League"},"marketCount":46,"competitionRegion":"GBR"},
     *          {"competition":{"id":"4608865","name":"Thailand Division 2"},"marketCount":23,"competitionRegion":"THA"},
     *          {"competition":{"id":"8594603","name":"Peruvian Primera Division"},"marketCount":92,"competitionRegion":"PER"},
     *          {"competition":{"id":"9513","name":"Segunda Liga"},"marketCount":184,"competitionRegion":"PRT"},
     *          {"competition":{"id":"4905","name":"Liga 1"},"marketCount":130,"competitionRegion":"ROU"},
     *          {"competition":{"id":"1065530","name":"Venezuelan Primera Division"},"marketCount":115,"competitionRegion":"VEN"},
     *          {"competition":{"id":"2609677","name":"Moroccan Division 1"},"marketCount":69,"competitionRegion":"MAR"},
     *          {"competition":{"id":"439085","name":"Brazilian Gaucho"},"marketCount":92,"competitionRegion":"BRA"},
     *          {"competition":{"id":"43","name":"National League South"},"marketCount":23,"competitionRegion":"GBR"},
     *          {"competition":{"id":"5618298","name":"Bulgarian B PFG"},"marketCount":23,"competitionRegion":"BGR"},
     *          {"competition":{"id":"4515700","name":"Luxembourg National Division"},"marketCount":23,"competitionRegion":"LUX"},
     *          {"competition":{"id":"3972877","name":"Panama Primera Division"},"marketCount":67,"competitionRegion":"PAN"},
     *          {"competition":{"id":"8832951","name":"Brazilian Sergipano"},"marketCount":23,"competitionRegion":"BRA"},
     *          {"competition":{"id":"439090","name":"Brazilian Catarinense"},"marketCount":69,"competitionRegion":"BRA"},
     *          {"competition":{"id":"2888729","name":"Armenian Premier League"},"marketCount":46,"competitionRegion":"ARM"},
     *          {"competition":{"id":"1327393","name":"Maltese Premier League"},"marketCount":46,"competitionRegion":"MLT"},
     *          {"competition":{"id":"879931","name":"Chinese Super League"},"marketCount":92,"competitionRegion":"CHN"},
     *          {"competition":{"id":"55","name":"French Ligue 1"},"marketCount":273,"competitionRegion":"FRA"},
     *          {"competition":{"id":"4517491","name":"I Divizion"},"marketCount":23,"competitionRegion":"AZE"},
     *          {"competition":{"id":"57","name":"French Ligue 2"},"marketCount":32,"competitionRegion":"FRA"},
     *          {"competition":{"id":"59","name":"Bundesliga 1"},"marketCount":211,"competitionRegion":"DEU"},
     *          {"competition":{"id":"10150305","name":"A League 2016/17"},"marketCount":47,"competitionRegion":"AUS"},
     *          {"competition":{"id":"61","name":"Bundesliga 2"},"marketCount":125,"competitionRegion":"DEU"},
     *          {"competition":{"id":"63","name":"Regionalliga Nord"},"marketCount":23,"competitionRegion":"DEU"},
     *          {"competition":{"id":"65","name":"Regionalliga Sudwest"},"marketCount":41,"competitionRegion":"DEU"},
     *          {"competition":{"id":"175680","name":"Turkish Division 1"},"marketCount":115,"competitionRegion":"TUR"},
     *          {"competition":{"id":"67","name":"Greek Super League"},"marketCount":156,"competitionRegion":"GRC"},
     *          {"competition":{"id":"439106","name":"Brazilian Paranaense"},"marketCount":46,"competitionRegion":"BRA"},
     *          {"competition":{"id":"845129","name":"Bahrain Premier"},"marketCount":23,"competitionRegion":"BHR"},
     *          {"competition":{"id":"6242330","name":"U23 Internationals (W)"},"marketCount":23,"competitionRegion":"International"},
     *          {"competition":{"id":"43079","name":"Coupe de France"},"marketCount":1,"competitionRegion":"FRA"},
     *          {"competition":{"id":"3061353","name":"Armenian First League"},"marketCount":46,"competitionRegion":"ARM"},
     *          {"competition":{"id":"880458","name":"Russian Division 1"},"marketCount":20,"competitionRegion":"RUS"},
     *          {"competition":{"id":"849220","name":"Icelandic League Cup"},"marketCount":46,"competitionRegion":"ISL"},
     *          {"competition":{"id":"856134","name":"Colombian Primera B"},"marketCount":46,"competitionRegion":"COL"},
     *          {"competition":{"id":"77","name":"Airtricity Premier Division"},"marketCount":1,"competitionRegion":"IRL"},
     *          {"competition":{"id":"81","name":"Serie A"},"marketCount":429,"competitionRegion":"ITA"},
     *          {"competition":{"id":"845150","name":"Indonesian President's Cup"},"marketCount":23,"competitionRegion":"IDN"},
     *          {"competition":{"id":"1874","name":"Coppa Italia"},"marketCount":1,"competitionRegion":"ITA"},
     *          {"competition":{"id":"83","name":"Serie B"},"marketCount":100,"competitionRegion":"ITA"},
     *          {"competition":{"id":"2699898","name":"Algarve Cup (W)"},"marketCount":138,"competitionRegion":"PRT"},
     *          {"competition":{"id":"1517121","name":"Macedonian Football League"},"marketCount":46,"competitionRegion":"MKD"},
     *          {"competition":{"id":"8770515","name":"Mexican Liga Premier"},"marketCount":92,"competitionRegion":"MEX"},
     *          {"competition":{"id":"164952","name":"Belgian Second Division"},"marketCount":23,"competitionRegion":"BEL"},
     *          {"competition":{"id":"833616","name":"Greek Football League"},"marketCount":117,"competitionRegion":"GRC"},
     *          {"competition":{"id":"30558","name":"English FA Cup"},"marketCount":51,"competitionRegion":"GBR"},
     *          {"competition":{"id":"6103298","name":"Tunisian Ligue Professionelle 1"},"marketCount":46,"competitionRegion":"TUN"},
     *          {"competition":{"id":"62815","name":"Copa Libertadores"},"marketCount":20,"competitionRegion":"International"},
     *          {"competition":{"id":"61025","name":"Brazilian Carioca"},"marketCount":24,"competitionRegion":"BRA"},
     *          {"competition":{"id":"97","name":"Ekstraklasa"},"marketCount":70,"competitionRegion":"POL"},
     *          {"competition":{"id":"8423138","name":"Campeonato de Portugal"},"marketCount":299,"competitionRegion":"PRT"},
     *          {"competition":{"id":"99","name":"Primeira Liga"},"marketCount":156,"competitionRegion":"PRT"},
     *          {"competition":{"id":"840808","name":"Regionalliga West"},"marketCount":20,"competitionRegion":"DEU"},
     *          {"competition":{"id":"4556576","name":"Hungarian NB II"},"marketCount":115,"competitionRegion":"HUN"},
     *          {"competition":{"id":"101","name":"Premier Division"},"marketCount":156,"competitionRegion":"RUS"},
     *          {"competition":{"id":"803690","name":"Ecuadorian Primera A"},"marketCount":46,"competitionRegion":"ECU"},
     *          {"competition":{"id":"4488226","name":"Vietnam U19 Championship Qualifications"},"marketCount":161,"competitionRegion":"VNM"},
     *          {"competition":{"id":"827754","name":"Mexican Liga de Ascenso"},"marketCount":44,"competitionRegion":"MEX"},
     *          {"competition":{"id":"105","name":"Scottish Premiership"},"marketCount":3,"competitionRegion":"GBR"},
     *          {"competition":{"id":"107","name":"Scottish Championship"},"marketCount":39,"competitionRegion":"GBR"},
     *          {"competition":{"id":"109","name":"Scottish League One"},"marketCount":24,"competitionRegion":"GBR"},
     *          {"competition":{"id":"824417","name":"Greek U20 Super League"},"marketCount":92,"competitionRegion":"GRC"},
     *          {"competition":{"id":"111","name":"Scottish League Two"},"marketCount":24,"competitionRegion":"GBR"},
     *          {"competition":{"id":"113","name":"Slovakian Super Liga"},"marketCount":46,"competitionRegion":"SVK"},
     *          {"competition":{"id":"5627174","name":"Mexican Primera Division"},"marketCount":161,"competitionRegion":"MEX"},
     *          {"competition":{"id":"4684340","name":"Friendlies (W)"},"marketCount":23,"competitionRegion":"GBR"},
     *          {"competition":{"id":"115","name":"Slovenian Prva Liga"},"marketCount":46,"competitionRegion":"SVN"},
     *          {"competition":{"id":"9969899","name":"Premier League 2"},"marketCount":138,"competitionRegion":"GBR"},
     *          {"competition":{"id":"10226409","name":"Oberliga Niederrhein"},"marketCount":23,"competitionRegion":"DEU"},
     *          {"competition":{"id":"117","name":"Primera Division"},"marketCount":379,"competitionRegion":"ESP"},
     *          {"competition":{"id":"119","name":"Segunda Division"},"marketCount":179,"competitionRegion":"ESP"},
     *          {"competition":{"id":"2490975","name":"Brazilian Paulista A1"},"marketCount":77,"competitionRegion":"BRA"},
     *          {"competition":{"id":"121","name":"Segunda B/1"},"marketCount":23,"competitionRegion":"ESP"},
     *          {"competition":{"id":"20601","name":"Finnish Cup"},"marketCount":23,"competitionRegion":"FIN"},
     *          {"competition":{"id":"89979","name":"Belgian Jupiler League"},"marketCount":1,"competitionRegion":"BEL"},
     *          {"competition":{"id":"123","name":"Segunda B/2"},"marketCount":23,"competitionRegion":"ESP"},
     *          {"competition":{"id":"125","name":"Segunda B/3"},"marketCount":23,"competitionRegion":"ESP"},
     *          {"competition":{"id":"9404402","name":"CAF African U20 Championship"},"marketCount":23,"competitionRegion":"GBR"},
     *          {"competition":{"id":"862579","name":"Romanian Liga II"},"marketCount":23,"competitionRegion":"ROU"},
     *          {"competition":{"id":"3057583","name":"Ecuadorian Primera B"},"marketCount":23,"competitionRegion":"ECU"},
     *          {"competition":{"id":"129","name":"Allsvenskan"},"marketCount":16,"competitionRegion":"SWE"},
     *          {"competition":{"id":"857992","name":"Chilean Primera B"},"marketCount":46,"competitionRegion":"CHL"},
     *          {"competition":{"id":"133","name":"Swiss Super League"},"marketCount":70,"competitionRegion":"CHE"},
     *          {"competition":{"id":"135","name":"Swiss Challenge League"},"marketCount":69,"competitionRegion":"CHE"},
     *          {"competition":{"id":"409743","name":"Scottish Cup"},"marketCount":59,"competitionRegion":"GBR"},
     *          {"competition":{"id":"7654910","name":"Ghana Premier League"},"marketCount":46,"competitionRegion":"GHA"},
     *          {"competition":{"id":"139","name":"Vischya Liga"},"marketCount":101,"competitionRegion":"UKR"},
     *          {"competition":{"id":"403085","name":"Polish 1 Liga"},"marketCount":2,"competitionRegion":"POL"},
     *          {"competition":{"id":"141","name":"MLS"},"marketCount":252,"competitionRegion":"USA"},
     *          {"competition":{"id":"867459","name":"K League"},"marketCount":25,"competitionRegion":"KOR"},
     *          {"competition":{"id":"4848838","name":"Czech U21 League"},"marketCount":184,"competitionRegion":"CZE"},
     *          {"competition":{"id":"2079376","name":"Costa Rican Primera Division"},"marketCount":92,"competitionRegion":"CRI"},
     *          {"competition":{"id":"4540630","name":"Brasiliense"},"marketCount":23,"competitionRegion":"BRA"},
     *          {"competition":{"id":"895129","name":"Azerbaijan Premier"},"marketCount":46,"competitionRegion":"AZE"},
     *          {"competition":{"id":"824729","name":"U20 Campionato Berretti"},"marketCount":23,"competitionRegion":"ITA"},
     *          {"competition":{"id":"876442","name":"Polish 2 Liga"},"marketCount":69,"competitionRegion":"POL"},
     *          {"competition":{"id":"5808320","name":"SPFL Development League"},"marketCount":23,"competitionRegion":"GBR"},
     *          {"competition":{"id":"1062024","name":"J2 League"},"marketCount":92,"competitionRegion":"JPN"},
     *          {"competition":{"id":"8928016","name":"Indian CFA Senior Division League"},"marketCount":46,"competitionRegion":"IND"},
     *          {"competition":{"id":"822165","name":"Israeli Premier"},"marketCount":69,"competitionRegion":"ISR"},
     *          {"competition":{"id":"3085749","name":"Chilean Segunda Division"},"marketCount":16,"competitionRegion":"CHL"},
     *          {"competition":{"id":"6566654","name":"Guatemalan Liga Nacional"},"marketCount":69,"competitionRegion":"GTM"},
     *          {"competition":{"id":"839575","name":"Paraguayan Primera"},"marketCount":69,"competitionRegion":"PRY"},
     *          {"competition":{"id":"6011072","name":"SPFL Development League 2"},"marketCount":69,"competitionRegion":"GBR"},
     *          {"competition":{"id":"862638","name":"Czech 2 Liga"},"marketCount":46,"competitionRegion":"CZE"},
     *          {"competition":{"id":"194215","name":"Turkish Super League"},"marketCount":214,"competitionRegion":"TUR"},
     *          {"competition":{"id":"744098","name":"Chilean Primera"},"marketCount":92,"competitionRegion":"CHL"},
     *          {"competition":{"id":"844197","name":"Colombian Primera A"},"marketCount":202,"competitionRegion":"COL"},
     *          {"competition":{"id":"5984496","name":"Lega Pro"},"marketCount":161,"competitionRegion":"ITA"},
     *          {"competition":{"id":"10615822","name":"Spanish Division de Honor U19"},"marketCount":23,"competitionRegion":"ESP"},
     *          {"competition":{"id":"3785366","name":"Japan Football League"},"marketCount":92,"competitionRegion":"JPN"},
     *          {"competition":{"id":"853948","name":"Regionalliga Bayern"},"marketCount":41,"competitionRegion":"DEU"},
     *          {"competition":{"id":"7550146","name":"National Football League"},"marketCount":46,"competitionRegion":"FJI"},
     *          {"competition":{"id":"843454","name":"Uruguayan Primera"},"marketCount":69,"competitionRegion":"URY"},
     *          {"competition":{"id":"4540663","name":"Panama Liga Nacional de Ascenso"},"marketCount":23,"competitionRegion":"PAN"},
     *          {"competition":{"id":"3765131","name":"Croatian U19 League"},"marketCount":23,"competitionRegion":"HRV"},
     *          {"competition":{"id":"18099","name":"Swedish Cup"},"marketCount":124,"competitionRegion":"SWE"},
     *          {"competition":{"id":"10282031","name":"Oberliga Schleswig-Holstein"},"marketCount":23,"competitionRegion":"DEU"},
     *          {"competition":{"id":"2371216","name":"Jordan 1st Division"},"marketCount":23,"competitionRegion":"JOR"},
     *          {"competition":{"id":"4040072","name":"Georgian Division 1"},"marketCount":92,"competitionRegion":"GEO"},
     *          {"competition":{"id":"3765124","name":"Argentinian Primera B Metropolitana"},"marketCount":23,"competitionRegion":"ARG"},
     *          {"competition":{"id":"158146","name":"3 Liga"},"marketCount":23,"competitionRegion":"DEU"},
     *          {"competition":{"id":"804044","name":"Israeli Liga Leumit"},"marketCount":23,"competitionRegion":"ISR"},
     *          {"competition":{"id":"8718402","name":"Brazilian Alagoano"},"marketCount":23,"competitionRegion":"BRA"},
     *          {"competition":{"id":"6182297","name":"Gibraltar Premier Division"},"marketCount":46,"competitionRegion":"GIB"},
     *          {"competition":{"id":"30921","name":"Danish Cup"},"marketCount":27,"competitionRegion":"DNK"},
     *          {"competition":{"id":"827078","name":"Mexican U20 League"},"marketCount":54,"competitionRegion":"MEX"},
     *          {"competition":{"id":"833990","name":"Hong Kong Premier League"},"marketCount":69,"competitionRegion":"HKG"},
     *          {"competition":{"id":"853446","name":"Saudi Premier"},"marketCount":23,"competitionRegion":"SAU"},
     *          {"competition":{"id":"10479956","name":"Austrian Bundesliga."},"marketCount":24,"competitionRegion":"AUT"},
     *          {"competition":{"id":"10662505","name":"DFB Pokal"},"marketCount":1,"competitionRegion":"DEU"},
     *          {"competition":{"id":"9926489","name":"Dutch Tweede Divisie"},"marketCount":23,"competitionRegion":"NLD"},
     *          {"competition":{"id":"1987280","name":"Vietnam V League 1"},"marketCount":23,"competitionRegion":"VNM"},
     *          {"competition":{"id":"5614746","name":"FIFA World Cup 2018"},"marketCount":10,"competitionRegion":"International"},
     *          {"competition":{"id":"309972","name":"AFC Cup"},"marketCount":69,"competitionRegion":"International"},
     *          {"competition":{"id":"4517524","name":"Liga Nacional"},"marketCount":23,"competitionRegion":"HND"},
     *          {"competition":{"id":"8068520","name":"Netherlands Reserve League"},"marketCount":138,"competitionRegion":"NLD"},
     *          {"competition":{"id":"2005","name":"UEFA Europa League"},"marketCount":25,"competitionRegion":"International"},
     *          {"competition":{"id":"4802204","name":"El Salvadoran Primera Division"},"marketCount":92,"competitionRegion":"SLV"},
     *          {"competition":{"id":"868823","name":"Singapore Prime League"},"marketCount":69,"competitionRegion":"SGP"},
     *          {"competition":{"id":"6812603","name":"Guatemalan Primera Division de Ascenso"},"marketCount":38,"competitionRegion":"GTM"},
     *          {"competition":{"id":"8825958","name":"Cameroon Ligue 1"},"marketCount":92,"competitionRegion":"CMR"},
     *          {"competition":{"id":"2129602","name":"Professional Development League"},"marketCount":161,"competitionRegion":"GBR"},
     *          {"competition":{"id":"228","name":"UEFA Champions League"},"marketCount":232,"competitionRegion":"International"},
     *          {"competition":{"id":"852459","name":"German Junioren Bundesliga"},"marketCount":23,"competitionRegion":"DEU"},
     *          {"competition":{"id":"376041","name":"Indian I League"},"marketCount":23,"competitionRegion":"IND"},
     *          {"competition":{"id":"1842928","name":"Hungarian NB I"},"marketCount":1,"competitionRegion":"HUN"},
     *          {"competition":{"id":"7596190","name":"Croatian 3 HNL"},"marketCount":23,"competitionRegion":"HRV"},
     *          {"competition":{"id":"7129730","name":"The Championship"},"marketCount":607,"competitionRegion":"GBR"},
     *          {"competition":{"id":"3729355","name":"Bolivian Liga Nacional A"},"marketCount":69,"competitionRegion":"BOL"},
     *          {"competition":{"id":"8828275","name":"Carioca U20"},"marketCount":20,"competitionRegion":"BRA"},
     *          {"competition":{"id":"8531829","name":"Greek Division 3"},"marketCount":46,"competitionRegion":"GRC"},
     *          {"competition":{"id":"4646590","name":"Icelandic League Cup (W)"},"marketCount":23,"competitionRegion":"ISL"},
     *          {"competition":{"id":"6569630","name":"Moroccan Division 2"},"marketCount":23,"competitionRegion":"MAR"},
     *          {"competition":{"id":"2938065","name":"Slovakian 1 Liga"},"marketCount":46,"competitionRegion":"SVK"},
     *          {"competition":{"id":"873203","name":"Estonian Esiliiga"},"marketCount":46,"competitionRegion":"EST"}
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
                    final List<CompetitionResult> competitions = client.listCompetitions(filter);
                    Dispatcher.getInstance(actionContext.getServletContext(), actionContext.getScheduler()).dispatch(competitions, CompetitionResult.class);
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
