#include "Network.h"

struct Network* new_Network();
void dest_Network(struct Network*this);

void Network_start(struct Network*this);
void Network_close(struct Network*this);

int Network_hasNetworkData(struct Network*this);
void Network_sendMessage(struct Network*this, char* msg, char*user);
char* Network_getMessage(struct Network*this);

int Network_connect(struct Network*this, char*user);
int Network_disconnect(struct Network*, char*user);
int Network_hasUser(struct Network*, char*user);

void Network_setStatus(struct Network*, int status);
int Network_getStatus(struct Network*this);

int Network_hasError(struct Network*);
char* Network_getError(struct Network*);

/*
 * ------------------------------------------------------------------------------
 * ------------------------------------PRIVATE ----------------------------------
 * ------------------------------------------------------------------------------
 */

/*
 *	run_ListeningThread daemon that always responds to 'connect' attempts to this
 *	machine on port 2307
 */
void* run_ListeningThread(void* threadData);

/*
 * run_ManagerThread is a daemon that responds to any data that has been received
 * on the network and copies it into the data flag for the network;
 * and is responsible to remove a user if he has disconnected.
 *
 * It does so by iterating through all the UserThreads and checking if any have data
 *
 */
void* run_ManagerThread(void* threadData);

//used by the manager thread to check if there are any disconnects;
int isDisconnect(const char* msg);
/*
 * ------------------------------------------------------------------------------
 * ---------------------------------IMPLEMENTATION-------------------------------
 * ------------------------------------------------------------------------------
 */
struct Network* new_Network() {
	struct Network* temp = malloc(sizeof(struct Network));
	temp->close = Network_close;
	temp->start = Network_start;
	temp->getMessage = Network_getMessage;
	temp->hasNetworkData = Network_hasNetworkData;
	temp->sendMessage = Network_sendMessage;
	temp->connect = Network_connect;
	temp->hasUser = Network_hasUser;
	temp->disconnect = Network_disconnect;
	temp->setStatus = Network_setStatus;
	temp->getStatus = Network_getStatus;
	temp->hasError = Network_hasError;
	temp->getError = Network_getError;

	temp->listeningSocket = -1;

	temp->dataFromNetwork = new_DataFlag();
	temp->dataFromUser = new_DataFlag();
	temp->error = new_DataFlag();
	temp->terminate = new_EmptyDataFlag();

	temp->users = new_UserThreadCollection();

	//we make the status available by default;
	temp->status = USER_STATUS_AVAILABLE;

	return temp;
}

void dest_Network(struct Network*this) {
	dest_UserThreadCollection(this->users);
	dest_DataFlag(this->dataFromNetwork);
	dest_DataFlag(this->terminate);
	dest_DataFlag(this->dataFromUser);
	dest_DataFlag(this->error);
	free(this);
}

void Network_start(struct Network*this) {
	pthread_attr_t thAttr;

	pthread_attr_init(&thAttr);
	pthread_attr_setdetachstate(&thAttr, PTHREAD_CREATE_JOINABLE);

	if (0 != pthread_create(&this->listeningThread, &thAttr,
			run_ListeningThread, (void*) this))
		exit(1);

	if (0 != pthread_create(&this->managerThread, &thAttr, run_ManagerThread,
			(void*) this))
		exit(1);

	pthread_attr_destroy(&thAttr);

}
void Network_close(struct Network*this) {
	struct UserThreadIterator* it = this->users->iterator(this->users);
	struct UserThread *temp;

	this->terminate->set(this->terminate, NULL);

	while (it->hasNext(it)) {
		temp = it->next(it);
		if (NULL == temp)
			break;
		temp->close(temp);
	}
	dest_UserThreadIterator(it);
}

int Network_hasError(struct Network*this) {
	return this->error->isActive(this->error);
}
char* Network_getError(struct Network*this) {
	return this->error->reset(this->error);
}

int Network_hasUser(struct Network*this, char*user) {
	return this->users->contains(this->users, user);
}

