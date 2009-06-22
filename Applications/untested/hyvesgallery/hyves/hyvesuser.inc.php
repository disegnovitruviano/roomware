<?php

class HyvesUser {
	
	protected function __construct($user, $api) {
		$this->user = $user;
		$this->api = $api;
	}


	public function get_uid() {
		return $this->user->uid;
	}


	public function get_profile_url() {
		return $this->user->url;
	}


	public static function get_hyves_user($username, $api) {
		$results = $api->call_method('users.get', 'uname', $username);
		if(!$results) return false;

		$user = false;
		//print_r($results);
		// TODO: check size == 1
		foreach($results as $result) {
			$user = $result;
		}

		if($user !== false) $user = new HyvesUser($user, $api);
		return $user;
	}


	public function get_avatar_url() {
		$photo_id = $this->user->pid;
		$results = $this->api->call_method('photos.get', 'pid', $photo_id);
		if(!$results) return false;

		foreach($results as $result) {
			$photo_url = $result->image_medium->src;
		}

		return $photo_url;
	}


	public function get_photo_url($photo_id) {
		$results = $this->api->call_method('photos.get', 'pid', $photo_id);
		if(!$results) return false;

		foreach($results as $result) {
			$photo_url = $result->image_large->src;
		}

		return $photo_url;
	}
	

	public function get_album_ids() {
		$results = $this->api->call_method('photos.getAlbums', 'uid', $this->user->uid);
		if(!$results) return false;

		foreach($results as $result) {
			$album_ids[] = $result->aid;
		}

		return $album_ids;
	}


	public function get_photo_urls_from_album($album_id) {
		$results = $this->api->call_method('photos.get', 'aid', $album_id);
		if(!$results) return false;

		foreach($results as $result) {
			$photo_urls[] = $result->image_xlarge->src;
		}

		return $photo_urls;
	}


	public function get_all_photo_urls() {
		$album_ids = $this->get_album_ids();
		if($album_ids === false) {
			return false;
		}

		foreach($album_ids as $album_id) {
			$photo_urls = $this->get_photo_urls_from_album($album_id);
			if($photo_urls === false) return false;

			foreach($photo_urls as $photo_url) {
				$all_photo_urls[] = $photo_url;
			}
		}

		return $all_photo_urls;
	}

}

?>
