package com.avery.sftp;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.apache.sshd.server.SshFile;
import org.apache.sshd.server.filesystem.NameEqualsFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author AveryZhong.
 */

@SuppressWarnings("all")
public class HdfsSshFile implements SshFile {
    private static final Logger sLogger = LoggerFactory.getLogger(HdfsSshFile.class);
    private String mFileName;
    private Path mFile;
    private String mUserName;
    private FileSystem mFileSystem;
    @Nullable
    private FileStatus mFileStatus;

    HdfsSshFile(final FileSystem fileSystem, final String fileName, final Path file,
                final String userName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName can not be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("file can not be null");
        }

        if (fileName.length() == 0) {
            throw new IllegalArgumentException("fileName can not be empty");
        } else if (fileName.charAt(0) != '/') {
            throw new IllegalArgumentException("fileName must be an absolute path");
        }
        mFileSystem = fileSystem;
        mFileName = fileName;
        mFile = file;
        mUserName = userName;
        try {
            mFileStatus = mFileSystem.getFileStatus(file);
        } catch (IOException e) {
            sLogger.warn("==> {}", e.getLocalizedMessage());
        }
    }

    @Override
    public String getAbsolutePath() {
        String fullName = mFileName;
        int length = fullName.length();
        if ((length != 1) && (fullName.charAt(length - 1) == '/')) {
            fullName = fullName.substring(0, length - 1);
        }
        return fullName;
    }

    @Override
    public String getName() {
        if (mFileName.equals("/")) {
            return "/";
        }
        String shortName = mFileName;
        int length = mFileName.length();
        if (shortName.charAt(length - 1) == '/') {
            shortName = shortName.substring(0, length - 1);
        }
        int slashIndex = shortName.lastIndexOf('/');
        if (slashIndex != -1) {
            shortName = shortName.substring(slashIndex + 1);
        }
        return shortName;
    }

    @Override
    public String getOwner() {
        return mFileStatus == null ? mUserName : mFileStatus.getOwner();
    }

    @Override
    public boolean isDirectory() {
        try {
            return mFileSystem.isDirectory(mFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isFile() {
        try {
            return mFileSystem.isFile(mFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean doesExist() {
        try {
            return mFileSystem.exists(mFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long getSize() {
        return mFileStatus == null ? 0 : mFileStatus.getLen();
    }

    @Override
    public long getLastModified() {
        return mFileStatus == null ? 0 : mFileStatus.getModificationTime();
    }

    @Override
    public boolean setLastModified(long time) {
        // Unsupported
        return true;
    }

    @Override
    public boolean isReadable() {
        try {
            FsPermission permissions = getPermissions();
            if (permissions == null) {
                return true;
            }
            if (mFileName.equals(getOwner())) {
                if (permissions.toString().substring(0, 1).equals("r")) {
                    sLogger.debug("PERMISSIONS: " + mFile.toString() + " - " + " read allowed for user");
                    return true;
                }
            } else {
                if (permissions.toString().substring(6, 7).equals("r")) {
                    sLogger.debug("PERMISSIONS: " + sLogger + " - " + " read allowed for others");
                    return true;
                }
            }
            sLogger.debug("PERMISSIONS: " + mFile.toString() + " - " + " read denied");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private FsPermission getPermissions() throws IOException {
        if (mFileSystem.exists(mFile)) {
            return null;
        }
        return mFileSystem.getFileStatus(mFile).getPermission();
    }

    public boolean isWritable() {
        try {
            FsPermission permissions = getPermissions();
            if (permissions == null) {
                return true;
            }
            if (mUserName.equals(getOwner())) {
                if (permissions.toString().substring(1, 2).equals("w")) {
                    sLogger.debug("PERMISSIONS: " + mFile.toString() + " - " + " write allowed for user");
                    return true;
                }
            } else {
                if (permissions.toString().substring(7, 8).equals("w")) {
                    sLogger.debug("PERMISSIONS: " + mFile.toString() + " - " + " write allowed for others");
                    return true;
                }
            }
            sLogger.debug("PERMISSIONS: " + mFile.toString() + " - " + " write denied");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isExecutable() {
        try {
            if (!mFileSystem.exists(mFile)) {
                return false;
            }
            return mFileSystem.isDirectory(mFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isRemovable() {
        if ("/".equals(mFileName)) {
            return false;
        }
        String fullName = getAbsolutePath();
        int indexOfSlash = fullName.lastIndexOf('/');
        String parentFullName;
        if (indexOfSlash == 0) {
            parentFullName = "/";
        } else {
            parentFullName = fullName.substring(0, indexOfSlash);
        }
        HdfsSshFile parentObject = new HdfsSshFile(mFileSystem, parentFullName,
                mFile.getParent(), mUserName);
        return parentObject.isWritable();
    }

    @Override
    public SshFile getParentFile() {
        int indexOfSlash = getAbsolutePath().lastIndexOf('/');
        String parentFullName;
        if (indexOfSlash == 0) {
            parentFullName = "/";
        } else {
            parentFullName = getAbsolutePath().substring(0, indexOfSlash);
        }
        return new HdfsSshFile(mFileSystem, parentFullName, mFile.getParent(), mUserName);
    }

    @Override
    public boolean delete() {
        boolean retVal = false;
        if (isRemovable()) {
            try {
                retVal = mFileSystem.delete(mFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    @Override
    public boolean create() throws IOException {
        mFileSystem.create(mFile);
        return true;
    }

    @Override
    public void truncate() throws IOException {
        // Unsupported
    }

    public boolean move(final SshFile dest) {
        boolean retVal = false;
        if (dest.isWritable() && isReadable()) {
            Path destFile = ((HdfsSshFile) dest).mFile;
            try {
                if (!mFileSystem.exists(destFile)) {
                    retVal = mFileSystem.rename(mFile, destFile);
                } else {
                    sLogger.info("{} is exists", destFile.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    @Override
    public boolean mkdir() {
        boolean retVal = false;
        if (isWritable()) {
            try {
                retVal = mFileSystem.mkdirs(mFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    @Override
    public List<SshFile> listSshFiles() {
        try {
            if (!mFileSystem.isDirectory(mFile)) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileStatus[] files = new FileStatus[0];
        try {
            files = mFileSystem.listStatus(mFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (files == null) {
            return null;
        }
        Arrays.sort(files, new Comparator<FileStatus>() {
            public int compare(FileStatus f1, FileStatus f2) {
                return f1.getPath().getName().compareTo(f2.getPath().getName());
            }
        });
        String virtualFileStr = getAbsolutePath();
        if (virtualFileStr.charAt(virtualFileStr.length() - 1) != '/') {
            virtualFileStr += '/';
        }
        SshFile[] virtualFiles = new SshFile[files.length];
        for (int i = 0; i < files.length; ++i) {
            FileStatus fileObj = files[i];
            String fileName = virtualFileStr + fileObj.getPath().getName();
            virtualFiles[i] = new HdfsSshFile(mFileSystem, fileName, fileObj.getPath(), mUserName);
        }
        return Collections.unmodifiableList(Arrays.asList(virtualFiles));
    }

    @Override
    public OutputStream createOutputStream(final long offset) throws IOException {
        if (!isWritable()) {
            throw new IOException("No write permission : " + mFile.getName());
        }
        FSDataOutputStream outputStream = null;
        try {
            outputStream = mFileSystem.create(mFile);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeStream(outputStream);
        }
        return outputStream;
    }

    @Override
    public InputStream createInputStream(final long offset) throws IOException {
        if (!isReadable()) {
            throw new IOException("No read permission : " + mFile.getName());
        }
        FSDataInputStream inputStream = null;
        try {
            inputStream = mFileSystem.open(mFile);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeStream(inputStream);
        }
        return inputStream;
    }

    @Override
    public void handleClose() {
        //  Unsupported
    }

    private static String normalizeSeparateChar(final String pathName) {
        String normalizedPathName = pathName.replace(File.separatorChar, '/');
        normalizedPathName = normalizedPathName.replace('\\', '/');
        return normalizedPathName;
    }

    static String getPhysicalName(final String rootDir,
                                  final String currDir, final String fileName,
                                  final boolean caseInsensitive) {
        String normalizedRootDir = normalizeSeparateChar(rootDir);
        if (normalizedRootDir.charAt(normalizedRootDir.length() - 1) != '/') {
            normalizedRootDir += '/';
        }
        String normalizedFileName = normalizeSeparateChar(fileName);
        String resArg;
        String normalizedCurrDir = currDir;
        if (normalizedFileName.charAt(0) != '/') {
            if (normalizedCurrDir == null) {
                normalizedCurrDir = "/";
            }
            if (normalizedCurrDir.length() == 0) {
                normalizedCurrDir = "/";
            }
            normalizedCurrDir = normalizeSeparateChar(normalizedCurrDir);
            if (normalizedCurrDir.charAt(0) != '/') {
                //  normalizedCurrDir = '/' + normalizedCurrDir;
            }
            if (normalizedCurrDir.charAt(normalizedCurrDir.length() - 1) != '/') {
                normalizedCurrDir += '/';
            }
            resArg = normalizedRootDir + normalizedCurrDir.substring(1);
        } else {
            resArg = normalizedRootDir;
        }
        if (resArg.length() > 1 && resArg.charAt(resArg.length() - 1) == '/') {
            resArg = resArg.substring(1, resArg.length() - 1);
        }
        StringTokenizer st = new StringTokenizer(normalizedFileName, "/");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            // . => current directory
            if (tok.equals(".")) {
                continue;
            }
            // .. => parent directory (if not root)
            if (tok.equals("..")) {
                if (resArg.startsWith(normalizedRootDir)) {
                    int slashIndex = resArg.lastIndexOf('/');
                    if (slashIndex != -1) {
                        resArg = resArg.substring(0, slashIndex);
                    }
                }
                continue;
            }

            // ~ => home directory (in this case the root directory)
            if (tok.equals("~")) {
                resArg = normalizedRootDir.substring(0, normalizedRootDir
                        .length() - 1);
                continue;
            }

            if (caseInsensitive) {
                File[] matches = new File(resArg)
                        .listFiles(new NameEqualsFileFilter(tok, true));

                if (matches != null && matches.length > 0) {
                    tok = matches[0].getName();
                }
            }

            resArg = resArg + '/' + tok;
        }
        if ((resArg.length()) + 1 == normalizedRootDir.length()) {
            resArg += '/';
        }
        if (!resArg.regionMatches(0, normalizedRootDir, 0, normalizedRootDir
                .length())) {
            resArg = normalizedRootDir;
        }
        return resArg;
    }
}
