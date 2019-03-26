package com.avery.hdfs.sftp.conf;

/**
 * @author AveryZhong.
 */

public interface Configurable {
    SftpServerConf getServerConf();

    SftpUsersConf getUsersConf();
}
