#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir beretta
cd beretta
git init

echo 01 > test01.cpp
echo 01 > test02.cpp
echo 01 > test03.cpp
echo 01 > test04.cpp
echo 01 > test05.cpp
echo 01 > test06.cpp
echo 01 > test07.cpp
echo 01 > test08.cpp
echo 01 > test09.cpp
echo 01 > test10.cpp
git add -A
git commit -m 'Initial commit.'

echo 02 > test01.cpp
echo 02 > test02.cpp
echo 02 > test03.cpp
echo 02 > test04.cpp
echo 02 > test05.cpp
echo 02 > test06.cpp
echo 02 > test07.cpp
echo 02 > test08.cpp
echo 02 > test09.cpp
echo 02 > test10.cpp
