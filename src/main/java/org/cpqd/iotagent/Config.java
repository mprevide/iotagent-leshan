package org.cpqd.iotagent;

public class Config {

    private static Config mInstance;

    private String mFileServerAddress;
    private String mFileServerDataPath;
    private int mFileServerHttpPort;
    private int mFileServerHttpsPort;

    private Config() {

        if (System.getenv("FILE_SERVER_ADDRESS") == null) {
            this.mFileServerAddress = "127.0.0.1";
        } else {
            this.mFileServerAddress = System.getenv("FILE_SERVER_ADDRESS");
        }

        if (System.getenv("FILE_SERVER_DATA_PATH") == null) {
            this.mFileServerDataPath = "./data";
        } else {
            this.mFileServerDataPath = System.getenv("FILE_SERVER_DATA_PATH");
        }

        if (System.getenv("FILE_SERVER_HTTP_PORT") == null) {
            this.mFileServerHttpPort = 5896;
        } else {
            this.mFileServerHttpPort = Integer.parseInt(System.getenv("FILE_SERVER_HTTP_PORT"));
        }

        if (System.getenv("FILE_SERVER_HTTPS_PORT") == null) {
            this.mFileServerHttpsPort = 5897;
        } else {
            this.mFileServerHttpsPort = Integer.parseInt(System.getenv("FILE_SERVER_HTTPS_PORT"));
        }

    }

    public static synchronized Config getInstance() {
        if (mInstance == null) {
            mInstance = new Config();
        }
        return mInstance;
    }

    public String getFileServerAddress() {
        return this.mFileServerAddress;
    }

    public String getFileServerDataPath() {
        return this.mFileServerDataPath;
    }

    public int getFileServerHttpPort() {
        return this.mFileServerHttpPort;
    }

    public int getFileServerHttpsPort() {
        return this.mFileServerHttpsPort;
    }
}