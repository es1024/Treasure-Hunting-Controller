from enum import Enum
from random import choice, random
from math import ceil

class ServantState(Enum):
    dead = 0,
    camp = 1,
    searching = 2

states = (ServantState.dead, ServantState.camp, ServantState.searching)

class ServantMove(Enum):
    none = 0, # dead or stay in camp
    search = 1, # search for treasure
    come_back = 2 # return to camp

moves = (ServantMove.none, ServantMove.search, ServantMove.come_back)

class MoveResult(Enum):
    returned = 0,
    failed_to_return = 1,
    searching = 2,
    dead = 3,
    camp = 4

move_results = (MoveResult.returned, MoveResult.failed_to_return,
           MoveResult.searching, MoveResult.dead, MoveResult.camp)

def result_to_state(result):
    if result == MoveResult.returned or result == MoveResult.camp:
        return ServantState.camp
    elif result == MoveResult.failed_to_return or result == MoveResult.dead:
        return ServantState.dead
    else:
        return ServantState.searching

def memory_move(num_to_die, last_return, bots, turn):
    flattened = [servant for i, bot in enumerate(last_return)
                 for j, servant in enumerate(bot)
                 if i != my_index - 1 and bots[i][j] == ServantState.searching]

    if len(flattened) <= num_to_die:
        return ServantMove.come_back

    if turn >= min(sorted(flattened)[-num_to_die:]) - 1:
        return ServantMove.come_back

    return ServantMove.search

def process_turn_results(raw_results):
    split = raw_results[9:]
    split = split[split.index(' ') + 1:].split(' ')
    if len(split) == 1:
        raise Exception("Invalid END_TURN")
    results = []
    for bot_results in split:
        servant_results = bot_results.split(',')
        results.append([move_results["RrSDN".index(char)] for char in servant_results])
    return results

def process_day_results(raw_results):
    raw_results = raw_results[7:][raw_results.index(' ') + 1:].split(' ')
    results = []
    for bot_results in raw_results:
        servant_results = bot_results.split(',')
        results.append([(ServantState.searching, ServantState.dead)["AD".index(char)] for char in servant_results])
    return results

buffer = []

def my_read():
    read = input()

    if read.startswith("EXIT"):
        exit(0)

    return read

my_index = int(my_read()[6:])

my_servants = [ServantState.camp for _ in range(5)]

day = 1

while True:
    start_day_line = my_read()

    split_index = start_day_line.index('/')

    day_num = int(start_day_line[10:split_index])

    num_to_die = int(start_day_line[split_index+1:])
    if day == 1:
        if num_to_die == 3: # 2, or 3 bots
            bots = [[ServantState.searching for _ in range(5)] for _ in range(3)]
        else: # num of bots is num_to_die * 4 rounded up to the nearest 5
            bots = [[ServantState.searching for _ in range(5)]
                    for _ in range(int(ceil(num_to_die*4)/5.0))]
        last_return = [[14 for _ in range(5)] for _ in range(len(bots))]
        my_servants = bots[my_index - 1]


    for turn in range(1, 31):
        start_turn = my_read() # We keep our own turn number
        if start_turn.startswith("END_DAY"):
                end_day = start_turn
                break

        num_alive = sum([(servant == ServantState.searching or
                          servant == ServantState.camp)
                         for bot in bots for servant in bot])

        mine_alive = sum([(servant == ServantState.searching or
                           servant == ServantState.camp)
                          for servant in my_servants])

        my_moves = [ServantMove.search for _ in range(5)]

        my_move = ",".join(["NSR"[moves.index(memory_move(num_to_die,
                                                          last_return,
                                                          bots,
                                                          turn))]] * 5)

        print (my_move)

        results = process_turn_results(my_read())

        bots = [[result_to_state(servant) for servant in bot] for bot in results]

        if len(last_return) != len(bots):
            last_return = [[14 for _ in range(5)] for _ in range(len(bots))]

        for i, bot in enumerate(results):
            for j in range(5):

                if bot[j] == MoveResult.returned:
                    last_return[i][j] = turn

        my_servants = bots[my_index - 1]
    else:
        end_day = my_read()

    bots = process_day_results(end_day)

    my_servants = bots[my_index - 1]

    day += 1
