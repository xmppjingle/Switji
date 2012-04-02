#!/bin/bash
SIPGATEWAY_HOME=$HOME/sipgateway

case "$1" in
start)
        if [ -e $SIPGATEWAY_HOME/sipgateway.pid ]; then
                echo "sipgateway already running.";
        else
                echo "Starting sipgateway..."
                java -Dsipgateway.home=${SIPGATEWAY_HOME} -server -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=42 -Xms2048M -Xmx2048M -Xmn512M -XX:PermSize=64m -XX:MaxPermSize=128m -XX:+HeapDumpOnOutOfMemoryError -Xss128k -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1 -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -Djava.net.preferIPv4Stack=true -Dcom.sun.management.jmxremote.port=7399 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configuration=file:${SIPGATEWAY_HOME}/conf/log4j.xml -jar ${SIPGATEWAY_HOME}/sipgateway.jar start &
                pid=$!
                echo ${pid} > $SIPGATEWAY_HOME/sipgateway.pid
        fi

;;
stop)

        if [ -e $SIPGATEWAY_HOME/sipgateway.pid ]; then
                echo "Killing sipgateway...";
                kill `cat $SIPGATEWAY_HOME/sipgateway.pid`;
                rm -f $SIPGATEWAY_HOME/sipgateway.pid;
        else
                echo "sipgateway is not running.";
        fi

;;
restart)
    $0 stop
    $0 start
;;
status)
        if [ -e $SIPGATEWAY_HOME/sipgateway.pid ]; then
                echo "sipgateway running. PID " `cat $SIPGATEWAY_HOME/sipgateway.pid`;
        else
                echo "sipgateway is not running.";
        fi
;;
*)

echo 'Usage: (start|stop|restart|status)'
                echo ' '                
                echo 'Options:'
                echo '  start           - Starts SIP Gateway using the configuration files'
                echo '  stop            - Stops SIP Gateway'
                echo '  restart         - Restart SIP Gateway'
                echo '  status          - Shows the status of SIP Gateway'
esac