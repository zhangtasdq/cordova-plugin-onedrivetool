cordova-plugin-aestool
===
> 对 `OneDrive` 中应用目录下的文件操作的 `cordova` 插件，
> 只是对 `onedrive-sdk-android` 的一个简单封装

使用
---

### 检查应用目录文件是否存在
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

### 上传内容到应用目录

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

### 下载应用目录中的文件

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

感谢
---
[onedrive-sdk-android](https://github.com/OneDrive/onedrive-sdk-android)
