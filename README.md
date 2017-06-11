cordova-plugin-onedrivetool
===
> download file from `onedrive approot` or upload file to `onedrive approot`，
> Just a simple encapsulation of `onedrive-sdk-android`

Example
---

### Check file exist

```javascript
let OneDriveTool = cordova.plugins.OneDriveTool;

OneDriveTool.isFileExists(fileName, clientId, scope, (fileExistError, isExist) => {
    if (fileExistError) {
        ...
    } else {
        ...
    }
});
```

### Upload file

```javascript
let OneDriveTool = cordova.plugins.OneDriveTool;

OneDriveTool.saveFile(fileName, content, clientId, scope, (saveError) => {
    if (saveError) {
        ...
    } else {
        ...
    }
});
```

### Download File

```javascript
let OneDriveTool = cordova.plugins.OneDriveTool;

OneDriveTool.downloadFile(fileName, clientId, scope, (downloadError, data) => {
    if (downloadError) {
        ...
    } else {
        ...
    }
});

```

Thanks
---
[onedrive-sdk-android](https://github.com/OneDrive/onedrive-sdk-android)
