package com.avery.hdfs.sftp;

import com.avery.hdfs.sftp.conf.Configured;
import com.avery.hdfs.sftp.conf.SftpUsersConf;
import com.avery.hdfs.sftp.utils.DigestUtils;
import com.avery.hdfs.sftp.utils.StringUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.kex.DHG1;
import org.apache.sshd.server.kex.DHG14;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author AveryZhong.
 */

public class SftpServer extends Configured {
    private static final Logger sLogger = LoggerFactory.getLogger(SftpServer.class);

    public static void main(String[] args) {
        try {
            create().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        int port = getServerConf().getPort();
        if (port != 0) {
            sLogger.info("port is set. sftp server will be started");
        } else {
            sLogger.info("port is not set. so sftp server will not be started");
            return;
        }
        final SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("keys/host-key.pem"));
        sshServer.setKeyExchangeFactories(Arrays.asList(new DHG14.Factory(), new DHG1.Factory()));
        sshServer.setCommandFactory(new ScpCommandFactory());
        sshServer.setSubsystemFactories(Collections.singletonList(new SftpSubsystem.Factory()));
        sshServer.setHost(getServerConf().getHost());
        sshServer.setPort(port);
        sshServer.setPasswordAuthenticator((username, password, session) -> {
            if (StringUtils.isEmpty(username)) {
                sLogger.error("username can't be empty.");
                return false;
            }
            SftpUsersConf.UserConf userConf = getUsersConf().getUserConf(username);
            if (userConf == null) {
                sLogger.error("have no such user, username: {}", username);
                return false;
            }
            if (!userConf.isEnabled()) {
                sLogger.error("user: {} is disabled.", username);
                return false;
            }
            String userPassword = userConf.getPassword();
            if (StringUtils.isEmpty(userPassword)) {  // indicate password is optional
                setFileSystemFactory(sshServer, getServerConf().getHdfsUri(), userConf.getHomeDir());
                return true;

            }
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
            if (!md5Password.equals(userPassword)) {
                sLogger.error("password is wrong");
                return false;
            }
            setFileSystemFactory(sshServer, getServerConf().getHdfsUri(), userConf.getHomeDir());
            return true;
        });
        sshServer.start();
        sLogger.info("SftpServer started at port {}, now you can connect the server via: sftp -oPort={} yourUsername@{}",
                port, port, getServerConf().getHost());
    }

    private void setFileSystemFactory(final SshServer sshServer, final String hdfsUri, final String homeDir) {
        sLogger.info("setFileSystemFactory, homeDir: {}", homeDir);
        String tmpHomeDir = homeDir;
        if (StringUtils.isEmpty(tmpHomeDir)) {
            tmpHomeDir = "/";
        }
        final String finalTmpHomeDir = tmpHomeDir;
        sshServer.setFileSystemFactory(session ->
                new HdfsFileSystemView(session.getUsername(), hdfsUri, finalTmpHomeDir, false));
    }

    public static SftpServer create() {
        return new SftpServer();
    }

}
