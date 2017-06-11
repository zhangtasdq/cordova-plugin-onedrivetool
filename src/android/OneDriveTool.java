package cordova.plugin.onedrivetool;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.concurrency.IProgressCallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.ISearchCollectionPage;
import com.onedrive.sdk.extensions.Item;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.logger.LoggerLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OneDriveTool extends CordovaPlugin {
    private IOneDriveClient oneDriveClient = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("isFileExists")) {
            String fileName = args.getString(0);
            String clientId = args.getString(1);
            String[] scope = this.coverJSONToStringArray(args.getJSONArray(2));
            this.isFileExists(fileName, clientId, scope, callbackContext);
            return true;
        }

        if (action.equals("saveFile")) {
            String fileName = args.getString(0);
            String content = args.getString(1);
            String clientId = args.getString(2);
            String[] scope = this.coverJSONToStringArray(args.getJSONArray(3));
            this.saveFile(fileName, content, clientId, scope, callbackContext);
            return true;
        }

        if (action.equals("downloadFile")) {
            String fileName = args.getString(0);
            String clientId = args.getString(1);
            String[] scope = this.coverJSONToStringArray(args.getJSONArray(2));
            this.downloadFile(fileName, clientId, scope, callbackContext);
            return true;
        }
        return false;
    }

    private IClientConfig createConfig(final String clientId, final String[] scope) {
        final MSAAuthenticator authenticator = new MSAAuthenticator() {
            @Override
            public String getClientId() {
                return clientId;
            }

            @Override
            public String[] getScopes() {
                return scope;
            }
        };

        final IClientConfig config = DefaultClientConfig.createWithAuthenticator(authenticator);
        config.getLogger().setLoggingLevel(LoggerLevel.Debug);

        return config;
    }

    private void getOneDriveClient(String clientId, String[] scopes, final ICallback<Void> callback) {
        if (this.oneDriveClient == null) {
            final ICallback<IOneDriveClient> authCallback = new ICallback<IOneDriveClient>() {
                @Override
                public void success(IOneDriveClient iOneDriveClient) {
                    oneDriveClient = iOneDriveClient;
                    callback.success(null);
                }

                @Override
                public void failure(ClientException ex) {
                    callback.failure(ex);
                }
            };

            new OneDriveClient.
                    Builder().
                    fromConfig(this.createConfig(clientId, scopes)).
                    loginAndBuildClient(this.cordova.getActivity(), authCallback);
        } else {
            callback.success(null);
        }
    }

    public void isFileExists(final String fileName, String clientId, String[] oneDriveScope, final CallbackContext callbackContext) {
        this.getOneDriveClient(clientId, oneDriveScope, new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                IOneDriveClient client = OneDriveTool.this.oneDriveClient;

                client.getDrive().
                    getSpecial("approot").
                    getSearch(fileName).
                    buildRequest().
                    get(new ICallback<ISearchCollectionPage>() {
                        @Override
                        public void success(ISearchCollectionPage iSearchCollectionPage) {
                            boolean isFileExists = !iSearchCollectionPage.getCurrentPage().isEmpty();
                            callbackContext.success("" + isFileExists);
                        }

                        @Override
                        public void failure(ClientException ex) {
                            callbackContext.error(ex.getMessage());
                            ex.printStackTrace();
                        }
                    });
            }
            @Override
            public void failure(ClientException ex) {
                callbackContext.error(ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    public void saveFile(final String fileName, final String content, String clientId, String[] oneDriveScope, final CallbackContext callbackContext) {
        final IProgressCallback<Item> saveCallback = new IProgressCallback<Item>() {
            @Override
            public void progress(long current, long max) {
            }

            @Override
            public void success(Item item) {
                callbackContext.success("true");
            }

            @Override
            public void failure(ClientException ex) {
                callbackContext.error(ex.getMessage());
                ex.printStackTrace();
            }
        };

        this.getOneDriveClient(clientId, oneDriveScope, new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                IOneDriveClient client = OneDriveTool.this.oneDriveClient;
                byte[] contentByte = content.getBytes();

                client.getDrive().
                    getSpecial("approot").
                    getChildren().
                    byId(fileName).
                    getContent().
                    buildRequest().
                    put(contentByte, saveCallback);
            }

            @Override
            public void failure(ClientException ex) {
                callbackContext.error(ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    public void downloadFile(final String fileName, String clientId, String[] oneDriveScope, final CallbackContext callbackContext) {
        this.getOneDriveClient(clientId, oneDriveScope, new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                IOneDriveClient client = OneDriveTool.this.oneDriveClient;

                try {
                    final InputStream inputStream = client.getDrive().
                                                    getSpecial("approot").
                                                    getChildren().
                                                    byId(fileName).
                                                    getContent().
                                                    buildRequest().
                                                    get();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    while((len = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }

                    inputStream.close();
                    byteArrayOutputStream.close();

                    String data = byteArrayOutputStream.toString();
                    callbackContext.success(data);
                } catch (IOException e) {
                    callbackContext.error(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(ClientException ex) {
                callbackContext.error(ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private String[] coverJSONToStringArray(JSONArray data) {
        String[] result = new String[data.length()];

        for(int i = 0, j = data.length(); i < j; ++i) {
            try {
                result[i] = data.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
