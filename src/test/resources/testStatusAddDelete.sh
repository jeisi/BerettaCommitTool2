#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir git-depot
cd git-depot
git init --bare
cd ..

git clone git-depot beretta
cd beretta
echo eee > tools00.txt
git add tools00.txt
git commit -m 'Initial.'
git push origin master
cd ..

git clone git-depot beretta2
cd beretta2
git rm tools00.txt
git commit -m 'rm tools00.txt'
git push origin master
cd ..

cd beretta
echo 0820 > tools00.txt
git add tools00.txt
git commit -m 'Modifing tools00.txt'
git pull origin master || echo ""
