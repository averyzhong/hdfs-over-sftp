# hdfs-over-sftp
SFTP server which works on the top of HDFS

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
Connected to admin@localhost.
sftp> ls          
hbase               inject              log                 logs                merger              
opt                 security            test                tmp                 
user                words.txt           
sftp> 

```

# Configurations
### Server config
Edit the `resources/hdfs-over-sftp.properties` file as yours, some config like this:
```
# sftp server host & port
host = localhost
port = 2233
# hdfs uri
hdfs-uri = hdfs://host:port
```
 ### Users config
 Edit the `resources/users.properties` file file as yours, some config like this:
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
 
 # License
 ```
 Copyright 2019 AveryZhong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 ```
