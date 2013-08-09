#include "UserThread.h"

struct UserThread* new_UserThread(const char* userName, int socket);
void dest_UserThread(struct UserThread*);

int UserThread_sendData(struct UserThread*, char*);
char* UserThread_getData(struct UserThread*);
int UserThread_hasData(struct UserThread*);
int UserThread_equals(struct UserThread*, struct UserThread*);
void UserThread_close(struct UserThread*);

//PRIVATE

void* run_ReceivingThread(void*);
void* run_SenderThread(void*);
//IMPLEMENTATION

struct UserThread* new_UserThread(const char* userName, int socket) {
	struct UserThread* temp = malloc(sizeof(struct UserThread));
	pthread_attr_t thAttr;

	temp->equals = UserThread_equals;
	temp->getData = UserThread_getData;
	temp->hasData = UserThread_hasData;
	temp->sendData = UserThread_sendData;
	temp->close = UserThread_close;

	temp->terminate = new_EmptyDataFlag();
	temp->networkData = new_DataFlag();
	temp->dataToSend = new_DataFlag();

	temp->socket = socket;
	temp->userName = malloc(strlen(userName));
	strcpy(temp->userName, userName);

	pthread_attr_init(&thAttr);
	pthread_attr_setdetachstate(&thAttr, PTHREAD_CREATE_JOINABLE);

	pthread_create(&temp->receiverThread, &thAttr, run_ReceivingThread,
			(void*) temp);

	if (0 != pthread_create(&temp->senderThread, &thAttr, run_SenderThread,
			(void*) temp))
		return 0;

	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);

	pthread_attr_destroy(&thAttr);
	return temp;
}
struct UserThread* new_MockUserThread(const char* userName, int socket) {
	struct UserThread* temp = malloc(sizeof(struct UserThread));

	temp->equals = UserThread_equals;
	temp->getData = UserThread_getData;
	temp->hasData = UserThread_hasData;
	temp->sendData = UserThread_sendData;
	temp->close = UserThread_close;

	temp->networkData = NULL;
	temp->dataToSend = NULL;
	temp->terminate = NULL;

	temp->socket = socket;
	temp->userName = malloc(strlen(userName));
	strcpy(temp->userName, userName);
	return temp;
}

void dest_UserThread(struct UserThread*this) {
	dest_DataFlag(this->networkData);
	dest_DataFlag(this->dataToSend);
	dest_DataFlag(this->terminate);
	free(this->userName);
	free(this);
}

int UserThread_sendData(struct UserThread*this, char* dataToSend) {
	return this->dataToSend->set(this->dataToSend, dataToSend);
}

char* UserThread_getData(struct UserThread*this) {
	return this->networkData->reset(this->networkData);
}
int UserThread_hasData(struct UserThread*this) {
	return this->networkData->isActive(this->networkData);
}

int UserThread_equals(struct UserThread*this, struct UserThread*toCompare) {
	if (strcmp(this->userName, toCompare->userName) == 0)
		return 1;
	return 0;
}

void UserThread_close(struct UserThread*this) {
	this->terminate->set(this->terminate, NULL);

	pthread_join(this->senderThread, NULL);
	pthread_cancel(this->receiverThread);
	pthread_join(this->receiverThread, NULL);

}

void* run_ReceivingThread(void* threadData) {
	struct UserThread *this = (struct UserThread*) threadData;
	char buff[DATA_FLAG_BUFFER_SIZE];
	char type[NETWORK_CODE_LENGTH];
	char messageForNetwork[DATA_FLAG_BUFFER_SIZE];
	char message[DATA_FLAG_BUFFER_SIZE];

	int bytesReceived = 0;

	for (;;) {
		if (this->terminate->isActive(this->terminate)) {
			pthread_exit(NULL);
		}

		memset(buff, 0, DATA_FLAG_BUFFER_SIZE);
		bytesReceived = recv(this->socket, buff, DATA_FLAG_BUFFER_SIZE,
				MSG_NOSIGNAL);

		//means that the other side has closed the connection
		if (0 == bytesReceived) {
			Utils_format(message, N_CODE_USER_DISSCONNECTED, this->userName,
					DEFAULT_PADDING);
			this->terminate->set(this->terminate, NULL);
			this->networkData->set(this->networkData, message);
			pthread_exit(NULL);
		}

		//we announce the network that we have data;
		memset(message, 0, DATA_FLAG_BUFFER_SIZE);
		if (!Utils_tokenizeMessage(buff, type, NULL, message))
			continue;
		Utils_format(messageForNetwork, type, this->userName, message);
		this->networkData->set(this->networkData, messageForNetwork);
	}
	pthread_exit(NULL);
}

void* run_SenderThread(void* threadData) {
	struct UserThread *this = (struct UserThread*) threadData;
	char* buffer;

	for (;;) {
		if (this->terminate->isActive(this->terminate)) {
			pthread_exit(NULL);
		}

		if (this->dataToSend->isActive(this->dataToSend)) {
			buffer = this->dataToSend->reset(this->dataToSend);

			send(this->socket, buffer, strlen(buffer), MSG_NOSIGNAL);
			free(buffer);
		}
	}
	pthread_exit(NULL);
}
