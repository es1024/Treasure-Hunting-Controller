import sys
from random import randint, choice
team = range(5)

while True:
    inp = sys.stdin.readline().split()
    cmd = inp.pop(0)
    if cmd == 'INDEX':
        teamnum = int(inp[0]) - 1   # using zero based indexing
    elif cmd == 'START_DAY':
        daynum, deadnum = [int(v) for v in inp[0].split('/')]
        # Set up strategy for the day:
        goback = [randint(5,25) for i in team]
    elif cmd == 'START_TURN':
        turn = int(inp[0])
        # Output actions [R]eturn, [S]earch, [N]othing here:
        actions = ['S' if turn < goback[i] else 'R' for i in team]
        sys.stdout.write( (','.join(actions)) + '\n' )
        sys.stdout.flush()
    elif cmd == 'END_TURN':
        endturn = int(inp.pop(0))
        status = [v.split(',') for v in inp]  # R,r,S,D,N
        # [R]eturned, [r]ejected, [S]earching, [D]ead, [N]othing
        mystatus = status[teamnum]
    elif cmd == 'END_DAY':
        endturn = int(inp.pop(0))
        alive = [v.split(',') for v in inp]  # [A]live or [D]ead
        myalive = alive[teamnum]
    elif cmd == 'EXIT':
        sys.exit(0)
