#!/usr/intel/bin/perl
#

use warnings;
use strict;

#my $testName = "linux1_base";
#my $testName = "sc_linux5_s";
my $testName = "iil_1";
my $testDir = "/tmp/simba/iil_1_old_traces";
my $multiplier = "1";
my $hostsFile = "/nfs/iil/stod/stod048/w.nbdist.104/oshai/workstations.${testName}.new.filtered.csv";
#my $hostsFile = "/nfs/iil/iec/sws/work/oshai/public/workload/traces2/iil1_workstations";
my $jobsFile = "/nfs/iil/stod/stod048/w.nbdist.104/oshai/jobs.${testName}.csv";
#my $jobsFile = "/nfs/iil/iec/sws/work/oshai/public/workload/traces2/iil1_trace_14-10-28-10-2012.csv";
my $scheduler = "fifo";#"reservation";
my @tests = ( 
			'bf',
			'ff',
			'wf',
			'rf',
			'mf',
			'smf',
			);
my $simulatorJar = "/nfs/iil/iec/sws/work/oshai/simba.jar";
print "running test $testName\n";
foreach (@tests) 
{
	my $algo = $_;
	my $specificTestDir = $testDir."/$algo";
	system("mkdir -p $specificTestDir");
	my $cmd = "echo algo $algo pid \$\$ dir $specificTestDir ; cd $specificTestDir; /usr/intel/pkgs/java/1.6.0.31-64/bin/java -Xmx15g -Dscheduler=$scheduler -Dhosts-file=$hostsFile -Dhost-memory-multiplier=$multiplier -Djobs-file=$jobsFile -Dgrader=$algo -jar $simulatorJar > sim.out &";
#	print "executing $cmd\n"; 
  	system("$cmd");
}
