@echo off
rm results logs/*
set x=1000
:loop
	php controller.php --commas --log-suffix=-%x:~-3% 1>>results
	set /A x+=1
if %x% leq 1999 goto loop