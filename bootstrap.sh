#!/bin/bash


# create directory if needed and print absolute path
function make-dir {
    [ -d $1 ] || mkdir -p $1
    cd $1
    pwd
    cd - > /dev/null
}

# download file from given url
function get-file {
    FILE="$DL/${1##*/}"
    curl $1 -o $FILE
}

# install jar in local repo
function mvn-deploy {
    mvn deploy:deploy-file \
        -Dfile=$1 \
        -DgroupId=$(basename $REPO) \
        -DartifactId=$(basename $1 .jar) \
        -Dversion=$2 \
        -Dpackaging=jar \
        -Durl=file:$REPO
}


# downloads directory
DL=$(make-dir 'downloads')
# local maven repository
REPO=$(make-dir 'local-repo')
# project resources directory
RES=$(make-dir 'resources')


# jMonkeyEngine 3.0
get-file 'http://www.jmonkeyengine.com/nightly/jME3_2012-08-02.zip'
unzip $FILE -d "$DL/jme3"
for jar in $DL/jme3/lib/*.jar; do
    mvn-deploy $jar 3.0
done

# sample scene
get-file "http://www.jmonkeyengine.com/nightly/town.zip"
cp $FILE $RES


# build jar
lein uberjar
