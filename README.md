# hdfs-over-sftp
SFTP server which works on a top of HDFS

Hdfs-over-sftp is an open source project based on Apache sshd to access and operate HDFS through SFTP protocol

# How to Run?
1. import project to your IDE(e.g: Intellij Idea).
2. run `com.avery.hdfs.sftp.SftpServer#main`.
3. input`sftp -oPort=2233 -o HostKeyAlgorithms=+ssh-dss root@localhost` at your terminal and press "Return" key.
4. enter "yes".
5. press "Return" key directly, password is optional, can be empty.
```
Avery:IdeaProjects AveryZhong$ sftp -oPort=2233 -o HostKeyAlgorithms=+ssh-dss root@localhost
root@localhost's password: 
Connected to root@localhost.
sftp> ls          
hbase               inject              log                 logs                merger              
opt                 security            test                tmp                 
user                words.txt           
sftp> 

```

# Configuration
 Edit `com.avery.hdfs.sftp.SftpConf.java` file as yours.
 Some config like this:
 ```
 public class SftpConf {
     public static final String HOST = "localhost";
     public static final int PORT = 2233;
 
     public static final String HDFS_SERVER_URL = "hdfs://ip:8020";
     public static final String HOME_DIR = "/";
 
 }
 
 ```
