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
        if day == 1: # On first day, return when two thirds of others have returned
            if turn == 1:
                print("S,S,S,S,S")
            elif turn == 30 or num_active < (num_others*5) * 2/3:
                print("R,R,R,R,R")
            else:
                print("S,S,S,S,S")
        elif turn >= 29 or expected_servants[turn+1] < max_deaths:
            print("R,R,R,R,R") # If the critical number of servants are expected to return next turn, return to camp (the array is 0-based)
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
    elif msg_type == "END_DAY":
        day = int(msg.pop(0))
        my_statuses = tuple(msg[my_index].split(','))
        others_statuses = [tuple(s.split(',')) for s in msg[:my_index] + msg[my_index+1:]]
        others_averages = [[] for i in range(num_others)] # Calculate the average return times of other bots
        expected_servants = [0]*30
        for i in range(num_others):
            for j in range(5):
                if others_statuses[i][j] == 'A':
                    turn_sum = 0
                    for day_num in others_history[i]:
                        turn_sum += others_history[i][day_num][j]
                    for turn in range(turn_sum//day):
                        expected_servants[turn] += 1
    elif msg_type == "EXIT":
        running = False
