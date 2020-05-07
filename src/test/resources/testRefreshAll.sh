#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

rm -rf work
mkdir work
cd work

mkdir beretta
cd beretta
git init
cat > .git_repositories.lst <<EOF
.
gyp
gyptools
EOF

mkdir gyp
cd gyp
git init
cd ..

mkdir gyptools
cd gyptools
git init
cd ..
