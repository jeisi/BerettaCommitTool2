#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir beretta
cd beretta
git init

echo aaa > a.txt
git add a.txt
git commit -m 'Adding a.txt.'

echo bbb > b.txt
git add b.txt
git commit -m 'Adding b.txt.'

git checkout HEAD~1

echo ccc > c.txt
git add c.txt
