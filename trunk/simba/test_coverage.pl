#!/usr/bin/perl
my $coverage = `grep 'coverage type=\"line' coverage.xml | head -1 | awk -F\\" '{print $4}' | awk '{print \$1}'`;
chomp($coverage);
print "coverage of lines is $coverage\n";
exit ($coverage eq '100%' ? 0 : 1);

