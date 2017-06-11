var exec = require('cordova/exec');

exports.isFileExists = function(fileName, clientId, scope, callback) {
    exec(
      function(result) {
          if (result === "true") {
              callback(null, true);
          } else {
              callback(null, false);
          }
      },

      function (error) {
          callback(error);
      },
      "OneDriveTool",
      "isFileExists",
      [fileName, clientId, scope]
    )
}

exports.saveFile = function(fileName, content, clientId, scope, callback) {
    exec(
      function(result) {
          callback(null, result);
      },

      function (error) {
          callback(error);
      },
      "OneDriveTool",
      "saveFile",
      [fileName, content, clientId, scope]
    )
}

exports.downloadFile = function(fileName, clientId, scope, callback) {
    exec(
      function(result) {
          callback(null, result);
      },

      function (error) {
          callback(error);
      },
      "OneDriveTool",
      "downloadFile",
      [fileName, clientId, scope]
    )
}
