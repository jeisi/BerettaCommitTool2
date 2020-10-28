#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir beretta
cd beretta
git init

echo aaa > update.rb
git add update.rb
git commit -m 'add update.rb.'

echo bbb > update.rb

