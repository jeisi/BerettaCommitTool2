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
git commit -F- << EOM
テスト
メッセージ
EOM

