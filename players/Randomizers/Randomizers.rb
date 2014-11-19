def min(a,b);(a<b)?a:b;end
x=""
r=0
while x != "EXIT"
  x=gets.chomp
  if x =~ /^START_DAY/
    r = min(rand(30),rand(30))
  end
  if x =~ /^START_TURN (\d*)/
    puts ($1.to_i>r)?'R,R,R,R,R':'S,S,S,S,S'
	STDOUT.flush
  end
end
