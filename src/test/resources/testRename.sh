#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir beretta
cd beretta
git init

echo aaa > pre-push
git add pre-push
git commit -m 'Initial.'
git mv pre-push pre-push.sh
