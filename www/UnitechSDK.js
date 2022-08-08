
var UnitechSDK = function () {
    // Create new event handlers on the window (returns a channel instance)
    this.channels = {
        scan2data: cordova.addWindowEventHandler('scan2data'),
    };
    for (var key in this.channels) {
        this.channels[key].onHasSubscribersChange = UnitechSDK.onHasSubscribersChange;
    }
};
function handlers () {
    return unitechsdk.channels.scan2data.numHandlers;
}
var scan2dataAct = "unitech.scanservice.data";
var scan2dataExtra = "text";
UnitechSDK.onHasSubscribersChange = function () {
    // If we just registered the first handler, make sure native listener is started.
      if (this.numHandlers === 1 && handlers() === 1) {
        cordova.exec(unitechsdk._scan2data, null, 'UnitechSDK', 'start', [scan2dataAct,scan2dataExtra]);
      } else if (handlers() === 0) {
        cordova.exec(null, null, 'UnitechSDK', 'stop', []);
      }
};
UnitechSDK.prototype._scan2data = function (info) {
    if (info) {
        cordova.fireWindowEvent('scan2data', info);
    }
};
var unitechsdk = new UnitechSDK(); // jshint ignore:line

module.exports = {
    unitechsdk,
    configScan2Data: function(act,extra){
      scan2dataAct = act;
      scan2dataExtra = extra;
    },
    packageName: function (success, error) {
      cordova.exec(success, error, 'UnitechSDK', 'packageName', []);
    },
    serialNumber: function (success, error) {
      cordova.exec(success, error, 'UnitechSDK', 'serialNumber', []);
    },
};

