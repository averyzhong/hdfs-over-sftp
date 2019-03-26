package com.avery.hdfs.sftp.conf;

/**
 * @author AveryZhong.
 */

public class SftpServerConf {
    private String mHost;
    private int mPort;
    private String mHdfsUri;

    public String getHost() {
        return mHost;
    }

    public void setHost(final String host) {
        mHost = host;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(final int port) {
        mPort = port;
    }

    public String getHdfsUri() {
        return mHdfsUri;
    }

    public void setHdfsUri(final String hdfsUri) {
        mHdfsUri = hdfsUri;
    }
}
