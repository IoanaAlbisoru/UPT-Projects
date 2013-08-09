#ifndef USERTHREADCOLLECTION_H_
#define USERTHREADCOLLECTION_H_

#include "UserThread.h"
#include <stdlib.h>


struct UserThreadIterator {
	struct UserThreadCollection* collection;

	int internalIndex;

	int version;

	struct UserThread* (*next)(struct UserThreadIterator*);
	unsigned int (*hasNext)(struct UserThreadIterator*);
};

//struct UserThreadIterator* new_UserThreadIterator( struct UserThreadCollection*);
/*
 * WHEN THE ITERATOR IS DESTROYED THE LOCK ON THE COLLECTION IS RELEASED!!!
 * NEVER FORGET TO destroy the iterator after you're done with it!
 */
void dest_UserThreadIterator(struct UserThreadIterator*);

struct UserThreadCollection {
	struct UserThread **users;

	unsigned int numberOfUsers;

	//should never be modified outside of the add function;
	unsigned int numberAllocated;

	pthread_mutex_t *lock;

	void (*add)(struct UserThreadCollection*, struct UserThread*);
	void (*remove)(struct UserThreadCollection*, struct UserThread*);
	unsigned int (*size)(struct UserThreadCollection*);
	unsigned int (*contains)(struct UserThreadCollection*, char*);
	struct UserThread* (*getUser)(struct UserThreadCollection*, const char*);

	struct UserThreadIterator* (*iterator)(struct UserThreadCollection*);

};

struct UserThreadCollection* new_UserThreadCollection();
void dest_UserThreadCollection(struct UserThreadCollection*);

#endif /* USERTHREADCOLLECTION_H_ */
