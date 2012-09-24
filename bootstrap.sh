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
# Scenes directory
ASSETS=$(make-dir 'assets')


# jMonkeyEngine 3.0
get-file 'http://www.jmonkeyengine.com/nightly/jME3_2012-08-02.zip'
unzip $FILE -d "$DL/jme3"
for jar in $DL/jme3/lib/*.jar; do
    mvn-deploy $jar 3.0
done

# put test data in assets directory
# instead of including it in the jar
unzip $DL/jme3/lib/jME3-testdata.jar -d $ASSETS
rm -rf "$ASSETS/META-INF"
rm "$ASSETS/profiling points"

# sample scene
get-file "http://www.jmonkeyengine.com/nightly/town.zip"
unzip $FILE -d "$ASSETS/Scenes/town"


# build jar
lein uberjar
