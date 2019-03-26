# hdfs-over-sftp
SFTP server which works on a top of HDFS

Hdfs-over-sftp is an open source project based on Apache sshd to access and operate HDFS through SFTP protocol

# How to Run?
1. Import project to your IDE(e.g: Intellij Idea).
2. Edit the `resources/hdfs-over-sftp.properties` file & `resources/users.properties` as yours.
3. Run `com.avery.hdfs.sftp.SftpServer#main`.
4. Input`sftp -oPort=2233 -o HostKeyAlgorithms=+ssh-dss yourusername@localhost` at your terminal and press "Return" key.
5. Enter "yes".
6. Enter your password and  press "Return" key.
```
AveryZhong$ sftp -oPort=2233 -o HostKeyAlgorithms=+ssh-dss admin@localhost
admin@localhost's password: 
Connected to root@localhost.
sftp> ls          
hbase               inject              log                 logs                merger              
opt                 security            test                tmp                 
user                words.txt           
sftp> 

```

# Configurations
## Server conf
Edit the `resources/hdfs-over-sftp.properties` file as yours, some conf like this:
```
# sftp server host & port
host = localhost
port = 2233
# hdfs uri
hdfs-uri = hdfs://172.16.0.234:8020
```
 ## Users conf
 Edit the `resources/users.properties` file file as yours, some conf like this:
 ```
# username: admin, password: 123456 (md5 format)
sftpserver.user.admin.userpassword = e10adc3949ba59abbe56e057f20f883e
sftpserver.user.admin.homedirectory = /log
sftpserver.user.admin.enableflag = true

# username: sftpuser, password: 123456 (md5 format)
sftpserver.user.sftpuser.userpassword = e10adc3949ba59abbe56e057f20f883e
sftpserver.user.sftpuser.homedirectory = /log/log-download
sftpserver.user.sftpuser.enableflag = true

 ```
