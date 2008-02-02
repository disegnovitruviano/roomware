#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <time.h>
#include <sys/time.h>

#include "jbluetooth.h"
#include "event.h"
#include "block.h"


static pthread_t worker;
static pthread_mutex_t shared_data_lock;
static pthread_mutex_t sleep_lock;
static pthread_mutex_t setup_lock;
static pthread_cond_t sleep_cond;
static pthread_cond_t setup_cond;

static int worker_stay_alive = JBLUETOOTH_TRUE;
static int worker_has_work = JBLUETOOTH_FALSE;
static void* worker_pool;

static int shared_accessCode;
static int shared_setup_ok;
static DiscoveryListener shared_listener;


static int create_locks() {
	int success = JBLUETOOTH_TRUE;
	int ret = 0;

	ret = pthread_mutex_init(&shared_data_lock, NULL);
	if(ret != 0) {
		perror("create data lock");
		success = JBLUETOOTH_FALSE;
	}

	ret = pthread_mutex_init(&sleep_lock, NULL);
	if(ret != 0) {
		perror("create sleep lock");
		success = JBLUETOOTH_FALSE;
	}

	ret = pthread_cond_init(&sleep_cond, NULL);
	if(ret != 0) {
		perror("create sleep cond");
		success = JBLUETOOTH_FALSE;
	}

	ret = pthread_mutex_init(&setup_lock, NULL);
	if(ret != 0) {
		perror("create setup lock");
		success = JBLUETOOTH_FALSE;
	}	

	ret = pthread_cond_init(&setup_cond, NULL);
	if(ret != 0) {
		perror("create setup cond");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


static int destroy_locks() {
	int success = JBLUETOOTH_TRUE;
	int ret = 0;
	
	ret = pthread_mutex_destroy(&shared_data_lock);
	if(ret != 0) {
		perror("destroy shared data lock");
		success = JBLUETOOTH_FALSE;
	}

	ret = pthread_cond_destroy(&sleep_cond);
	if(ret != 0) {
		perror("destroy sleep cond");
		success = JBLUETOOTH_FALSE;
	}

	ret = pthread_cond_destroy(&setup_cond);
	if(ret != 0) {
		perror("destroy setup cond");
		success = JBLUETOOTH_FALSE;
	}

	ret = pthread_mutex_destroy(&setup_lock);
	if(ret != 0) {
		perror("destroy setup lock");
		success = JBLUETOOTH_FALSE;
	}

	ret = pthread_mutex_destroy(&sleep_lock);
	if(ret != 0) {
		perror("destroy sleep lock");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


static int obtain_data_lock() {
	int success = JBLUETOOTH_TRUE;
	int ret = 0;

	ret = pthread_mutex_lock(&shared_data_lock);
	if(ret != 0) {
		perror("obtain lock");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


static int release_data_lock() {
	int success = JBLUETOOTH_TRUE;
	int ret = 0;

	ret = pthread_mutex_unlock(&shared_data_lock);
	if(ret != 0) {
		perror("release lock");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


static int worker_setupInquiry() {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	if(success == JBLUETOOTH_TRUE) {
		status = block_setupInquiry(shared_accessCode, shared_listener);
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Problems setting up inquiry!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


static int wake_worker() {
	int success = JBLUETOOTH_TRUE;
	int ret = 0;
	int got_lock;

	if(success == JBLUETOOTH_TRUE) {
		ret = pthread_mutex_trylock(&sleep_lock);
		if(ret != 0) {
			perror("mutex trylock");
			success = JBLUETOOTH_FALSE;
		} else {
			got_lock = JBLUETOOTH_TRUE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		worker_has_work = JBLUETOOTH_TRUE;
		ret = pthread_cond_signal(&sleep_cond);
		if(ret != 0) {
			perror("signal worker");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(got_lock == JBLUETOOTH_TRUE) {
		ret = pthread_mutex_unlock(&sleep_lock);
		if(ret != 0) {
			perror("unlock sleep lock");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


static int wait_for_work() {
	int success = JBLUETOOTH_TRUE;
	int ret = 0;


	while(worker_has_work == JBLUETOOTH_FALSE
		&& success == JBLUETOOTH_TRUE) {
		ret = pthread_cond_wait(&sleep_cond, &sleep_lock);
		if(ret != 0) {
			fprintf(stderr, "Could not wait for signal!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		worker_has_work = JBLUETOOTH_FALSE;
	}

	return success;
}


static int wake_setup() {
	int success = JBLUETOOTH_TRUE;
	int ret = 0;
	int got_lock = JBLUETOOTH_FALSE;

	if(success == JBLUETOOTH_TRUE) {
		ret = pthread_mutex_lock(&setup_lock);
		if(ret != 0) {
			perror("setup lock");
			success = JBLUETOOTH_FALSE;
		} else {
			got_lock = JBLUETOOTH_TRUE;
		}	
	}

	if(success == JBLUETOOTH_TRUE) {
		ret = pthread_cond_signal(&setup_cond);
		if(ret != 0) {
			perror("signal setup");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(got_lock == JBLUETOOTH_TRUE) {
		ret = pthread_mutex_unlock(&setup_lock);
		if(ret != 0) {
			perror("setup unlock");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


static int waitForSetupStatus() {
	int success = JBLUETOOTH_TRUE;
	int ret;
	struct timespec ts;
	struct timeval tv;

	if(success == JBLUETOOTH_TRUE) {
		ret = gettimeofday(&tv, NULL);
		if(ret != 0) {
			perror("get time of day");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		ts.tv_sec = tv.tv_sec;
		ts.tv_nsec = tv.tv_usec * 1000;

		ts.tv_sec += 5; /* wait max 5 sec for worker to setup */
		ret = pthread_cond_timedwait(&setup_cond, &setup_lock, &ts);
		if(ret != 0) {
			if(ret == ETIMEDOUT) {
				fprintf(stderr, "no response from worker!\n");
			}
			else {
				perror("wait for worker");
			}
			success = JBLUETOOTH_FALSE;
		}
	}


	if(success == JBLUETOOTH_TRUE) {
		success = shared_setup_ok;
	}


	return success;
}


static int workerCode(void) {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	status = cfutil_createPool(&worker_pool);
	if(status != CFUTIL_TRUE) {
		fprintf(stderr, "Could not create worker autopool!\n");
		success = JBLUETOOTH_FALSE;
	}

	status = pthread_mutex_lock(&sleep_lock);
	if(status != 0) {
		perror("Could not obtain sleep lock!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(success == JBLUETOOTH_TRUE) {

		while(worker_stay_alive == JBLUETOOTH_TRUE) {
			int got_lock = JBLUETOOTH_FALSE;

			status = wait_for_work();
			if(status != JBLUETOOTH_TRUE) {
				fprintf(stderr, "Worker failed to wait for work!\n");
				success = JBLUETOOTH_FALSE;
			}

			if(worker_stay_alive == JBLUETOOTH_TRUE && success == JBLUETOOTH_TRUE) {
				status = obtain_data_lock();
				if(status != JBLUETOOTH_TRUE) {
					fprintf(stderr, "Worker could not obtain lock!\n");
					success = JBLUETOOTH_FALSE;
				} else {
					got_lock = JBLUETOOTH_TRUE;
				}
			}

			if(worker_stay_alive == JBLUETOOTH_TRUE && success == TRUE) {
				status = worker_setupInquiry();
				if(status != JBLUETOOTH_TRUE) {
					fprintf(stderr, "Worker has problems initializing inquiry!\n");
					success = JBLUETOOTH_FALSE;
				}
			}

			shared_setup_ok = success;

			if(got_lock == JBLUETOOTH_TRUE) {
				status = release_data_lock();
				if(status != JBLUETOOTH_TRUE) {
					fprintf(stderr, "Worker could not release lock!\n");
					success = JBLUETOOTH_FALSE;
				}
			}

			if(worker_stay_alive == JBLUETOOTH_TRUE) {
				status = wake_setup();
				if(status != JBLUETOOTH_TRUE) {
					fprintf(stderr, "Could not wake setup thread!\n");
					success = JBLUETOOTH_FALSE;
				}
			}

			if(worker_stay_alive == JBLUETOOTH_TRUE && success == JBLUETOOTH_TRUE) {
				status = block_doInquiry();
				if(status != JBLUETOOTH_TRUE) {
					fprintf(stderr, "Problems starting inquiry!\n");
					success = JBLUETOOTH_FALSE;
				}
			}
		}
	}

	status = pthread_mutex_unlock(&sleep_lock);
	if(status != 0) {
		perror("unlock sleep lock");
		success = JBLUETOOTH_FALSE;
	}

	status = cfutil_destroyPool(worker_pool);
	if(status != CFUTIL_TRUE) {
		fprintf(stderr, "Could not destroy autopool!\n");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


static int create_worker_thread() {
	int status;

	worker_stay_alive = JBLUETOOTH_TRUE;

	status = pthread_create(&worker, NULL, (void*)workerCode, NULL);
	if(status != 0) {
		perror("pthread_create");
		return JBLUETOOTH_FALSE;
	}

	return JBLUETOOTH_TRUE;
}


static int destroy_worker_thread() {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	status = obtain_data_lock();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Could not obtain data lock!\n");
		success = JBLUETOOTH_FALSE;
	}

	worker_stay_alive = JBLUETOOTH_FALSE;

	status = release_data_lock();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Could not release data lock!\n");
		success = JBLUETOOTH_FALSE;
	}

	status = wake_worker();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Could not wake worker thread!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(success == JBLUETOOTH_TRUE) {
		sleep(1); /* Give worker time to end ... */
	}

	return success;
}


int event_init() {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	if(success == JBLUETOOTH_TRUE) {
		status = block_init();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not init block!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		status = create_locks();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not create locks!");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		status = create_worker_thread();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not create worker thread!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


int event_destroy() {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	status = block_destroy();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Problems with deinit block!\n");
		success = JBLUETOOTH_FALSE;
	}

	status = destroy_worker_thread();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Could not destory worker thread!\n");
		success = JBLUETOOTH_FALSE;
	}

	status = destroy_locks();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Could not destroy locks!\n");
	}

	return success;
}


int event_startInquiry(int accessCode, DiscoveryListener listener) {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;
	int got_lock = JBLUETOOTH_FALSE;

	status = obtain_data_lock();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Could not obtain data lock!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(success == JBLUETOOTH_TRUE) {
		shared_accessCode = accessCode;
		shared_listener = listener;

		status = release_data_lock();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not release data lock!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		status = pthread_mutex_trylock(&setup_lock);
		if(status != 0) {
			perror("setup lock");
			success = JBLUETOOTH_FALSE;
		} else {
			got_lock = JBLUETOOTH_TRUE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		status = wake_worker();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not wake worker!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		status = waitForSetupStatus();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Setup status inquiry is bad!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(got_lock == JBLUETOOTH_TRUE) {
		status = pthread_mutex_unlock(&setup_lock);
		if(status != 0) {
			perror("setup lock");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


int event_cancelInquiry(DiscoveryListener listener) {
	return block_cancelInquiry(listener);
}


CFStringRef event_getFoundRemoteDeviceName(IOBluetoothDeviceRef deviceRef) {
	return IOBluetoothDeviceGetName(deviceRef);
}


CFStringRef event_queryRemoteDeviceName(IOBluetoothDeviceRef deviceRef) {
	return block_queryRemoteDeviceName(deviceRef);
}
