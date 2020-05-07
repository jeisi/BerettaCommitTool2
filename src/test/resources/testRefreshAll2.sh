#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

cd work
cd beretta

echo devtools >> .git_repositories.lst

mkdir devtools
cd devtools
git init
