#!/bin/bash
if [ -n $1 ]; then 	
	cd /opt/server/
	sipgateway/bin/sipgateway.sh stop
	wget http://archiva..../jinglenodes-sipgateway-$1.zip
	unzip jinglenodes-sipgateway-$1.zip

	mv jinglenodes-sipgateway-$1.zip pack/
	unlink sipgateway
	ln -s jinglenodes-sipgateway-$1/ appengine
	chmod a+x sipgateway/bin/sipgateway.sh

	sed -i "s#localhost#$(hostname)#g" sipgateway/conf/sipgateway-properties.xml 
	sipgateway/bin/sipgateway.sh start
else
	echo "You must suply the sipgateway version as the first parameter!"
	echo "Ex.: ./install-sipgateway 0.2.2"
fi
