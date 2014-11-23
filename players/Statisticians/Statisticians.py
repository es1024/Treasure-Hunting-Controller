# Team of treasure-hunting statisticians
# Run with:
# python3 statisticians.py

num_others = None
running = True
while running:
    msg = input().split()
    msg_type = msg.pop(0)
    if msg_type == "INDEX":
        my_index = int(msg[0])-1
    elif msg_type == "START_DAY":
        day, max_deaths = tuple(map(int, msg[0].split('/')))
    elif msg_type == "START_TURN":
        turn = int(msg[0])
        if day == 1:
            if turn == 1:
                print("S,S,S,S,S")
            elif turn == 30 or num_active <= max_deaths * 4/5:
                print("R,R,R,R,R") # On first day, return when 4/5 of  maximum number of dying servants remain
            else:
                print("S,S,S,S,S")
        elif turn >= 29 or len(expected_servants[turn+1]) <= max(2, max_deaths * 3/4) or len(expected_servants[turn]) <= max(2, max_deaths * 1/4):
            print("R,R,R,R,R") # If many servants are expected to return next turn or someone is sure to die, return to camp
        else:
            print("S,S,S,S,S") # Otherwise, keep going
    elif msg_type == "END_TURN":
        turn = int(msg.pop(0))
        others_moves = [tuple(s.split(',')) for s in msg[:my_index] + msg[my_index+1:]]
        if num_others is None: # End of first turn, initialize variables that depend on number of servants
            num_others = len(others_moves)
            others_history = [{} for i in range(num_others)]
        if day == 1:
            num_active = sum([move.count('S') for move in others_moves])
        for i, moves in enumerate(others_moves): # Log the return habits of other bots
            if turn == 1:
                others_history[i][day] = [0]*5
            for j, move in enumerate(moves):
                if move == "R": # Only safely returned servants are taken into account
                    others_history[i][day][j] = turn
                    if day > 1:
                        for future_turn in range(turn, 30):
                            expected_servants[future_turn].discard((i,j))
    elif msg_type == "END_DAY":
        day = int(msg.pop(0))
        my_statuses = tuple(msg[my_index].split(','))
        others_statuses = [tuple(s.split(',')) for s in msg[:my_index] + msg[my_index+1:]]
        expected_servants = [set() for i in range(30)] # Compute the sets of expected servants for each turn
        for i in range(num_others):
            for j in range(5):
                if others_statuses[i][j] == 'A':
                    turn_sum = 0
                    for day_num in others_history[i]:
                        turn_sum += others_history[i][day_num][j]
                    for turn in range(turn_sum//day):
                        expected_servants[turn].add((i,j))
    elif msg_type == "EXIT":
        running = False
