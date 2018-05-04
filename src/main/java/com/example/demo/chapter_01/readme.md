# 第一章 并发编程的挑战

## 1.1 上下文切换

1. 即使单核处理器也支持多线程执行代码； 

2. 任务从保存到再加载的过程就是一次上下文切换； 

3. 上下文切换和线程的创建会影响多线程的执行速度；

>代码参考：ConcurrencyTest.java 

4. linux系统可以使用如下命令查看每秒上下文切换次数（CS列）； 

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

5. 减少上下文切换方法有：无锁或少锁、CAS算法（Atomic包，无需加锁）、最少线程（避免创建不需要的线程，减少等待）、协程（单线程多任务调度）

6. 统计某一进程所有线程分别处于什么状态，如下执行，使用vi打开dump文件，可以看到很多线程处于waiting状态，此时可以统计下waiting状态的值，适当减少该进程的线程数目，这里以tomcat为例
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

修改Tomcat线程池中maxThreads属性值，改变线程数量

```sbtshell
    <Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
        maxThreads="150" minSpareThreads="4"/>
```

waiting线程少了，系统上下文切换的次数就会少，因为每次从waiting到runnable都会进行一次上下文切换





## 1.2 死锁

>代码参考：DeadLockDemo.java

1. 编译该类，并运行

```sbtshell
[root@jalenchu test]# echo $PATH
/usr/local/jalen/jdk/jdk1.8.0_161/bin:/usr/local/jalen/maven/apache-maven-3.5.3/bin:/usr/local/jalen/jdk/jdk1.8.0_161:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin
[root@jalenchu test]# ls
DeadLockDemo.java
[root@jalenchu test]# javac DeadLockDemo.java 
[root@jalenchu test]# ls
DeadLockDemo$1.class  DeadLockDemo$2.class  DeadLockDemo.class  DeadLockDemo.java
[root@jalenchu test]# java DeadLockDemo

```

2. 输出dump文件

```sbtshell
[root@jalenchu test]# jps
26950 Jps
26938 DeadLockDemo
[root@jalenchu test]# sudo -u root /usr/local/jalen/jdk/jdk1.8.0_161/bin/jstack 26938 > /usr/local/test/dump17
[root@jalenchu test]# ls
DeadLockDemo$1.class  DeadLockDemo$2.class  DeadLockDemo.class  DeadLockDemo.java  dump17
[root@jalenchu test]# 

```

3. 查看dump文件，会发现两个线程死锁

```sbtshell
"Thread-1" #9 prio=5 os_prio=0 tid=0x00007f28880ef800 nid=0x6945 waiting for monitor entry [0x00007f288c63f000]
   java.lang.Thread.State: BLOCKED (on object monitor)
        at DeadLockDemo$2.run(DeadLockDemo.java:49)
        - waiting to lock <0x00000000e34713b0> (a java.lang.String)
        - locked <0x00000000e34713e0> (a java.lang.String)
        at java.lang.Thread.run(Thread.java:748)

"Thread-0" #8 prio=5 os_prio=0 tid=0x00007f28880ee000 nid=0x6944 waiting for monitor entry [0x00007f288c740000]
   java.lang.Thread.State: BLOCKED (on object monitor)
        at DeadLockDemo$1.run(DeadLockDemo.java:38)
        - waiting to lock <0x00000000e34713e0> (a java.lang.String)
        - locked <0x00000000e34713b0> (a java.lang.String)
        at java.lang.Thread.run(Thread.java:748)
```

4. 避免死锁的几个方法：

* 避免一个线程同时获取多个锁；
* 避免一个线程在锁内同时占用多个资源，尽量保证每个锁只占用一个资源；
* 尝试使用定时锁，使用lock.tryLock(timeout)来替代使用内部锁机制；
* 对于数据库锁，加锁和解锁必须在一个数据库连接里，否则会出现解锁失败的问题；





## 1.3 资源限制的挑战

1. 资源限制是指在并发编程时，程序的执行速度受限于计算机硬件资源或软件资源，如带宽限制；

2. 在资源受限情况下，如将串行代码并发执行，因为增加了上下文切换和资源调度的时间，反而会变慢，此时不如直接使用单线程；

3. 对于硬件资源限制，可以使用集群，不同的机器处理不同的数据，如可以将“数据%机器数”计算得到一个机器编号，由对应编号的机器处理该数据；

4. 对于软件资源限制，可以使用资源池复用资源，如使用连接池将数据库和Socket连接复用；