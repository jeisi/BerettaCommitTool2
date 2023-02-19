#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

cp ../encode_utf8_without_bom.txt ./
