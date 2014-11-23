#include <iostream>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>

using namespace std;

int compare(int i, int j)
  {
  if (i < j)
    return (-1);
  if (i == j)
    return (0);
  if (i > j)
    return (1);
  }

int main()
  {
  int index;
  int day;
  int turn;
  int slash_index;
  int to_die;
  int num_alive;
  int mine_alive;
  int turn_to_return;
  bool returned;
  string line;
  vector<int> last_returns;
  vector<int> today_returns;

  getline(cin, line);

  if (line.compare(0, 6, "INDEX ") != 0)
    {
    cerr << "INVALID INDEX LINE \"" << line << "\"" << endl;

    return (-1);
    }

  index = atoi(line.substr(6).c_str()) - 1;

  while (1) // Day loop
    {
    getline(cin, line);
    if (line.compare(0, 4, "EXIT") == 0)
      {
      return (0);
      }
    else if (line.compare(0, 9, "START_DAY") != 0 || (slash_index = line.find('/')) == string::npos)
      {
      cerr << "INVALID START_DAY \"" << line << "\"" << endl;
      return (-1);
      }

    day = atoi(line.substr(10, slash_index - 10).c_str());
    to_die = atoi(line.substr(slash_index + 1, line.length() - slash_index - 1).c_str());

    if (day != 1)
      {
      if (to_die > num_alive)
        {
        turn_to_return = 30;
        }
      else
        {
        turn_to_return = last_returns[last_returns.size() - to_die] - 1;
        }
      }

    returned = false;

    for (turn = 1; turn <= 30; ++turn)
      {
      getline(cin, line);

      if (line.compare(0, 4, "EXIT") == 0)
        {
        return (0);
        }
      if (line.compare(0, 7, "END_DAY") == 0)
        {
        goto end_day;
        }
      if (line.compare(0, 10, "START_TURN") != 0)
        {
        cerr << "INVALID START_TURN \"" << line << "\"" << endl;
        }

      if (day == 1)
        {
        switch (compare(turn, 30))
          {
            case -1:
              cout << "S,S,S,S,S" << endl;
              break;

            case 0:
              cout << "R,R,R,R,R" << endl;
              break;

            case 1:
              cout << "N,N,N,N,N" << endl;
              break;
          }
        }
      else
        {
        if (returned)
          {
          cout << "N,N,N,N,N" << endl;
          }
        /*
        else if (num_alive - today_returns.size() < to_die)
          {
          cout << "R,R,R,R,R" << endl;
          returned = true;
          }
        */
        else if (turn >= turn_to_return)
          {
          cout << "R,R,R,R,R" << endl;
          returned = true;
          }
        else
          {
          cout << "S,S,S,S,S" << endl;
          }
        }

      getline(cin, line);

      if (line.compare(0, 4, "EXIT") == 0)
        {
        return (0);
        }
      if (line.compare(0, 8, "END_TURN") != 0)
        {
        cerr << "INVALID END_TURN \"" << line << "\"" << endl;
        }

      stringstream ss(line);
      string item;
      int i = 0;
      while (getline(ss, item, ' '))
        {
        i++;
        if (i > 2 && i - 3 != index)
          {
          int num_to_add = count(item.begin(), item.end(), 'R'); // Add turn to today_returns for each servant that returned
          for (int j = 0; j < num_to_add; j++)
            {
            today_returns.push_back(turn);
            }
          }
        }

      }

    getline(cin, line);

  end_day:

    if (line.compare(0, 4, "EXIT") == 0)
      {
      return (0);
      }
    else if (line.compare(0, 7, "END_DAY") != 0)
      {
      cerr << "INVALID END_DAY \"" << line << "\"" << endl;
      return (-1);
      }

    stringstream ss(line);
    string item;
    int i = 0;
    num_alive = 0;
    while (getline(ss, item, ' '))
      {
      i++;
      if (i > 2 && i - 3 != index)
        {
        num_alive += count(item.begin(), item.end(), 'A');
        }
      else if (i - 3 == index)
        {
        mine_alive = count(item.begin(), item.end(), 'A');
        }
      }

    last_returns = today_returns;
    today_returns.clear();

    }

  return (0);
  }
