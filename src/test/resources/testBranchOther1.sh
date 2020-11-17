#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir git-depot
cd git-depot
mkdir beretta
cd beretta
git init --bare
cd ..
mkdir beretta_gyp
cd beretta_gyp
git init --bare
cd ..
cd ..

git clone git-depot/beretta beretta
cd beretta

echo aaa > a.txt
git add a.txt
git commit -m 'add a.txt.'
git push origin master

git branch other1
cd ..

git clone git-depot/beretta_gyp beretta/gyp
cd beretta/gyp
echo bbb > b.txt
git add b.txt
git commit -m 'add b.txt.'
git push origin master

git branch god
cd ../..
