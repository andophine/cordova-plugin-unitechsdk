# Getting Started
# cordova-plugin-unitechsdk
This plugin provides an implementation to access unitechsdk for unitech Android device. It adds the following event  to the `window` object and methods:

* event: scan2keydata
* method: serialNumber

Applications may use `window.addEventListener` to attach an event listener for any of the above events after the `deviceready` event fires.

## Installation

    cordova plugin add https://github.com/andophine/cordova-plugin-unitechsdk.git

## scan2keydata event

Fires when the unitech scanner read data successfully.

### Example

    window.addEventListener("scan2keydata", onScan2KeyData, false);

    function onScan2KeyData(data) {
        console.log("scan2key: " + data);
    }

## serialNumber method
get unitech device serial number

### Example
    navigator.unitechsdk.serialNumber(function(echoValue){ console.log(echoValue);},null);