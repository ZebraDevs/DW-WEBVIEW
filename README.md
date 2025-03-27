# DATAWEGDE INTEGRATION INTO A WEBVIEW-BASED APP


- Successfully tested on A8.1, A13, A14 Webview 117, 121 and 122
- Two variants are now supported and each creates its dedicated DW profile
    ![image](https://github.com/ZebraDevs/DW-WEBVIEW/assets/11386676/c343e200-1173-43ea-b23e-c0ba6686aae1)

    ![image](https://github.com/ZebraDevs/DW-WEBVIEW/assets/11386676/192b0f54-0b5d-48c2-b71f-03b8fe0b68bd)
  
- Screenshots

    ![image](https://github.com/NDZL/DW-WEBVIEW/assets/11386676/ada8ffc5-dfee-439c-895d-cccbfd781c90)



## Code annotations about DW Intents and Profiles


- The onCreate method
  
    https://github.com/ZebraDevs/DW-WEBVIEW/blob/2541876d11670e7616d7a9e584a0f8991ae4867a/app/src/main/java/com/zebra/dw_webview/HDLauncherActivity.java#L47 

    defines at the same time
    - a new DW profile

    ![image](https://github.com/ZebraDevs/DW-WEBVIEW/assets/11386676/46f8b878-7c06-4e5b-b155-dd0f58378d9f)

    - and the related Intent Receiver

    ![image](https://github.com/ZebraDevs/DW-WEBVIEW/assets/11386676/c0cf74d4-f71f-41ef-9a33-00fd6b64d1f3)

   for other purposes the received barcode data is then passed to a webview and displayed as HTML text.

- DW profiles have the following features
    - each profile is associated to the respective app variant

        `bundleApp1.putString("PACKAGE_NAME", appName)`

        `val activityName = arrayOf("*")`

        `bundleApp1.putStringArray("ACTIVITY_LIST", activityName)`

    - the IntentOutput plugin is configured to send an intent named after the applicationID (app's package name)
        `bParams.putString("intent_action", appName)`

    - a prefix (1# or 2#) is added to the reading output to differentiate the profile that generated it

        `bundleAllPluginsConfig.add(dwSetPrefixPostfix(context))`

    - the KeystrokeOutput plugin is switched off

        `bundleAllPluginsConfig.add(dwSwitchOffKeystrokeOutput(context))`


## HIDE OVERLAY WINDOWS

![image](https://github.com/user-attachments/assets/277a3ca7-3b9e-4fd6-bdd0-973bbbd0ba3c)

To test this feature:

- ensure the DW profile uses INTERNAL_CAMERA as barcode input source
- keep switch 5 off
- press 4 to activate a DW profile
- press 1 or side scan buttons to trigger scanning. Note that the camera preview is visible
- then set 5
- again scan with 1 or side buttons - appreciate that the scanning completes, but no preview is available
- long press 2 - the system camera preview is brought up. it is not affected by the setHideOverlayWindows API






      

