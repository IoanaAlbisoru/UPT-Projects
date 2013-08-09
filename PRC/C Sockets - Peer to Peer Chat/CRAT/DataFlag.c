#include "DataFlag.h"

struct DataFlag* new_DataFlag();
struct DataFlag* new_EmptyDataFlag();
void dest_DataFlag(struct DataFlag*this);
/*
 * DataFlag_set is used by the EmptyDataFlag class
 */
int DataFlag_set(struct DataFlag *this, char* dataToWrite);

/*
 * DataFlag_waitAndSet is used by the DataFlag class
 * polymorphism FTW!
 */
int DataFlag_waitAndSet(struct DataFlag *this, char* dataToWrite);
char* DataFlag_reset(struct DataFlag *this);
int DataFlag_isActive(struct DataFlag *this);

//IMPLEMENTATION
struct DataFlag* new_DataFlag() {
	struct DataFlag* temp = malloc(sizeof(struct DataFlag));

	temp->isActive = DataFlag_isActive;
	temp->reset = DataFlag_reset;
	temp->set = DataFlag_waitAndSet;

	temp->data = malloc(DATA_FLAG_BUFFER_SIZE);
	temp->flag = 0;

	temp->lock = malloc(sizeof(pthread_mutex_t));

	pthread_mutex_init(temp->lock, NULL);

	return temp;
}

struct DataFlag* new_EmptyDataFlag() {
	struct DataFlag* temp = malloc(sizeof(struct DataFlag));

	temp->isActive = DataFlag_isActive;
	temp->reset = DataFlag_reset;
	temp->set = DataFlag_set;

	temp->data = NULL;
	temp->flag = 0;

	temp->lock = malloc(sizeof(pthread_mutex_t));

	pthread_mutex_init(temp->lock, NULL);

	return temp;
}

void dest_DataFlag(struct DataFlag*this) {
	if (this == NULL)
		return;
	pthread_mutex_destroy(this->lock);
	free(this->data);
	free(this);
}

int DataFlag_set(struct DataFlag *this, char* dataToWrite) {
	pthread_mutex_lock(this->lock);

	//if the flag is already active it means that some other thread still needs to handle it
	if (this->flag == 1) {
		pthread_mutex_unlock(this->lock);
		return 0;
	}
	if (dataToWrite == NULL) {
		this->flag = 1;
		pthread_mutex_unlock(this->lock);
		return 1;
	}
	this->size = strlen(dataToWrite) + 1;
	this->flag = 1;
	memcpy(this->data, dataToWrite, this->size);
	pthread_mutex_unlock(this->lock);
	return 1;
}

int DataFlag_waitAndSet(struct DataFlag *this, char* dataToWrite) {
	//	int count = INT_MIN;
	while (!DataFlag_set(this, dataToWrite)) {
		//		count++;
		//		if (INT_MAX == count)
		//			return 0;
	}
	return 1;
}

/*
 * IMPORTANT!!!!!
 * Because we return a new chunk of memory with the
 * data copied in it, we need to free the message received
 * from every DataFlag at it's destination
 */
char* DataFlag_reset(struct DataFlag *this) {
	char *temp;

	pthread_mutex_lock(this->lock);
	this->flag = 0;
	if (this->data == NULL) {
		pthread_mutex_unlock(this->lock);
		return NULL;
	}
	temp = (char*) malloc(this->size);
	memcpy(temp, this->data, this->size);
	//we need to clear the buffer, if not after we copy a shorter string
	memset(this->data, 0, DATA_FLAG_BUFFER_SIZE);
	pthread_mutex_unlock(this->lock);
	return temp;
}

int DataFlag_isActive(struct DataFlag *this) {
	int state = -1;
	//pthread_mutex_trylock returns 0 if successful
	//and acquires that lock;
	if (0 == pthread_mutex_trylock(this->lock)) {
		state = this->flag;
		pthread_mutex_unlock(this->lock);
		return state;
	}
	return 0;
}

