package com.avery.hdfs.sftp.conf;

import com.avery.hdfs.sftp.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * @author AveryZhong.
 */

public class Configured implements Configurable {
    private static final Logger sLogger = LoggerFactory.getLogger(Configured.class);
    private static final String SERVER_CONF_FILE_PATH = "/hdfs-over-sftp.properties";
    private static final String USERS_CONF_FILE_PATH = "/users.properties";
    private SftpServerConf mSftpServerConf;
    private SftpUsersConf mSftpUsersConf;

    public Configured() {
        initServerConf();
        initUsersConf();
    }

    @Override
    public SftpServerConf getServerConf() {
        return mSftpServerConf;
    }

    @Override
    public SftpUsersConf getUsersConf() {
        return mSftpUsersConf;
    }

    private void initServerConf() {
        mSftpServerConf = new SftpServerConf();
        Properties props = loadConfig(SERVER_CONF_FILE_PATH);
        String host = props.getProperty("host");
        if (StringUtils.isEmpty(host)) {
            sLogger.warn("host is not set");
        }
        sLogger.info("sftp host is {}", host);
        int port = 0;
        try {
            port = Integer.parseInt(props.getProperty("port"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sLogger.info("sftp port is {}", port);
        String hdfsUri = props.getProperty("hdfs-uri");
        if (StringUtils.isEmpty(hdfsUri)) {
            sLogger.warn("hdfs-uri is not set");
        }
        sLogger.info("hdfs-uri is  {}", hdfsUri);
        mSftpServerConf.setHost(host);
        mSftpServerConf.setPort(port);
        mSftpServerConf.setHdfsUri(hdfsUri);
    }

    private void initUsersConf() {
        mSftpUsersConf = new SftpUsersConf();
        Properties props = loadConfig(USERS_CONF_FILE_PATH);
        props.forEach((key, value) -> {
            String username = key.toString().split("\\.")[2];
            SftpUsersConf.UserConf userConf = mSftpUsersConf.getUserConf(username);
            if (userConf == null) {
                userConf = new SftpUsersConf.UserConf();
            }
            userConf.setUsername(username);
            userConf.setPassword(props.getProperty(StringUtils.concat("sftpserver.user.", username, ".userpassword")));
            userConf.setHomeDir(props.getProperty(StringUtils.concat("sftpserver.user.", username, ".homedirectory")));
            userConf.setEnabled(Boolean.parseBoolean(props.getProperty(StringUtils.concat("sftpserver.user.", username, ".enableflag"))));
            mSftpUsersConf.putUserConf(username, userConf);
        });

    }

    private Properties loadConfig(final String resourceName) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(loadResource(resourceName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    private File loadResource(String resourceName) {
        final URL resource = getClass().getResource(resourceName);
        if (resource == null) {
            throw new RuntimeException("Resource not found: " + resourceName);
        }
        return new File(resource.getFile());
    }

}
