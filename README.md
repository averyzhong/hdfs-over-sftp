# hdfs-over-sftp
SFTP server which works on a top of HDFS

Hdfs-over-sftp is an open source project based on Apache sshd to access and operate HDFS through SFTP protocol

# How to Run?
1. run com.avery.hdfs.sftp.SftpServer#main
2. sftp -oPort=2233 -o HostKeyAlgorithms=+ssh-dss root@localhost
3. enter "yes"
4. press "Retrue" key directly, password is optional, can be empty.
