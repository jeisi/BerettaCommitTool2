#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir git-depot.git
cd git-depot.git
git init --bare
cd ..

mkdir alice
cd alice
git init
echo Init > readme.txt
git add readme.txt
git commit -m 'Initial.'
git remote add origin ../git-depot.git
git push origin master
cd ..

git clone git-depot.git bob

cd alice
echo aaa > a.txt
git add a.txt
git commit -m 'Add a.txt.'
git push origin master
cd ..

cd bob
echo AAA > a.txt
git add a.txt
git commit -m 'Adding a.txt.'
git pull origin master
cd ..
