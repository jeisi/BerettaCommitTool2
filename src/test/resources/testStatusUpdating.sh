#! /bin/bash -ex

basedir=$1

cd $basedir/src/test/resources

function create() {
    rm -rf work
    mkdir work
    cd work

    mkdir beretta
    cd beretta
    git init
}

function update() {
    cd work
    cd beretta
    echo aaa > a.txt
}

case $2 in
    'create')
        create
        ;;
    'update')
        update
        ;;
esac
