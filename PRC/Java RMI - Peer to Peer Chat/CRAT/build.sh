#!/bin/bash

createEverything(){

rm -f -r $CreateDir
mkdir $CreateDir

#create the 'CratRunner.sh' file
echo 'SCRIPT=$(readlink -f $0)
BASEDIR=`dirname $SCRIPT`

java -classpath $BASEDIR/'$jarFile' -Djava.security.policy=$BASEDIR/'$jarFile'/'$policyFile' -Djava.rmi.server.codebase=file://$BASEDIR/'$jarFile' '$mainFunction>$RunnerScript

#create the 'security.policy' file
echo 'grant {
permission java.security.AllPermission;
};' >> $policyFile

javac -d $BASEDIR $packageCommon/*.java
javac -d $BASEDIR $packageParticular/*.java

jar cf0 $jarFile $policyFile crat/*

rm -f -r crat

mv -t $CreateDir $jarFile
mv -t $CreateDir $RunnerScript
rm $policyFile
}

#Absolute path to this script.
SCRIPT=$(readlink -f $0)
# Absolute path this script is in.
BASEDIR=`dirname $SCRIPT`

CreateDir=CRATClient
RunnerScript=CratClientRunner.sh
jarFile=cratClient.jar
policyFile=security.policy
packageCommon=src/crat/common
packageParticular=src/crat/client
mainFunction=crat.client.MainClient

createEverything

CreateDir=CRATServer
RunnerScript=CratServerRunner.sh
jarFile=cratServer.jar
policyFile=security.policy
packageCommon=src/crat/common
packageParticular=src/crat/server
mainFunction=crat.server.MainServer

createEverything
