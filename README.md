## Trade Buddy Bot + App
This bot tracks watchlists for multiple users and displays a table with clickable buttons to allow users to join discussion rooms

![](trade-buddy-bot.gif)

## Configuration
### Add Application into AC Portal
1. Go to AC Portal > App Management
2. Add Custom App
3. Fill in details and note the APP ID for later
4. Generate RSA key pair and fill in public key
5. Go to AC Portal > App Settings
6. Find the newly-created app and enable it, then make it visible

### Populate configuration files
##### src/main/resources/config.json
* Replace ``appId`` value with ``APP ID`` from step 3
* Replace private key path/name with location of generated key pair from step 4
````json5
{
    // ...
    "appId": "trade-buddy-app",
    "appPrivateKeyPath": "rsa/",
    "appPrivateKeyName": "rsa-private.pem"
}
````

##### src/main/resources/static/bundle.json
* Replace ``id`` value with ``APP ID`` from step 3
* Replace ``url`` and ``icon`` with the deployment location (ignore during dev)
````json5
{
  "applications": [
    {
      // ...
      "id": "trade-buddy-app",
      // ...
      "url": "https://localhost:4000/controller.html",
      "icon": "https://localhost:4000/img/icon.png",
      // ...
    }
  ]
}
````

##### src/main/resources/static/js/controller.js
* Replace ``appId`` with ``APP ID`` from step 3
* Replace ``backendUrl`` with the deployment location (ignore during dev)
````javascript
const appId = 'trade-buddy-bot';
const backendUrl = 'https://localhost:4000';
````

### Development Testing
1. Refresh maven dependencies and run/debug the Spring Boot project
2. Head to https://[your-pod].symphony.com/client/index.html?bundle=https://localhost:4000/bundle.json
3. Go to Symphony Market and install Trade Buddy
4. Note that you will see 2 entries as 1 of them is the one installed in the pod, which is used for app authentication, and the other one is the one injected by the bundle in step 2 above
5. You might need to remove/add from Symphony Market in between code changes. To avoid this, you can rename the app name and id in your project's ``bundle.json`` temporarily during development time. 

### Disclaimer
* This project was created to demonstrate the capabilities of an extension app performing native custom rendering while integrating with the REST API to perform administrative functions like room management
* It does not have a persistence layer so the watchlist data is lost once the bot shuts down and as such, is not recommended for production use as-is
