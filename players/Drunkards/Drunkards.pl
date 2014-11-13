#!/usr/bin/perl
use 5.10.1;
@opts = qw(S R);
$| = 1;
while(<>){
	if(index($_, 'START_TURN') == 0){
		say 'S,S,S,S,S';
	}elsif(index($_, 'EXIT') == 0){
		exit 0;
	}
}
