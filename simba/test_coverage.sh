test `grep "coverage type=\"line" /var/lib/jenkins/jobs/simba/workspace/coverage.xml | head -1 | awk -F\" '{print $4}' | awk '{print $1}'` == '100%'
