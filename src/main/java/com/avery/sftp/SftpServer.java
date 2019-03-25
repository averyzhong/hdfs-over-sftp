package com.avery.sftp;

import org.apache.log4j.PropertyConfigurator;
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

public class SftpServer {
    private static final Logger sLogger = LoggerFactory.getLogger(SftpServer.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure(SftpServer.class.getClassLoader().getResourceAsStream("log4j.properties"));
        final SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("keys/host-key.pem"));
        sshServer.setKeyExchangeFactories(Arrays.asList(new DHG14.Factory(), new DHG1.Factory()));
        sshServer.setCommandFactory(new ScpCommandFactory());
        sshServer.setSubsystemFactories(Collections.singletonList(new SftpSubsystem.Factory()));
        sshServer.setHost(SftpConf.HOST);
        sshServer.setPort(SftpConf.PORT);
        // FIXME: 2019-03-23 为了开发中测试方便，任何用户名和密码都能登陆
        sshServer.setPasswordAuthenticator((username, password, session) -> {
            sshServer.setFileSystemFactory(session2 ->
                    new HdfsFileSystemView(session2.getUsername(), SftpConf.HOME_DIR, false));
            return true; // 任何用户名和密码都能登陆
        });

        try {
            sshServer.start();
            sLogger.info("SftpServer started at port {}, now you can connect the server via: sftp -oPort={} " +
                    "-o HostKeyAlgorithms=+ssh-dss root@{}", SftpConf.PORT, SftpConf.PORT, SftpConf.HOST);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
