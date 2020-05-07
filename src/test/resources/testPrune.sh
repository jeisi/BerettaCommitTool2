#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir Controls
mkdir Download

cd Controls
git init
mkdir subA
cd subA
git init
echo aaa > a.txt
git add a.txt
git commit -m 'Init subA.'
cd ..
cd ..