int Network_hasNetworkData(struct Network*this) {
	return this->dataFromNetwork->isActive(this->dataFromNetwork);
}
void Network_sendMessage(struct Network*this, char* msg, char*user) {

	struct UserThreadIterator* it = this->users->iterator(this->users);

	struct UserThread *temp;
	struct UserThread *mock = new_MockUserThread(user, 0);

	while (it->hasNext(it)) {
		temp = it->next(it);
		if (NULL == temp)
			break;
		if (temp->equals(temp, mock)) {
			temp->sendData(temp, msg);
			break;
		}
	}
	dest_UserThreadIterator(it);
	dest_UserThread(mock);
}
char* Network_getMessage(struct Network*this) {
	return this->dataFromNetwork->reset(this->dataFromNetwork);
}

void Network_setStatus(struct Network *this, int status) {
	if ((status == USER_STATUS_BUSY) || (status == USER_STATUS_AVAILABLE))
		this->status = status;
	else
		status = USER_STATUS_AVAILABLE;
}

int Network_getStatus(struct Network*this) {
	return this->status;
}

int Network_disconnect(struct Network*this, char*user) {
	struct UserThread* temp;
	if (!this->users->contains(this->users, user))
		return 0;
	temp = this->users->getUser(this->users, user);
	temp->close(temp);
	close(temp->socket);

	this->users->remove(this->users, temp);
	dest_UserThread(temp);
	return 1;

}

int Network_connect(struct Network*this, char*user) {
	struct addrinfo hints, *servinfo;
	int rv;
	int newSocket;
	char message[DATA_FLAG_BUFFER_SIZE];
	char data[DATA_FLAG_BUFFER_SIZE];

	if (this->users->contains(this->users, user)) {
		memset(message, 0, 100);
		Utils_format(message, ERR_CODE_USER_ALREADY_CONNECTED, user,
				DEFAULT_PADDING);
		this->error->set(this->error, message);
		return 0;
	}

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE; // use my IP

	//we get address info of the user we want to connect to.
	//THIS IS WHERE WE INTRODUCE UNKOWN ERROR!
	if ((rv = getaddrinfo(user, PORT, &hints, &servinfo)) != 0) {
		sprintf(data, "%s", gai_strerror(rv));
		Utils_format(message, ERR_CODE_UNABLE_TO_CONNECT_WRONG_ADRESS, user, data);
		this->error->set(this->error, message);
		return 0;
	}

	newSocket = socket(servinfo->ai_family, servinfo->ai_socktype,
			servinfo->ai_protocol);

	rv = connect(newSocket, servinfo->ai_addr, servinfo->ai_addrlen);

	if (rv >= 0) {
		this->users->add(this->users, new_UserThread(user, newSocket));
		//WE DO NOT HAVE TO SEND ANY CONFIRMATION MESSAGE
		//ONCE THE PROGRAM HAS RECEIVED THE REQUEST IT AUTOMATICALLY GENERATES THE REQUEST
		return 1;
	}

	return (rv >= 0);
}

int bindSocket(struct Network *this) {
	struct addrinfo hints, *servinfo, *it;
	int yes = 1;
	int rv;
	char data[DATA_FLAG_BUFFER_SIZE];
	char message[DATA_FLAG_BUFFER_SIZE];

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE; // use my IP

	if ((rv = getaddrinfo(NULL, PORT, &hints, &servinfo)) != 0) {
		sprintf(data, "%s", gai_strerror(rv));
		Utils_format(message, ERR_CODE_UNABLE_TO_CONNECT_WRONG_ADRESS, "host", data);
		this->error->set(this->error, message);
		return 1;
	}
	// loop through all the results and bind to the first we can
	for (it = servinfo; it != NULL; it = it->ai_next) {
		if ((this->listeningSocket = socket(it->ai_family, it->ai_socktype,
				it->ai_protocol)) == -1) {
			perror("server: socket");
			continue;
		}
		if (setsockopt(this->listeningSocket, SOL_SOCKET, SO_REUSEADDR, &yes,
				sizeof(int)) == -1) {
			perror("setsockopt");
			return 0;
		}
		if (bind(this->listeningSocket, it->ai_addr, it->ai_addrlen) == -1) {
			close(this->listeningSocket);
			sprintf(data, "%s", gai_strerror(-1));
			Utils_format(message, ERR_CODE_UNABLE_TO_BIND, DEFAULT_PADDING, data);
			this->error->set(this->error, message);
			continue;
		}
		break;
	}
	if (it == NULL) {
		return 0;
	}

	freeaddrinfo(servinfo); // all done with this structure
	if (listen(this->listeningSocket, BACKLOG) == -1) {
		perror("listen");
		this->terminate->set(this->terminate, NULL);
		//		exit(1);
	}
	return 1;
}

