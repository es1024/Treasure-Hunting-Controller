<?php
error_reporting(E_ERROR);

$opts = getopt('h',['help','log-suffix:']);
if(isset($opts['h']) || isset($opts['help'])){
	echo 'Treasure Hunting on a Deserted Island controller' . PHP_EOL;
	echo 'written by es1024' . PHP_EOL . PHP_EOL;
	echo ' -h, --help         Display this help text' . PHP_EOL;
	echo ' --log-suffix=""    Controller will apply this suffix to logs.' . PHP_EOL;
	echo PHP_EOL;
	exit;
}

if(isset($opts['log-suffix'])){
	$log_suffix = $opts['log-suffix'];
}else{
	$log_suffix = '';
}

$handles = [];
$pipes = [];
$log = null;
$btreasure = []; // total bot treasure
$alive = []; // A - alive, D - dead, C - camp
$names = [];

$bot_list = parse_ini_file('bots.ini', true);
$bot_list || die('Could not open bots.ini' . PHP_EOL);

$proc_open_desc = [
	0 => ['pipe', 'r'],
	1 => ['pipe', 'w'],
	2 => ['file', 'php://stderr', 'a']
];
function kill_all(){
	global $names, $pipes, $handles, $logs;
	printAll("EXIT\n");
	foreach($names as $id=>$n){
		fclose($pipes[$id][0]);
		fclose($pipes[$id][1]);
		fclose($pipes[$id][2]);
		proc_close($handles[$id]);
		fclose($logs[$id]);
	}
}
register_shutdown_function(kill_all);
foreach($bot_list as $n=>$data){
	isset($data['name']) || die('name property not set for ' . $n . PHP_EOL);
	isset($data['run']) || die('run property not set for ' . $n . PHP_EOL);

	$id = count($names);

	$names[$id] = $data['name'];
	$handles[$id] = proc_open($data['run'], $proc_open_desc, $pipes[$id]);
	is_resource($handles[$id]) || die('Could not run ' . $data['name'] . PHP_EOL);

	$btreasure[$id] = 0;
	$alive[$id] = ['A', 'A', 'A', 'A', 'A'];
}
$log = fopen("logs/treasure-hunt$log_suffix.log", 'w');
$log || die('Could not open logs/treasure-hunt'.$log_suffix.'.log'.PHP_EOL);

function printAll($str){
	global $names, $pipes, $log;
	foreach($pipes as $id=>&$p){
		$tmp = str_replace('/id/', $id+1, $str);
		fwrite($p[0], $tmp) || die('Issue outputting text for ' . $names[$id] . PHP_EOL);
		fflush($p[0]);
	}
	$tmp = str_replace('/id/', 0, $str);
	fwrite($log, $tmp);
	fflush($log);
}
function readAll(){
	global $names, $pipes;
	$res = [];
	foreach($pipes as $id=>&$p){
		$txt = stream_get_line($p[1], 100, "\n");
		$txt || die('Could not read for "' . $names[$id] . '"' . PHP_EOL);

		$arr = explode(',', $txt);
		count($arr) == 5 || die('Invalid output "' . $txt . '" for ' . $names[$id] . PHP_EOL);
		$res[$id] = $arr;
	}
	return $res;
}
$day = 1;
$nalive = count($names) * 5; $total = $nalive;
while($nalive > 5){
	$N = max(3, floor($nalive/4));
	$n = mt_rand(2, $N);
	printAll("START_DAY $day/$N\n");
	$turn = 1; $space_left = $nalive - $n;
	$streasure = [];
	foreach($names as $id=>$n)
		$streasure[$id] = [0, 0, 0, 0, 0];
	$returned = 0;
	while($turn <= 30 && $space_left > 0){
		printAll("START_TURN $turn\n");
		$inp = readAll();
		$return_attempts = [];
		foreach($inp as $id=>$arr){
			for($i = 0; $i < 5; ++$i){
				switch($alive[$id][$i]){
				case 'C':
					$inp[$id][$i] = 'N';
				break;
				case 'D':
					$inp[$id][$i] = 'D';
				break;
				case 'A':
					switch($arr[$i]){
					case 'R': 
						$return_attempts[] = [$id, $i];
					break;
					default:
						$streasure[$id][$i] += $returned + 1;
					break;
					}
				break;
				default:
					var_dump($alive);
					die('Invalid value for $alive: ' . $alive[$id][$i] . PHP_EOL);
				}
			}
		}
		shuffle($return_attempts);
		foreach($return_attempts as $r){
			if($space_left > 0){
				--$space_left;
				++$returned;
				$alive[$r[0]][$r[1]] = 'C';
				$inp[$r[0]][$r[1]] = 'R';
			}else{
				$inp[$r[0]][$r[1]] = 'r';			
			}
		}
		$ostring = "END_TURN /id/";
		foreach($inp as $id=>$arr){
			$ostring .= ' ' . implode(',', $arr);
		}
		printAll($ostring."\n");
		++$turn;
	}
	foreach($alive as $id=>$a){
		for($i = 0; $i < 5; ++$i){
			if($alive[$id][$i] == 'C'){
				$alive[$id][$i] ='A';
				$btreasure[$id] += $streasure[$id][$i];
			}else if($alive[$id][$i] == 'A'){
				$alive[$id][$i] = 'D';
				--$nalive;
			}
		}
	}
	$ostring = "END_DAY /id/";
	foreach($alive as $a){
		$ostring .= ' ' . implode(',', $a);
	}
	printAll($ostring."\n");
	++$day;
}
foreach($btreasure as $id=>$amt){
	echo $names[$id] . ' - ' . $amt . PHP_EOL;
}
//echo implode(',',$btreasure).PHP_EOL;
exit;