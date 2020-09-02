#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir beretta
cd beretta
git init

echo bbb > a.txt
git add a.txt
git commit -m 'add a.txt.'

echo BBB > a.txt
git add a.txt
