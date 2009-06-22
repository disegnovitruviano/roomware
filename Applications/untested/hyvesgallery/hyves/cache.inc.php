<?php
class cache
{
    /**
    * Whether caching is enabled
    * @var bool
    */
    public static $enabled = true;

    /**
    * Place to store the cache files
	* set to '/dev/shm/' for memory based cache
    * @var string
    */
    protected static $store = './tmp/'; 
    
    /**
    * Prefix to use on cache files
    * @var string
    */
    protected static $prefix = 'cache_';

    /**
    * Stores data
    * 
    * @param string $group Group to store data under
    * @param string $id    Unique ID of this data
    * @param int    $ttl   How long to cache for (in seconds)
    */
    protected static function write($group, $id, $ttl, $data)
    {
        $filename = self::getFilename($group, $id);
        
        if ($fp = fopen($filename, 'xb')) {
        
            if (flock($fp, LOCK_EX)) {
                fwrite($fp, $data);
            }
            fclose($fp);
            
            // Set filemtime
            touch($filename, time() + $ttl);
        }
    }
    
    /**
    * Reads data
    * 
    * @param string $group Group to store data under
    * @param string $id    Unique ID of this data
    */
    protected static function read($group, $id)
    {
        $filename = self::getFilename($group, $id);
        
        return file_get_contents($filename);
    }
    
    /**
    * Determines if an entry is cached
    * 
    * @param string $group Group to store data under
    * @param string $id    Unique ID of this data
    */
    protected static function isCached($group, $id)
    {
	
		$filename = self::getFilename($group, $id);
	
   		if (self::$enabled && file_exists($filename) && filemtime($filename) > time()) {
           return true;
        }

        @unlink($filename);

        return false;
    }
    
    /**
    * Builds a filename/path from group, id and
    * store.
    * 
    * @param string $group Group to store data under
    * @param string $id    Unique ID of this data
    */
    protected static function getFilename($group, $id)
    {
        $id = md5($id);

        return self::$store . self::$prefix . "{$group}_{$id}";
    }
    
    /**
    * Sets the filename prefix to use
    * 
    * @param string $prefix Filename Prefix to use
    */
    public static function setPrefix($prefix)
    {
        self::$prefix = $prefix;
    }

    /**
    * Sets the store for cache files. Defaults to
    * /dev/shm. Must have trailing slash.
    * 
    * @param string $store The dir to store the cache data in
    */
    public static function setStore($store)
    {
        self::$store = $store;
    }

    /**
    * Clears a specific cache file if it exists.
    * 
    * @param string $group Group to store data under
    * @param string $id    Unique ID of this data
    */
    public static function clear($group, $id)
    {
       	$filename = self::getFilename($group, $id);
        if (file_exists($filename)) {
			@unlink($filename);
            return true;
        }
		return false;

    }

}

 /**
* Data cache extension of base caching class
*/
class dataCache extends cache
{

    /**
    * Retrieves data from the cache
    * 
    * @param  string $group Group this data belongs to
    * @param  string $id    Unique ID of the data
    * @return mixed         Either the resulting data, or null
    */
    public static function get($group, $id)
    {
        
		if (self::isCached($group, $id)) {
            return unserialize(self::read($group, $id));
        }
        
        return null;
    }
    
    /**
    * Stores data in the cache
    * 
    * @param string $group Group this data belongs to
    * @param string $id    Unique ID of the data
    * @param int    $ttl   How long to cache for (in seconds)
    * @param mixed  $data  The data to store
    */
    public static function put($group, $id, $ttl, $data)
    {
        self::write($group, $id, $ttl, serialize($data));
    }
}
?>