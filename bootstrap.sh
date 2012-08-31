#!/bin/bash


# downloads directory
DL='downloads'

# local maven repository
REPO='local-repo'


# create directories if needed and get absolute paths
[ -d $DL ] || mkdir $DL
cd $DL
DL=`pwd`
cd - > /dev/null

[ -d $REPO ] || mkdir $REPO
cd $REPO
REPO=`pwd`
cd - > /dev/null


# download file from given url
function get-file {
    FILE="$DL/${1##*/}"
    curl $1 -o $FILE
}

# install jar in local repo
function mvn-deploy {
    mvn deploy:deploy-file -Dfile=$1 \
                           -DgroupId=$(basename $REPO) \
                           -DartifactId=$(basename $1 .jar) \
                           -Dversion=$2 \
                           -Dpackaging=jar \
                           -Durl=file:$REPO
}


# jMonkeyEngine 3.0
get-file 'http://www.jmonkeyengine.com/nightly/jME3_2012-08-02.zip'
unzip $FILE -d "$DL/jme3"
for jar in $DL/jme3/lib/*.jar; do
    mvn-deploy $jar 3.0
done


# build jar
lein uberjar
