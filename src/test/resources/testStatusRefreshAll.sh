#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir beretta
cd beretta
git init
echo aaa > gyp.sh
git add gyp.sh
echo gyp > .gitignore
echo .gitignore >> .gitignore

mkdir gyp
cd gyp
git init
echo bbb > sources_common_all.txt
git add sources_common_all.txt

