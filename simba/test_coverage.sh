#!/bin/bash
echo coverage of lines `grep "coverage type=\"line" coverage.xml | head -1 | awk -F\" '{print $4}' | awk '{print $1}'`
test `grep "coverage type=\"line" coverage.xml | head -1 | awk -F\" '{print $4}' | awk '{print $1}'` == '100%'