int isDisconnect(const char* msg) {
	char type[NETWORK_CODE_LENGTH];

	if (!Utils_tokenizeMessage(msg, type, NULL, NULL))
		return 0;

	if (strcmp(type, N_CODE_USER_DISSCONNECTED) == 0)
		return 1;
	else
		return 0;
}

void* run_ManagerThread(void* threadData) {
	struct Network* this = (struct Network*) threadData;
	struct UserThreadIterator* it;
	struct UserThread* temp;
	char* dataFromUser;

	for (;;) {

		//		if (this->terminate->isActive(this->terminate))
		//			return NULL;
		//
		//		it = this->users->iterator(this->users);
		//		while (it->hasNext(it)) {
		//			temp = it->next(it);
		//			if (NULL == temp)
		//				break;
		//			if (temp->hasData(temp)) {
		//				this->dataFromNetwork->set(this->dataFromNetwork,
		//						temp->getData(temp));
		//			}
		//		}
		if (this->terminate->isActive(this->terminate))
			return NULL;

		it = this->users->iterator(this->users);
		while (it->hasNext(it)) {
			temp = it->next(it);
			if (NULL == temp)
				break;

			if (temp->hasData(temp)) {
				dataFromUser = temp->getData(temp);
				//if the user is disconnected then we must remove him from the list.
				if (isDisconnect(dataFromUser)) {
					//we must destroy the iterator first to release the lock on
					//the collection, else if we try to remove the user we'll get a
					// dead-lock.
					dest_UserThreadIterator(it);
					this->users->remove(this->users, temp);
					it = NULL;
					this ->dataFromNetwork->set(this->dataFromNetwork,
							dataFromUser);
					break;
				}
				this ->dataFromNetwork->set(this->dataFromNetwork, dataFromUser);
				free(dataFromUser);

			}
		}

		//we must under ALL circumstances destroy the iterator, if not we will always keep
		//the collection locked => DEAD-LOCK!
		dest_UserThreadIterator(it);

	}

	return NULL;
}

void *getIncomingAddress(struct sockaddr *sa) {
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*) sa)->sin_addr);
	}
	return &(((struct sockaddr_in6*) sa)->sin6_addr);
}

void* run_ListeningThread(void* threadData) {
	struct Network *this = (struct Network*) threadData;
	int newSocket;
	struct sockaddr_storage incomingAddress; // connector's address information
	socklen_t inSocketSize;
	char nameOfUser[INET6_ADDRSTRLEN];
	struct UserThread *user;
	char message[DATA_FLAG_BUFFER_SIZE];

	if (!bindSocket(this)) {
		this->terminate->set(this->terminate, NULL);
		pthread_exit(NULL);
	};

	while (1) {
		inSocketSize = sizeof incomingAddress;
		newSocket = accept(this->listeningSocket,
				(struct sockaddr *) &incomingAddress, &inSocketSize);
		if (newSocket == -1) {
			perror("accept");
			continue;
		}

		inet_ntop(incomingAddress.ss_family,
				getIncomingAddress((struct sockaddr *) &incomingAddress),
				nameOfUser, sizeof nameOfUser);

		//add to collection
		if (this->users->contains(this->users, nameOfUser)) {
			close(newSocket);
			continue;
		}

		user = new_UserThread(nameOfUser, newSocket);
		this->users->add(this->users, user);

		//if someone connected to us we send out the message with the connection request
		Utils_format(message, N_CODE_CON_REQUEST, nameOfUser, DEFAULT_PADDING);
		this->dataFromNetwork->set(this->dataFromNetwork, message);

		if (this->terminate->isActive(this->terminate))
			pthread_exit(NULL);
	}
	pthread_exit(NULL);
}

