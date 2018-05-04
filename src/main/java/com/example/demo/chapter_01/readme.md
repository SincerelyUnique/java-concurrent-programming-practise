# 第一章 并发编程的挑战
## 1.1上下文切换

>代码参考：ConcurrencyTest.java

1.即使单核处理器也支持多线程执行代码；
2.任务从保存到再加载的过程就是一次上下文切换；
3.上下文切换和线程的创建会影响多线程的执行速度；
4.linux系统可以使用如下命令查看每秒上下文切换次数（CS列）；
```sbtshell
[root@i-2431501A games]# vmstat 1
procs -----------memory---------- ---swap-- -----io---- --system-- -----cpu-----
 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
 0  0 3406856 1684420 160276 713664    0    1     1     5    1    1  1  1 99  0  0	
 0  0 3406852 1684412 160276 713668    0    0     0     0 4787 13181  2  1 97  0  0	
 0  0 3406852 1684412 160276 713664    0    0     0     0 4315 12756  1  1 98  0  0	
 0  0 3406852 1684412 160276 713664    0    0     0     0 4568 13016  1  1 98  0  0	
 0  0 3406852 1684412 160280 713664    0    0     0    16 5187 13637  1  1 97  1  0	
 0  0 3406848 1684412 160280 713672    0    0     0     0 4716 13149  1  1 97  0  0	
 0  0 3406848 1684412 160280 713668    0    0     0     0 4248 12690  1  1 98  0  0	
^C
[root@i-2431501A games]# 

```
5.减少上下文切换方法有：无锁或少锁、CAS算法（Atomic包，无需加锁）、最少线程（避免创建不需要的线程，减少等待）、协程（单线程多任务调度）
6.统计某一进程所有线程分别处于什么状态，如下执行，使用vi打开dump文件，可以看到很多线程处于waiting状态，此时可以统计下waiting状态的值，适当减少该进程的线程数目，这里以tomcat为例
```sbtshell
[root@i-2431501A games]# jps
29760 Jps
15122 Bootstrap
18050 Bootstrap
18228 Bootstrap
15573 Bootstrap
17560 Bootstrap
20040 Bootstrap
21577 Bootstrap
14669 Bootstrap
30910 Bootstrap
7231 Bootstrap
[root@i-2431501A games]# sudo -u root /usr/local/java/jdk1.8.0_121/bin/jstack  15122 > /usr/local/games/dump17
[root@i-2431501A games]# vi dump17 

```
各线程状态统计
```sbtshell
[root@i-2431501A games]# grep java.lang.Thread.State dump17 | awk '{print $2$3$4$5}' | sort | uniq -c
     13 RUNNABLE
      1 TIMED_WAITING(onobjectmonitor)
      1 TIMED_WAITING(parking)
      3 TIMED_WAITING(sleeping)
      2 WAITING(onobjectmonitor)
      9 WAITING(parking)
[root@i-2431501A games]# 

```
```sbtshell
[root@i-2431501A games]# ps -ef|grep 15122
root     15122     1  1 Apr17 ?        04:46:43 /usr/local/java/jdk1.8.0_121/bin/java -Djava.util.logging.config.file=/usr/local/tomcat-zzbx-8888/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.endorsed.dirs=/usr/local/tomcat-zzbx-8888/endorsed -classpath /usr/local/tomcat-zzbx-8888/bin/bootstrap.jar:/usr/local/tomcat-zzbx-8888/bin/tomcat-juli.jar -Dcatalina.base=/usr/local/tomcat-zzbx-8888 -Dcatalina.home=/usr/local/tomcat-zzbx-8888 -Djava.io.tmpdir=/usr/local/tomcat-zzbx-8888/temp org.apache.catalina.startup.Bootstrap start
root     30215 29445  0 11:10 pts/0    00:00:00 grep 15122
[root@i-2431501A games]# vi /usr/local/tomcat-zzbx-8888//conf/server.xml 
```
修改maxThreads属性值
```sbtshell
    <Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
        maxThreads="150" minSpareThreads="4"/>
```
waiting线程少了，系统上下文切换的次数就会少，因为每次从waiting到runnable都会进行一次上下文切换





## 1.2死锁
