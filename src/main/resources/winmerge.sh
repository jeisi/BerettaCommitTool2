#! /bin/sh

file1=$1
if [ "$file1" == '/dev/null' ] || ["$file1" == '\\.\nul' ] || [ ! -e "$file1" ]; then
    file1="/tmp/gitnull"
    `echo "">$file1`
fi
file2=$2
if [ "$file2" == '/dev/null' ] || [ "$file2" == '\\.\nul' ] || [ ! -e "$file2" ]; then
    file2="/tmp/gitnull"
    `echo "">$file2`
fi
"${{WinMergeU}}" -e -ub -dl "Base - $file1" -dr "Mine - $file2" "$file1" "$file2"

