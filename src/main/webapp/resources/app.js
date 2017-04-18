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

Ext.require(['Ext.ux.data.proxy.WebSocket']);

Ext.onReady(function () {
    Ext.define('model', {
        extend: 'Ext.data.Model',
        fields: ['date', 'value'],
        proxy: {
            type: 'websocket',
            storeId: 'dataStore',
//            url: 'ws://localhost:8084/webtrader/websocket',
            url: 'ws://localhost:8084/webtrader/getmarkets',
            reader: {
                type: 'json',
                root: 'data'
            }
        }
    });
    var dataStore = Ext.create('Ext.data.Store', {
        model: 'model',
        storeId: 'dataStore'
    });
    var chart = Ext.create('Ext.chart.Chart', {
        renderTo: 'div-chart',
        title: 'Chart',
        width: 500,
        height: 300,
        store: dataStore,
        axes: [{
                type: 'Time',
                dateFormat: 'H:i',
                step: [Ext.Date.MINUTE, 1],
                position: 'bottom',
                fields: ['date']
            }, {
                type: 'Numeric',
                position: 'left',
                minimum: 0,
                fields: ['value']
            }],
        series: [{
                type: 'line',
                axes: 'left',
                xField: 'date',
                yField: 'value',
                markerConfig: {type: 'circle', size: 4, radius: 4, 'stroke-width': 0}
            }]
    });
});