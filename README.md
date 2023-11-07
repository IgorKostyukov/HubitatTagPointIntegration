# TagPointIntegration app

Instalation and using

1. Install Hubitat Package Manager
If you don't have Hubitat Package Manager (HPM) installed yet, please install it on your Hubitat device by following the instructions provided on [https://hubitatpackagemanager.hubitatcommunity.com/](https://hubitatpackagemanager.hubitatcommunity.com/). 
2. Install the app using HPM.
   * Open the control interface for your Hubitat device.
   * Navigate to the "Apps" section.
   * Click "Hubitat Package Manager" (HPM) in the list of available apps.
   * In the HPM interface, click the "Install" button.
   * Click "From URL" option in the list of available options.
   * In the opened page enter the following URL: https://raw.githubusercontent.com/IgorKostyukov/HubitatTagPointIntegration/main/Apps/TagpointIntegration/packageManifest.json
   * Follow HPM instructions and click "Next" button several times until redirection to HPM main page
   * Then navigate to the "Apps" section in the control interface of your Hubitat device.
   * Click "Add user app" button
   * In the opened list select "TagpointIntegration"
   * On the app main page enter authentification header.
   * Click "Done" button.
   * Congratulations! You've successfully installed our application on your Hubitat device.
3. Updating the app manually
   * Open the control interface for your Hubitat device.
   * Click "Hubitat Package Manager" (HPM) in the list of available apps.
   * Navigate to the "Update" section.
   * If a new version released you can see the app in the drop-down list
   * Follow HPM instructions and click "Next" button several times until redirection to HPM main page
4. Updating the app automatically
   * Open the control interface for your Hubitat device.
   * Click "Hubitat Package Manager" (HPM) in the list of available apps.
   * Navigate to the "Package Manager Settings" section.
   * In the drop-down list "Install updates automatically when" select "Only those I list" option
   * In the drop-down list "Which packages should be automatically updated?" select "TagpointIntegration"
   * Also on this page you can set a device to notify about available updates.
5. Using the app
   * Adding a rule    
     - Open the control interface for your Hubitat device.
     - Click "TagpointIntegration" in the list of available apps.
     - Click "Add new Rule" input
     - Enter the name of the rule
     - Select triggered parameter (temperature, humidity, etc.) and click "Next"
     - Select device
     - If triggered parameter is a number (for example, temperature) then select comparison type ("greater then" or "less then") and value. The rule will be triggered when current parameter value is greater then or less then the value
     - If triggered parameter is a enum (for example, smoke) then select acceptable values for this parameter. Rule will be triggered when current parameter value is not in the list of acceptable values.
     - Enter Recheck interval in minutes. Every time the rule is triggered it will be disabled after it during the entered period of time
     - Enter URL to post data when the rule is triggered.
     - Click "Next"
   * Deleting a rule     
     - Open the control interface for your Hubitat device.
     - Click "TagpointIntegration" in the list of available apps.
     - Find the rule to be deleted in the "Existing rules" list.
     - Click "Delete" button
