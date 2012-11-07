#!/usr/intel/bin/perl
#

use warnings;
use strict;

#my $testName = "linux1_base";
#my $testName = "sc_linux5_s";
my $testName = "iil_1";
my $testDir = "/tmp/simba/sim_iil_x10mem";
my $hostsFile = "/nfs/iil/stod/stod048/w.nbdist.104/oshai/workstations.${testName}.new.filtered.csv";
my $jobsFile = "/nfs/iil/stod/stod048/w.nbdist.104/oshai/jobs.${testName}.csv";
my @tests = ( 
			'bf',
			'bfi',
			'ff',
			'wf',
			'rf',
			'mf',
			'mf4',
			'mf6',
			'nf');
my $simulatorJar = "/nfs/iil/iec/sws/work/oshai/simba.jar";
print "running test $testName\n";
foreach (@tests) 
{
	my $algo = $_;
	my $specificTestDir = $testDir."/$algo";
	system("mkdir -p $specificTestDir");
	my $cmd = "echo algo $algo pid \$\$ dir $specificTestDir ; cd $specificTestDir; /usr/intel/pkgs/java/1.6.0.31-64/bin/java -Xmx15g -Dhosts-file=$hostsFile -Dhost-memory-multiplier=10 -Djobs-file=$jobsFile -jar $simulatorJar $algo submit=real > sim.out &";
#	print "executing $cmd\n"; 
  	system("$cmd");
}
