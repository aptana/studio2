a==b;
// output as text
header("Content-type: text/plain");

// db config
$sql_host = "localhost";
$sql_user = "stats";
$sql_pass = "stats$$1";
$sql_base = "aptana-stats";

// table config
$table = "ping";
$fields = "ip,user_id,datetime,product,version,eclipse_version,install,os_name,os_version,os_architecture";

// get query key/value pairs and set default as needed
$ip = $_SERVER['REMOTE_ADDR'];
$user_id = isset($_GET['id']) ? $_GET['id'] : "none";
$datetime = date('Y-m-d H:i:s');
$product = isset($_GET['p']) ? $_GET['p'] : "";
$version = isset($_GET['v']) ? $_GET['v'] : "";
$eclipse_version = isset($_GET['ev']) ? $_GET['ev'] : "";
$os_name = isset($_GET['osn']) ? $_GET['osn'] : "";
$os_version = isset($_GET['osv']) ? $_GET['osv'] : "";
$os_architecture = isset($_GET['osa']) ? $_GET['osa'] : "";
$install = 0;

// open the db connection - do it any way you like - no error checking
sql_connect($sql_base, $sql_host, $sql_user, $sql_pass);

// 
if ($user_id == "none")
{
	// the first time we get an id, we assume this is from an install
	$install = 1;
	$user_id = get_next_user_id();
}
else
{
	// make sure id exists and create if it doesn't
	if (!user_id_exists($user_id))
	{
		$install = 1;
		$user_id = get_next_user_id();
	}
}

// if we have an id try incrementing the counter
$values =
	"'" .
	join(
		"','",
		array(
            $ip,
			$user_id,
            $datetime,
			$product,
			$version,
			$eclipse_version,
			$install,
			$os_name,
			$os_version,
			$os_architecture
		)
	) .
	"'";
$cmd = "INSERT INTO $table($fields) VALUES($values)";

if (!mysql_query($cmd))
{
	// there was an error adding the rec.
}

// disconnect from db
mysql_close();

// return id
echo "$user_id";

/**
 * get_next_user_id
 *
 * @return next id
 */
function get_next_user_id()
{
	global $table;
	
	$query = "SELECT MAX(user_id) FROM $table";
	$result_set = mysql_query($query);
	$result = 1;
	
	if ($result_set)
	{
		if (mysql_numrows($result_set) > 0)
		{
			$row = mysql_fetch_row($result_set);
			
			// get return value, if it isn't null
			if ($row[0] != "")
			{
				$result = $row[0] + 1;
			}
		}
	}
	
	return $result;
}

/**
 * sql_connect
 *
 * @param $base
 * @param $host
 * @param $user
 * @param $pass
 */
function sql_connect($base,$host,$user,$pass)
{
	if (!mysql_connect($host,$user,$pass))
	{
		echo("Failed attempt to connect to server - aborting.");
		exit();
	}
	
	if (isset($base))
	{
		if (!mysql_select_db($base))
		{
			echo("Failed attempt to open database: $base - aborting");
			exit();
		}
	}
}

/**
 * user_id_exists
 *
 * @param $user_id
 * @return boolean
 */
function user_id_exists($user_id)
{
	global $table;
	
	$condition = "user_id='$user_id'";
	$query = "SELECT COUNT(user_id) FROM $table WHERE $condition";
	$result_set = mysql_query($query);
	$result = 0;
	
	if ($result_set)
	{
		if (mysql_numrows($result_set) > 0)
		{
			$row = mysql_fetch_row($result_set);
			
			// success if return value is not null
			if ($row[0] != "")
			{
				$result = $row[0] > 0;
			}
		}
	}
	
	return $result;
}
