#!/usr/bin/perl
my $coverage = `grep 'coverage type=\"line' coverage.xml | head -1 | grep '100\%' `;
chomp($coverage);
print "coverage of lines is: $coverage\n";
exit ($coverage eq '' ? 1 : 0);
