package com.avery.sftp;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * @author AveryZhong.
 */

@SuppressWarnings("all")
public class HdfsFileSystemView implements FileSystemView {
    private static final Logger sLogger = LoggerFactory.getLogger(HdfsFileSystemView.class);
    private final String mHomeDir;
    private FileSystem mFileSystem;
    private String mCurrDir;
    private String mUserName;
    private boolean isCaseInsensitive;


    HdfsFileSystemView(String userName) {
        this(userName, "/", false);
    }

    HdfsFileSystemView(final String userName, final String homeDir, final boolean caseInsensitive) {
        if (userName == null) {
            throw new IllegalArgumentException("userName can not be null");
        }
        isCaseInsensitive = caseInsensitive;
        mHomeDir = homeDir;
        mCurrDir = StringUtils.concat(SftpConf.HDFS_SERVER_URL, mHomeDir);
        sLogger.info("mCurrDir = {}", mCurrDir);
        this.mUserName = userName;
        try {
            mFileSystem = FileSystem.get(URI.create(SftpConf.HDFS_SERVER_URL), new Configuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
        sLogger.info("hdfs filesystem view created for user \"{}\" with root \"{}\"", userName, mCurrDir);
    }

    @Override
    public SshFile getFile(String file) {
        return getFile(mCurrDir, file);
    }

    @Override
    public SshFile getFile(SshFile baseDir, String file) {
        return getFile(baseDir.getAbsolutePath(), file);
    }

    private SshFile getFile(String dir, String file) {
        String physicalName = HdfsSshFile.getPhysicalName("/", dir, file, isCaseInsensitive);
        Path fileObj = new Path(dir + file);
        String userFileName = physicalName.substring("/".length() - 1);
        return new HdfsSshFile(mFileSystem, userFileName, fileObj, mUserName);
    }
}
