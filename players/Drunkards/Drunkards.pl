#!/usr/bin/perl
use 5.10.1;
$| = 1; # disable buffering
@actions = qw(S S S S S);

$_ = <>; ~/^INDEX (\d+)/;
$index = $1;

while(<>){
    if(index($_, 'START_TURN') == 0){
        say join(',', @actions);
    }elsif(index($_, 'END_DAY') == 0){ 
        # update actions based on who is alive
        # index 1-indexed; first bot at position 2.
        # this is not actually necessary for Drunkards, as all of Drunkards'
        #  servants will die on day 1 in any case.
        # This check is here simply as an example.
        my @status = split(',',(split(' '))[$index + 1]);
        my $i;
        for($i = 0; $i < 5; ++$i){
            # action is S if alive, D if dead. Servants will never be in camp.
            $actions[$i] = $status[$i] eq 'A' ? 'S' : 'D';
        }
    }elsif(index($_, 'EXIT') == 0){
        exit 0;
    }
}
