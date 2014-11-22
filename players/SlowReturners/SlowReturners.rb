x=""
while x != "EXIT"
  x=gets.chomp
  if x =~ /^START_TURN (\d*)/
    puts (1..5).map{|i|(i<=$1.to_i/5)?"R":"S"}.join(",")
	STDOUT.flush
  end
end