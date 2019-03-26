package com.avery.hdfs.sftp.conf;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author AveryZhong.
 */

public class SftpUsersConf {
    private HashMap<String, UserConf> mUserConfHashMap = new HashMap<>();

    @Nullable
    public UserConf getUserConf(final String userName) {
        return mUserConfHashMap.get(userName);
    }

    public void putUserConf(final String userName, final UserConf userConf) {
        mUserConfHashMap.put(userName, userConf);
    }

    public static class UserConf {
        private String mUsername;
        private String mPassword;
        private boolean isEnabled;
        private String mHomeDir;

        public String getUsername() {
            return mUsername;
        }

        public void setUsername(final String username) {
            mUsername = username;
        }

        public String getPassword() {
            return mPassword;
        }

        public void setPassword(final String password) {
            mPassword = password;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        public void setEnabled(final boolean enabled) {
            isEnabled = enabled;
        }

        public String getHomeDir() {
            return mHomeDir;
        }

        public void setHomeDir(final String homeDir) {
            mHomeDir = homeDir;
        }

        @Override
        public String toString() {
            return "UserConf{" +
                    "mUsername='" + mUsername + '\'' +
                    ", mPassword='" + mPassword + '\'' +
                    ", isEnabled=" + isEnabled +
                    ", mHomeDir='" + mHomeDir + '\'' +
                    '}';
        }
    }
}
