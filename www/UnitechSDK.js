
var UnitechSDK = function () {
    // Create new event handlers on the window (returns a channel instance)
    this.channels = {
        scan2keydata: cordova.addWindowEventHandler('scan2keydata'),
    };
    for (var key in this.channels) {
        this.channels[key].onHasSubscribersChange = UnitechSDK.onHasSubscribersChange;
    }
};
function handlers () {
    return unitechsdk.channels.scan2keydata.numHandlers;
}
UnitechSDK.onHasSubscribersChange = function () {
    // If we just registered the first handler, make sure native listener is started.
      if (this.numHandlers === 1 && handlers() === 1) {
        cordova.exec(unitechsdk._scan2keydata, null, 'UnitechSDK', 'start', []);
      } else if (handlers() === 0) {
        cordova.exec(null, null, 'UnitechSDK', 'stop', []);
      }
};
UnitechSDK.prototype._scan2keydata = function (info) {
    if (info) {
        cordova.fireWindowEvent('scan2keydata', info);
    }
};
var unitechsdk = new UnitechSDK(); // jshint ignore:line

module.exports = {
    unitechsdk,
    coolMethod: function (arg0, success, error) {
      cordova.exec(success, error, 'UnitechSDK', 'coolMethod', [arg0]);
    },
    packageName: function (success, error) {
      cordova.exec(success, error, 'UnitechSDK', 'packageName', []);
    },
    serialNumber: function (success, error) {
        cordova.exec(success, error, 'UnitechSDK', 'serialNumber', []);
      },
};

