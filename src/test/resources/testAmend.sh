#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir alice
cd alice
git init
echo aaa > a.txt
git add a.txt
git commit -m 'Adding a.txt.'
cd ..

mkdir bob
cd bob
git init
echo bbb > b.txt
git add b.txt
git commit -m 'Adding b.txt.'
cd ..
