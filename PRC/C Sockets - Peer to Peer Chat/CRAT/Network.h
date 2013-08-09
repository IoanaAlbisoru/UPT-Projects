#ifndef NETWORK_H_
#define NETWORK_H_

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>

#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <errno.h>
#include <unistd.h>

#include "Utils.h"
#include "UserThread.h"
#include "UserThreadCollection.h"
#include "DataFlag.h"

struct Network {

	void (*start)(struct Network*);
	void (*close)(struct Network*);

	int (*hasNetworkData)(struct Network*);
	void (*sendMessage)(struct Network*, char* msg, char*user);
	char* (*getMessage)(struct Network*);

	int (*hasError)(struct Network*);
	char* (*getError)(struct Network*);

	int (*connect)(struct Network*, char*user);
	int (*disconnect)(struct Network*, char*user);
	void (*setStatus)(struct Network*, int status);
	int (*getStatus)(struct Network*);

	int(*hasUser)(struct Network*, char*user);

	pthread_t listeningThread;
	pthread_t managerThread;

	struct DataFlag* dataFromNetwork;
	struct DataFlag* dataFromUser;
	struct DataFlag* error;
	struct DataFlag* terminate;

	int listeningSocket;

	int status;

	struct UserThreadCollection *users;

};

struct Network* new_Network();
void dest_Network(struct Network*);

#endif /* NETWORK_H_ */
