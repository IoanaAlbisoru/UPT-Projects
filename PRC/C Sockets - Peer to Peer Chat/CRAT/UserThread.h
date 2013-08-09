#ifndef USERTHREAD_H_
#define USERTHREAD_H_

#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>

#include "DataFlag.h"
#include "Utils.h"

struct UserThread {
	int (*sendData)(struct UserThread*, char*);
	char* (*getData)(struct UserThread*);
	int (*hasData)(struct UserThread*);

	int (*equals)(struct UserThread*, struct UserThread*);

	void (*close)(struct UserThread*);

	struct DataFlag* networkData;
	struct DataFlag* dataToSend;
	struct DataFlag* terminate;

	char* userName;

	pthread_t receiverThread;
	pthread_t senderThread;
	int socket;

};

struct UserThread* new_UserThread(const char* userName, int socket);
struct UserThread* new_MockUserThread(const char* userName, int socket);

/*
 * UserThread destructor NOT responsible for closing the socket;
 */
void dest_UserThread(struct UserThread*);

#endif /* USERTHREAD_H_ */
