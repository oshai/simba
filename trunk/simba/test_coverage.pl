#!/usr/bin/perl
my $coverage = `awk -F, '{print \$4}' target/site/jacoco/report.csv | grep -v INSTRUCTION_MISSED | grep -v '0'`;
chomp($coverage);
print "coverage of lines is: $coverage\n";
exit ($coverage eq '' ? 0 : 1);
