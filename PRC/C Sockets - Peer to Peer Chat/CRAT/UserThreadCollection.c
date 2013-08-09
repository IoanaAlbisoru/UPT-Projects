#include "UserThreadCollection.h"

#define REALLOC_SIZE 30

struct UserThread* Iterator_next(struct UserThreadIterator*);
unsigned int Iterator_hasNext(struct UserThreadIterator*);

struct UserThreadIterator* new_UserThreadIterator(struct UserThreadCollection*);
void dest_UserThreadIterator(struct UserThreadIterator*);

static int iteratorVersion = 0;

//Private iterator stuff

struct UserThreadIterator* new_UserThreadIterator(
		struct UserThreadCollection* coll) {
	struct UserThreadIterator* temp;

	pthread_mutex_lock(coll->lock);
	temp = malloc(sizeof(struct UserThreadIterator));

	temp->collection = coll;
	temp->internalIndex = 0;

	temp->version = iteratorVersion;

	temp->hasNext = Iterator_hasNext;
	temp->next = Iterator_next;

	return temp;
}
void dest_UserThreadIterator(struct UserThreadIterator* this) {
	if (this == NULL)
		return;
	//just emphasizing that we NOT, under ANY circumstances, free(this->collection) here.
	pthread_mutex_unlock(this->collection->lock);
	free(this);
}

struct UserThread* Iterator_next(struct UserThreadIterator*this) {
	if (iteratorVersion != this->version)
		return NULL;

	if (this->internalIndex < this->collection->numberOfUsers) {

		return this->collection->users[this->internalIndex++];
	}
	return NULL;
}
unsigned int Iterator_hasNext(struct UserThreadIterator*this) {
	if (iteratorVersion != this->version)
		return 0;

	if (this->internalIndex < this->collection->numberOfUsers) {
		return 1;
	}
	return 0;
}

///
/*-------------------------------------------------------------------------
 *-------------------------------------------------------------------------
 *-------------------------------------------------------------------------
 */
struct UserThreadCollection* new_UserThreadCollection();
void dest_UserThreadCollection(struct UserThreadCollection*);

void Collection_add(struct UserThreadCollection*, struct UserThread*);
void Collection_remove(struct UserThreadCollection*, struct UserThread*);
unsigned int Collection_size(struct UserThreadCollection*);
unsigned int Collection_contains(struct UserThreadCollection*, char*);
struct UserThread
* Collection_getUser(struct UserThreadCollection*, const char*);

struct UserThreadIterator* Collection_iterator(struct UserThreadCollection*);


/*
 *PRIVATE STUFF
 */
int getIndex(struct UserThreadCollection*this, struct UserThread* info);
unsigned int Collection_containsByStruct(struct UserThreadCollection*this,
		struct UserThread* user);

//IMPLEMENTATION COLLECTION

struct UserThreadCollection* new_UserThreadCollection() {
	struct UserThreadCollection* temp;
	int i;

	temp = malloc(sizeof(struct UserThreadCollection));
	temp->numberAllocated = REALLOC_SIZE;
	temp->users = (struct UserThread**) malloc(
			sizeof(struct UserThread*) * REALLOC_SIZE);
	for (i = 0; i < REALLOC_SIZE; i++) {
		temp->users[i] = NULL;
	}

	temp->lock = malloc(sizeof(pthread_mutex_t));

	pthread_mutex_init(temp->lock, NULL);

	temp->numberOfUsers = 0;

	temp->add = Collection_add;
	temp->contains = Collection_contains;
	temp->getUser = Collection_getUser;
	temp->iterator = Collection_iterator;
	temp->remove = Collection_remove;
	temp->size = Collection_size;

	return temp;
}

void dest_UserThreadCollection(struct UserThreadCollection*this) {
	int i;
	if (this == NULL)
		return;

	for (i = 0; i < this->numberOfUsers; i++) {
		dest_UserThread(this->users[i]);
	}
	pthread_mutex_destroy(this->lock);
	free(this);
}

void Collection_add(struct UserThreadCollection*this, struct UserThread* toAdd) {
	struct UserThread *temp[this->numberAllocated];
	int i;

	//we don't add duplicates.
	if (this->contains(this, toAdd->userName)) {
		return;
	}
	iteratorVersion++;

	pthread_mutex_lock(this->lock);
	if (this->numberOfUsers == this->numberAllocated) {

		for (i = 0; i < this->numberOfUsers; i++) {
			temp[i] = this->users[i];
		}
		free(this->users);
		this->numberAllocated += REALLOC_SIZE;
		this->users = (struct UserThread**) malloc(
				sizeof(struct UserThread*) * this->numberAllocated);

		for (i = 0; i < this->numberOfUsers; i++) {
			this->users[i] = temp[i];
		}

		//		this->users = (struct UserInfo**) realloc(this->users,
		//				sizeof(struct UserInfo*) * this->numberAllocated);
		this->users[this->numberOfUsers++] = toAdd;
	} else {
		this->users[this->numberOfUsers++] = toAdd;
	}
	pthread_mutex_unlock(this->lock);
}

void Collection_remove(struct UserThreadCollection*this,
		struct UserThread* toRemove) {
	int index, j;

	if (this->contains(this, toRemove->userName)) {
		pthread_mutex_lock(this->lock);
		index = getIndex(this, toRemove);

		//we shift every element with one to the left
		for (j = index; j < this->numberOfUsers - 1; j++)
			this->users[j] = this->users[j + 1];

		this->numberOfUsers--;
		this->users[this->numberOfUsers] = NULL;
		iteratorVersion++;
	}
	pthread_mutex_unlock(this->lock);
}
unsigned int Collection_size(struct UserThreadCollection*this) {
	return this->numberOfUsers;
}

unsigned int Collection_contains(struct UserThreadCollection*this, char*name) {
	struct UserThread* temp = new_MockUserThread(name, 0);
	int rv = Collection_containsByStruct(this, temp);
	dest_UserThread(temp);
	return rv;
}

unsigned int Collection_containsByStruct(struct UserThreadCollection*this,
		struct UserThread* user) {
	pthread_mutex_lock(this->lock);
	int i = 0;
	for (i = 0; i < this->numberOfUsers; i++)
		if (this->users[i]->equals(this->users[i], user)) {
			pthread_mutex_unlock(this->lock);
			return 1;
		}
	pthread_mutex_unlock(this->lock);
	return 0;
}
struct UserThread* Collection_getUser(struct UserThreadCollection*this,
		const char* name) {
	struct UserThread* temp = new_MockUserThread(name, 0);
	int index = -1;
	pthread_mutex_lock(this->lock);
	index = getIndex(this, temp);
	dest_UserThread(temp);
	if (index >= 0) {
		pthread_mutex_unlock(this->lock);
		return this->users[index];
	}
	pthread_mutex_unlock(this->lock);
	return NULL;
}

struct UserThreadIterator* Collection_iterator(struct UserThreadCollection*this) {
	return new_UserThreadIterator(this);
}

int getIndex(struct UserThreadCollection*this, struct UserThread* info) {
	int i = -1;
	for (i = 0; i < this->numberOfUsers; i++)
		if (this->users[i]->equals(this->users[i], info))
			return i;
	return -1;
}
