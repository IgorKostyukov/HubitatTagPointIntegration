# TagPointIntegration app

Instalation and using

1. Install Hubitat Package Manager.   
If you don't have Hubitat Package Manager (HPM) installed yet, please install it on your Hubitat device by following the instructions provided on [https://hubitatpackagemanager.hubitatcommunity.com/](https://hubitatpackagemanager.hubitatcommunity.com/). 
1. Install the app using HPM.
   * Open the control interface for your Hubitat device.
   * Navigate to the "Apps" section.
   * Click "Hubitat Package Manager" (HPM) in the list of available apps.
   * In the HPM interface, click the "Install" button.
   * Click "From URL" option in the list of available options.
   * In the opened page enter the following URL: https://raw.githubusercontent.com/IgorKostyukov/HubitatTagPointIntegration/main/Apps/TagpointIntegration/packageManifest.json
   * Follow the instructions provided by HPM and click "Next" several times until you are redirected to the HPM main page.
   * Return to the "Apps" section in the control interface of your Hubitat device.
   * Click "Add user app" button
   * Select "TagpointIntegration" from the list.
   * On the app main page enter authentification header.
   * Click "Done" to complete the installation.   
1. Manual app update.
   * Open the control interface for your Hubitat device.
   * Select "Hubitat Package Manager" (HPM) from the list of available apps.
   * Navigate to the "Update" section.
   * If a new version is released,  it will be visible in the drop-down list.
   * Follow the instructions provided by HPM and click "Next" several times until you are redirected to the HPM main page.
1. Automatic App Update.
   * Open the control interface for your Hubitat device.
   * Select "Hubitat Package Manager" (HPM) from the list of available apps.
   * Navigate to the "Package Manager Settings" section.
   * In the "Install updates automatically when" drop-down list select "Only those I list"
   * In the "Which packages should be automatically updated?" drop-down list select "TagpointIntegration"
   * You can also set up a device to receive notifications about available updates.
1. Using the app
   * Adding a rule    
     - Open the control interface for your Hubitat device.
     - Click "TagpointIntegration" in the list of available apps.
     - Click "Add new Rule" input
     - Enter the name of the rule
     - Select the triggered parameter (temperature, humidity, etc.) and click "Next"
     - Choose device
     - If the triggered parameter is a number (e.g., temperature), select a comparison type ("greater then" or "less then") and specify the value. The rule will be triggered when the current parameter value is greater then or less then the specified value
     - If triggered parameter is a enum (e.g., smoke), select acceptable values for this parameter. The Rule will be triggered when current the  parameter value is not in the list of acceptable values.
     - Enter the recheck interval in minutes. The rule will be disabled for this duration after each trigger.
     - Enter URL to post data when the rule is triggered.
     - Click "Next"
   * Deleting a rule     
     - Open the control interface for your Hubitat device.
     - Select "TagpointIntegration" in the list of available apps.
     - Find the rule to be deleted in the "Existing rules" list.
     - Click the "Delete" button

# VictoriaMetricsLogger app

1. Install the app using HPM.   
You can install the VictoriaMetricsLogger app using HPM (Hubitat Package Manager). The installation process is very similar to the TagPointIntegration app, with just one difference. When installing VictoriaMetricsLogger, use the following URL for the manifest file: https://raw.githubusercontent.com/IgorKostyukov/HubitatTagPointIntegration/main/Apps/VictoriaMetricsLogger/packageManifest.json
1. Manual app update.
   
   For manual app updates, follow the instructions provided in the "Manual app update" section for the TagPointIntegration app.
1. Automatic App Update.
   
   For automatic app updates, follow the instructions provided in the "Automatic App Update" section for the TagPointIntegration app.
1. Using the app.
   * Open the control interface for your Hubitat device.
   * Click "VictoriaMetricsLogger" in the list of available apps.
   * Select the desired log level (default is "warn")
   * Enter the IP adress of your Victoriametrics server
   * Specify the port of your Victoriametrics server
   * Set the Queue Max Size (default is 5000). Each message from a device is placed in a message queue, and the queue size increments by one with each message. If the VictoriaMetrics server becomes inaccessible, the queue size keeps growing. When it exceeds the specified max size, the queue is cleared, and any messages in the queue are lost.
   * Enter the manual device polling interval in minutes. A message from a device is sent when:
     - the value of a logged parameter is changed
     - The specified polling interval in minutes elapses.
       
     If the manual device polling interval is set to 0, messages are sent only when a logged parameter changes.
   * Finally, select the parameters and devices you want to log.


   
