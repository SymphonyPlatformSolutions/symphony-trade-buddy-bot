// replace with value of app id for authentication
const appId = 'trade-buddy-bot';
// replace with deployment location
const backendUrl = 'https://localhost:4000';

const tradeBuddyName = 'tradeBuddyApp:controller';
const tradeBuddyService = SYMPHONY.services.register(tradeBuddyName);

let appToken = undefined;
let appTokenPromise = fetch(`${backendUrl}/appToken`)
    .then(res => res.text())
    .then(token => {
        appToken = token;
    });

Promise.all([ appTokenPromise, SYMPHONY.remote.hello()]).then(function() {
    SYMPHONY.application
    .register(
        { appId: appId, tokenA: appToken },
        [ 'entity', 'extended-user-info' ],
        [ tradeBuddyName ]
    )
    .then(function() {
        const entityService = SYMPHONY.services.subscribe('entity');
        entityService.registerRenderer('com.symphony.ps.watchlist', {}, tradeBuddyName);

        const extendedUserInfoService = SYMPHONY.services.subscribe('extended-user-info');
        let jwt = undefined;
        let currentUser = undefined;
        extendedUserInfoService.getJwt().then(response => {
            jwt = response;
            const mid = response.split('.')[1]
                .replace(/-/g, '+')
                .replace(/_/g, '/');
            currentUser = JSON.parse(window.atob(mid))['user'];
        });

        tradeBuddyService.implement({
            render: function (type, entityData) {
                const entityInstanceId = entityData['renderId'];
                const data = {
                    mention: { id: entityData['userId'], name: entityData['userDisplayName'] }
                };

                entityData['watchlist'].forEach(item => {
                    const symbol = item['symbol'];
                    const companyName = item['companyName'];

                    data[symbol] = {
                        label: `Discuss $${item['symbol']}`,
                        service: tradeBuddyName,
                        data: { symbol, companyName, entityInstanceId, entityData }
                    };
                    data[`cashtag-${symbol}`] = `$${symbol}`;
                });

                const dataItems = entityData['watchlist']
                    .map(item => {
                        const escapedCompanyName = item['companyName'].replace(/&/g, '&amp;');
                        const escapedSymbol = item['symbol'].replace(/&/g, '&amp;');
                        return `<tr>
                            <td style="padding-bottom: 5px"><cashtag id="cashtag-${escapedSymbol}" /></td>
                            <td>${escapedCompanyName}</td>
                            <td>${item['latestPrice']}</td>
                            <td>${this.getChangeEmoji(item['change'])} ${this.formatChangeText(item['change'])}</td>
                            <td><action class="button medium" id="${item['symbol']}" /></td>
                        </tr>`
                    })
                    .join('');

                let template = `
                    <entity>
                        <b>Watchlist for <mention id="mention" /></b>
                        <table>
                            <tr>
                                <th>Ticker</th><th>Company</th><th>Price</th><th>Change</th><th>Discuss</th>
                            </tr>
                            ${dataItems}
                        </table>
                    </entity>`;
                return JSON.parse(JSON.stringify({ data, template, entityInstanceId }));
            },
            getChangeEmoji: function(change) {
                const code = (change > 0) ? 'chart_with_upwards_trend' : (change < 0) ? 'chart_with_downwards_trend' : 'left_right_arrow';
                return `<img class="emoji" alt="${code}" src="./img/emoji/${code}.png" />`;
            },
            formatChangeText: function(change) {
                const polarity = (change > 0) ? "+" : (change < 0) ? "" : "&nbsp;";
                const colour = (change > 0) ? "green" : (change < 0) ? "red" : "black";
                return `<span style="color:${colour}">${polarity}${change}</span>`;
            },
            action: function(data) {
                fetch(`${backendUrl}/addToRoom`, {
                    method: 'POST',
                    body: JSON.stringify({
                        symbol: data.symbol,
                        companyName: data.companyName
                    }),
                    headers: {
                        'Content-Type': 'application/json',
                        'token': jwt
                    }
                })
                .then(res => res.text())
                .then(res => {
                    console.log("Room Opened");
                })
                .catch(error => console.error('Error:', error));
            }
        });
    });
});
