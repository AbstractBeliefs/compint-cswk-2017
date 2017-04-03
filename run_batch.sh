#!/usr/bin/env zsh

for i in `seq 1 50`; java -jar out/artifacts/SET10107Coursework_jar/SET10107Coursework.jar | grep -v csv
