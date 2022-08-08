# Getting Started
# cordova-plugin-unitechsdk
This plugin provides an implementation to access unitechsdk for unitech Android device. It adds the following event  to the `window` object and methods:

* event: scan2keydata
* method: serialNumber

Applications may use `window.addEventListener` to attach an event listener for any of the above events after the `deviceready` event fires.

## Installation

    cordova plugin add https://github.com/andophine/cordova-plugin-unitechsdk.git

## scan2data event

Fires when the unitech scanner read data successfully.

### Example

    window.addEventListener("scan2keydata", onScan2Data, false);

    function onScan2Data(data) {
        console.log("scan2data: " + data);
    }

## serialNumber method
get unitech device serial number

### Example
    navigator.unitechsdk.serialNumber(function(echoValue){ console.log(echoValue);},null);
    
## configScan2Data
Change scan action and extra key to listen.

### Example
    navigator.unitechsdk.configScan2Data("android.intent.ACTION_DECODE_DATA","barcode_string");
