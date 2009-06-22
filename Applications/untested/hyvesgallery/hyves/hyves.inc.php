<?php

class Hyves {
	
	function __construct($api_key, $shared_key) {
        $this->api_key = $api_key;
    	$this->shared_secret = $shared_key;
        $this->output = 'xml';
    	$this->version = '2007.9.4';
		$this->api_url = 'http://hyves-api.nl/';
		$this->dir = './tmp/'; // where to keep the log file
    }
	
	// Generates API call and parses the result in an object
	public function call_method($method, $parameter, $value) {
		
		$aParams = array(
			$parameter => $value, 
			"api_key" => $this->api_key,
			"v" => $this->version,
			"method" => $method,
			"format" => $this->output,
			"ts" => time() 
		);
		
		$sharedSecret = $this->shared_secret;
		ksort($aParams); //sort array
		$params = array();
		foreach($aParams as $key => $value) {
		  $params[] = $key . "=" . $value;
		}
		
		$param = implode('&', $params);
		$signature = md5(implode($params) . $this->shared_secret);

		$url = $this->api_url."?".$param."&sig=".$signature;
		// return was die... that is now adjusted...
		if(!$object = simplexml_load_file($url)) return null;
		
		if ($object->getName() == 'error_result')
		{
			$this->log($object);
			$object = null;
		}
		return $object;
		
	}
	
	private function log($object) {
		$timestamp = date(DATE_RFC822);
		$mssg = "{$object->error_code}\t{$object->error_message}\t{$timestamp}\n";
		return file_put_contents($this->dir.'error.log', $mssg, FILE_APPEND|FILE_TEXT);
	}
	
	
}

?>
