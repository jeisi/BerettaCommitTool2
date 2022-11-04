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
echo bbb > b.txt
echo ccc > c.txt

git add -A
git commit -m 'init comit.'

echo '日本語' > a.txt
echo 'ASCII' > b.txt

mkdir gyp
cd gyp
git init
echo 'ddd' > d.txt
git add d.txt
git commit -m 'commit gyp'
cp ../../../encode_utf8_with_bom.txt d.txt
