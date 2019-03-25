# hdfs-over-sftp
SFTP server which works on a top of HDFS

Hdfs-over-sftp is an open source project based on Apache sshd to access and operate HDFS through SFTP protocol

# How to Run?
1. run com.avery.hdfs.sftp.SftpServer#main
2. sftp -oPort=2233 -o HostKeyAlgorithms=+ssh-dss root@localhost
3. enter "yes"
4. press "Enter" key, password is no needed, can be empty.

# Config
 Edit com.avery.hdfs.sftp.SftpConf.java file as yours.
 Some config like this:
 ```
 public class SftpConf {
     public static final String HOST = "localhost";
     public static final int PORT = 2233;
 
     public static final String HDFS_SERVER_URL = "hdfs://172.16.0.24:8020";
     public static final String HOME_DIR = "/";
 
 }

 ```
