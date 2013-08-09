#ifndef DATAFLAG_H_
#define DATAFLAG_H_

#include <pthread.h>
#include <sys/types.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <limits.h>

#define DATA_FLAG_BUFFER_SIZE 256

struct DataFlag {
	/*
	 * Marks the flag as active and copies the dataToWrite in the flag's buffer
	 *
	 * If the flag is already active at the time of the method call then
	 * the method will wait a certain amount of time for the flag to be cleared
	 * for writing.
	 *
	 * This function uses a non-reentrant thread lock for synchronization
	 *
	 * Return 0 in case the flag could NOT be set.
	 * Return 1 in case of success.
	 *
	 */

	int (*set)(struct DataFlag *this, char* dataToWrite);

	/*
	 * it sets the flag status as in active and returns the data stored in it.
	 * It returns a new chunk of memory!! so the data that you get from a flag must be
	 * free()-ed somewhere along the way
	 *
	 * This function uses a non-reentrant thread lock for synchronization
	 * FOR EVERY flag->reset() there MUST be a free of the received data
	 */
	char* (*reset)(struct DataFlag *this);

	/*
	 * checks if the flag is active, non-blocking
	 *
	 * This function uses a non-reentrant thread lock for synchronization,
	 * but always uses try-lock function call
	 */
	int (*isActive)(struct DataFlag *this);

	char* data;
	int flag;
	size_t size;
	pthread_mutex_t *lock;
};
/*
 * this constructor allocates memory for 'data'
 */
struct DataFlag* new_DataFlag();

/*
 * this constructor doesn't allocate memory for 'data'
 * when using this the set function MUST be called with a null
 * pointer for the data;
 */
struct DataFlag* new_EmptyDataFlag();

void dest_DataFlag(struct DataFlag*this);

#endif /* DATAFLAG_H_ */
