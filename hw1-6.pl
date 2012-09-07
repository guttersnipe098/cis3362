#!/usr/bin/perl
################################################################################
# File:    hw1-6.pl
# Purpose: Determines how many times an affine cypher will compose into itself
#          before reducing to the identity function.
# Authors: Michael Alfield <maltfield@knights.ucf.edu>
#          Joe Castellanos
# Course:  CIS 3362 <http://www.cs.ucf.edu/courses/cis3362/fall2012/>
# Created: 2012-09-07
# Updated: 2012-09-07
################################################################################

####################
# SUBROUTINE STUBS #
####################
sub f( $$$ );

#################
# BEGIN PROGRAM #
#################

# DECLARE VARIABLES
print "Enter your values for the affine function.\n\n";

print "a: ";
my $a = <STDIN>;

# was our input sane?
unless( $a =~ m/[0-9]+/ ){
	die "ERROR: You have entered an invalid number";
}

print "b: ";
my $b = <STDIN>;

# was our input sane?
unless( $a =~ m/[0-9]+/ ){
	die "ERROR: You have entered an invalid number";
}

# call our recurssive function and print its output
print "----------\n";
print f( $a, $b, 1 );

exit 0;

###############
# SUBROUTINES #
###############
sub f( $$$ ) {

	# get arguments
	my ( $a1, $b1, $counter ) = @_;

	# distribute: f(x) = [a(a1x + b1) + b] % 26
	$a1 = ($a * $a1) % 26;
	$b1 = ($a * $b1 + $b) % 26;

	# our (affine) function is: f(x) = ax + b % 26
	# then, we know we've reduced to the identity function when:
	#  * a = 1
	#  * b = 0
	# such that the function becomes: f(x) = x

	# have we reached the identity function?
	if( $a1 == 1 && $b1 == 0 ){
		# we have reached the identity function
		print "$counter\n";
		return;
	}

	# we have not reached the identity function, continue..
	f( $a1, $b1, $counter+1 );
}
