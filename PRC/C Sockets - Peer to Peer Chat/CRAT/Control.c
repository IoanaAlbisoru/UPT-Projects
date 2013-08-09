#include "Control.h"

void Control_start(struct Control*this);
struct Control* new_Control(struct Network* net, struct View* view);
void dest_Control(struct Control*this);
/*
 * ------------------------------------------------------------------------------
 * ------------------------------------PRIVATE ----------------------------------
 * ------------------------------------------------------------------------------
 */

/*
 * the connReq flag is a way for the userInputListener thread and the NetworkListener thread
 * coordinate when a user is trying to connect to us.
 * Once someone is trying to connect the network notifies the NetworkListener thread,
 * it then set this flag to active. And the userInputListener will automatically interpret the
 * next user input as a response to that request.
 */
struct DataFlag* connReq;
/*
 *	run_NetworkListener is a daemon that responds to the data received on the network
 *	it interprets the data based on the following protocol:
 *
 *	CODE~!~USER IT RECEIVED FROM~!~DATA
 *
 */
void* run_NetworkListener(void *);

/*
 * run_ErrorListener is a daemon that responds to the errors that might arise in the
 * networking part of the system.
 */
void* run_ErrorListener(void *);

/*
 * run_UserInputListener is a daemon that responds to user input.
 * it interprets the user input as commands and delegates the work
 * to the appropriate components
 */
void run_UserInputListener(struct Control *this);

void quitHandler(struct Control*this);

int Control_UserInputType(const char *msg, char* user, char* data);
int Control_NetworkDataType(const char *msg, char* user, char* data);
int Control_ErrorType(const char *msg, char* user, char* data);
/*
 * ------------------------------------------------------------------------------
 * ---------------------------------IMPLEMENTATION-------------------------------
 * ------------------------------------------------------------------------------
 */

struct Control* new_Control(struct Network* net, struct View* view) {
	struct Control *temp = malloc(sizeof(struct Control));
	temp->start = Control_start;

	temp->network = net;
	temp->view = view;

	temp->terminate = new_EmptyDataFlag();
	connReq = new_DataFlag();

	return temp;
}

void dest_Control(struct Control*this) {
	dest_Network(this->network);
	dest_View(this->view);
	dest_DataFlag(this->terminate);
	dest_DataFlag(connReq);
	free(this);
}

void Control_start(struct Control*this) {
	pthread_attr_t thAttr;

	pthread_attr_init(&thAttr);
	pthread_attr_setdetachstate(&thAttr, PTHREAD_CREATE_JOINABLE);

	pthread_create(&this->networkListener, &thAttr, run_NetworkListener,
			(void*) this);
	pthread_create(&this->errorListener, &thAttr, run_ErrorListener,
			(void*) this);

	pthread_attr_destroy(&thAttr);

	this->network->start(this->network);
	this->view->start(this->view);
	run_UserInputListener(this);

}

void quitHandler(struct Control*this) {

	this->terminate->set(this->terminate, NULL);

	//we close the other components
	this->view->close(this->view);
	this->network->close(this->network);

	//and we wait for the threads to finish
	pthread_join(this->errorListener, NULL);
	pthread_join(this->networkListener, NULL);
	return;
}

void run_UserInputListener(struct Control *this) {
	char *tempStr;
	char data[DATA_FLAG_BUFFER_SIZE];
	char paddedMsg[DATA_FLAG_BUFFER_SIZE];
	char destUser[128];
	int msgType = 0;
	char toPrint[DATA_FLAG_BUFFER_SIZE];
	for (;;) {
		//we sit still until there is user input
		if (!this->view->hasUserInput(this->view))
			continue;

		tempStr = this->view->getUserInput(this->view);
		memset(data, 0, DATA_FLAG_BUFFER_SIZE);
		memset(destUser, 0, 128);
		msgType = Control_UserInputType(tempStr, destUser, data);
		//we can free(tempStr) because the view->getUserInput always return
		//the data in a new chunk of memory, it never gives out pointers to its
		//own internam buffer;
		free(tempStr);

		switch (msgType) {
		/*
		 * handler for the refresh command.
		 */
		case TYPE_REFRESH:
			this->view->printMessage(this->view, "");
			break;

			/*
			 * pretty obvious, isn't it?
			 */
		case TYPE_USER_QUIT:
			quitHandler(this);
			return;
			break;

		case TYPE_USER_CONNECT:
			this->network->connect(this->network, destUser);
			break;

		case TYPE_USER_MESSAGE:
			if (this->network->hasUser(this->network, destUser)) {
				Utils_format(paddedMsg, N_CODE_MESSAGE, destUser, data);
				this->network->sendMessage(this->network, paddedMsg, destUser);
			} else {
				sprintf(toPrint, "INVALID USER: %s\n", destUser);
				this->view->printMessage(this->view, toPrint);
			}
			break;

		case TYPE_USE_CON_RESPONSE:
			if (strcmp(data, "y") == 0) {
				tempStr = connReq->reset(connReq);
				Utils_format(paddedMsg, N_CODE_CON_RESPONSE, DEFAULT_PADDING,
						N_CON_ACCEPTED);
				this->network->sendMessage(this->network, paddedMsg, tempStr);
			} else {
				tempStr = connReq->reset(connReq);
				Utils_format(paddedMsg, N_CODE_CON_RESPONSE, DEFAULT_PADDING,
						N_CON_DECLINED);
				this->network->sendMessage(this->network, paddedMsg, tempStr);
				this->network->disconnect(this->network, tempStr);
			}
			break;

		case TYPE_STATUS_SET:
			if (strcmp(data, USER_STATUS_TEXT_BUSY) == 0) {
				this->network->setStatus(this->network, USER_STATUS_BUSY);
			} else
				this->network->setStatus(this->network, USER_STATUS_AVAILABLE);
			break;

		case TYPE_STATUS_CHECK:
			if (USER_STATUS_BUSY == this->network->getStatus(this->network))
				sprintf(toPrint, "Your status is: %s\n", USER_STATUS_TEXT_BUSY);
			else
				sprintf(toPrint, "Your status is: %s\n",
						USER_STATUS_TEXT_AVAILABLE);
			this->view->printMessage(this->view, toPrint);
			break;

		case TYPE_USER_INVALID_COMMAND:
			this->view->printMessage(this->view, "INVALID COMMAND\n");
			break;
		default:
			this->view->printMessage(this->view,
					"WHAT THE HELL JUST HAPPENED!! THIS SHOULD NEVER EXECUTE\n");
			break;
		}
		//END USER INPUT BRANCH

	}
}

void* run_NetworkListener(void*threadData) {
	struct Control* this = (struct Control*) threadData;
	char* tempStr;
	char data[DATA_FLAG_BUFFER_SIZE];
	char toPrint[DATA_FLAG_BUFFER_SIZE];
	char sendingUser[50];
	int msgType;

	for (;;) {
		if (this->terminate->isActive(this->terminate))
			pthread_exit(NULL);

		if (!this->network->hasNetworkData(this->network))
			continue;

		//if we have user intput we start processing it;
		tempStr = this->network->getMessage(this->network);
		memset(data, 0, DATA_FLAG_BUFFER_SIZE);
		memset(sendingUser, 0, 128);
		msgType = Control_NetworkDataType(tempStr, sendingUser, data);
		// we can free(tempStr) because this->network->getMessage always return a
		// new chunk of memory, never a pointer to its data;
		free(tempStr);

		switch (msgType) {
		/*
		 * If a user tries to connect to us the network notifies us and asks us what
		 * we want to do.
		 * If our status is BUSY then the connection is refused automatically without
		 * the user's input.
		 */
		case N_TYPE_CON_REQUEST:

			if (this->network->getStatus(this->network) == USER_STATUS_BUSY) {
				Utils_format(data, N_CODE_CON_RESPONSE, DEFAULT_PADDING,
						N_CON_DECLINED);
				this->network->sendMessage(this->network, data, sendingUser);
				this->network->disconnect(this->network, sendingUser);
				sprintf(toPrint,
						"User %s just tried to connect. Connection refused.\n",
						sendingUser);
				this->view->printMessage(this->view, toPrint);
				break;
			}
			sprintf(toPrint, "User %s is trying to connect, accept?[y/n]\n",
					sendingUser);
			//we announce the user input thread that we want user input regarding the connection
			connReq->set(connReq, sendingUser);
			this->view->printMessage(this->view, toPrint);
			break;

			/*
			 * we receive this type of message after we wanted to connect to another user.
			 * This message contains the response of that user. ACCEPT/DECLINE
			 */
		case N_TYPE_CON_RESPONSE:
			if (strcmp(data, N_CON_ACCEPTED) == 0) {
				sprintf(toPrint, "User %s has accepted your connection.\n",
						sendingUser);
				this->view->printMessage(this->view, toPrint);

				//we send a confirmation back to the user that accepted our request;
				Utils_format(data, N_CODE_CON_CONFIRMATION, DEFAULT_PADDING,
						DEFAULT_PADDING);
				this->network->sendMessage(this->network, data, sendingUser);
			} else {
				sprintf(toPrint, "User %s has refused your connection.\n",
						sendingUser);
				this->view->printMessage(this->view, toPrint);
			}
			break;

			/*
			 * After we've accepted a connection request we receive one last confirmation
			 * from the other party.
			 */
		case N_TYPE_CON_CONFIRMATION:
			sprintf(toPrint, "You are now connected to user %s.\n", sendingUser);
			this->view->printMessage(this->view, toPrint);
			break;

			/*
			 * This is a standard user message
			 */
		case N_TYPE_MESSAGE:
			sprintf(toPrint, "%s: %s\n", sendingUser, data);
			this->view->printMessage(this->view, toPrint);
			break;

			/*
			 * If a user disconnected then the network notifies us
			 */
		case N_TYPE_USER_DISSCONNECTED:
			sprintf(toPrint, "User %s just disconnected\n", sendingUser);
			this->view->printMessage(this->view, toPrint);
			break;

			/*
			 * Well, as it says: unknown. This shouldn't happen
			 */
		case N_TYPE_UNKNOWN:
			sprintf(toPrint,
					"We just received something completely unknown from the network\n");
			this->view->printMessage(this->view, toPrint);
			break;

			/*
			 * well, even if the above DOES happen; this surely will never happen
			 */
		default:
			sprintf(toPrint, "Something unknown just happened\n");
			this->view->printMessage(this->view, toPrint);
			break;

		}
	}
	pthread_exit(NULL);
}
/*
 *
 */
void* run_ErrorListener(void*threadData) {
	struct Control* this = (struct Control*) threadData;

	char* tempStr;
	char data[DATA_FLAG_BUFFER_SIZE];
	char toPrint[DATA_FLAG_BUFFER_SIZE];
	char user[50];
	int msgType;

	for (;;) {

		if (this->terminate->isActive(this->terminate))
			pthread_exit(NULL);

		if (!this->network->hasError(this->network))
			continue;

		tempStr = this->network->getError(this->network);
		memset(data, 0, DATA_FLAG_BUFFER_SIZE);
		memset(user, 0, 128);
		msgType = Control_ErrorType(tempStr, user, data);
		free(tempStr);

		switch (msgType) {
		case ERR_TYPE_USER_ALREADY_CONNECTED:
			sprintf(toPrint, "Error: user %s is already connected.\n", user);
			this->view->printMessage(this->view, toPrint);
			break;

		case ERR_TYPE_UNABLE_TO_CONNECT_WRONG_ADRESS:
			sprintf(toPrint, "Error at getting address info for %s: %s", user,
					data);
			this->view->printMessage(this->view, toPrint);
			break;

		case ERR_TYPE_UNABLE_TO_BIND:
			sprintf(toPrint, "Unable to bind port: %s", data);
			this->view->printMessage(this->view, toPrint);
			break;

		case ERROR_UNKNOWN:
			break;

		default:
			break;
		}
	}
	pthread_exit(NULL);
}

int Control_UserInputType(const char *msg, char* user, char* data) {
	char * temp;

	//this flag is tells the userInputListener thread that we've received a request
	// for a network connection so the user input is always interpreted as a response
	//to said request
	if (connReq->isActive(connReq)) {
		strcpy(data, msg);
		return TYPE_USE_CON_RESPONSE;
	}

	// * ; refreshes the screen.
	if (strncmp(msg, USER_REFRESH, USER_REFRESH_LENGHT) == 0)
		return TYPE_REFRESH;

	// /quit
	if (strncmp(msg, USER_COMMAND_QUIT, LEN_QUIT) == 0)
		return TYPE_USER_QUIT;

	// Format: /connect username
	// it stores the username in 'user'
	if (strncmp(msg, COMMAND_CONNECT, LEN_CONNECT) == 0) {
		strcpy(user, &msg[LEN_CONNECT + 1]);
		return TYPE_USER_CONNECT;
	}

	//Format: @username message
	// it stores the username in 'user' and the message in 'data'
	if (strncmp(msg, COMMAND_MSG, LEN_MSG_CMD) == 0) {
		strncpy(user, &msg[1], strcspn(msg, " ") - 1);
		temp = strchr(msg, ' ');
		if (temp == NULL) {
			strcpy(data, "");
			return TYPE_USER_MESSAGE;
		}
		temp++;
		strcpy(data, temp);
		return TYPE_USER_MESSAGE;
	}

	//Format: /status busy
	// /status available
	// /status prints the current status

	if (strncmp(msg, USER_COMMAND_STATUS, LEN_STATUS_CMD) == 0) {
		temp = strchr(msg, ' ');
		if (temp == NULL) {
			strcpy(data, "");
			return TYPE_STATUS_CHECK;
		}
		temp++;
		strcpy(data, temp);
		return TYPE_STATUS_SET;
	}

	return TYPE_USER_INVALID_COMMAND;
}
int Control_NetworkDataType(const char *msg, char* user, char* data) {
	char type[NETWORK_CODE_LENGTH];

	if (!Utils_tokenizeMessage(msg, type, user, data))
		return N_TYPE_UNKNOWN;

	if (0 == strcmp(type, N_CODE_MESSAGE))
		return N_TYPE_MESSAGE;

	if (0 == strcmp(type, N_CODE_CON_REQUEST))
		return N_TYPE_CON_REQUEST;

	if (0 == strcmp(type, N_CODE_CON_RESPONSE))
		return N_TYPE_CON_RESPONSE;

	if (0 == strcmp(type, N_CODE_CON_CONFIRMATION))
		return N_TYPE_CON_CONFIRMATION;

	if (0 == strcmp(type, N_CODE_USER_DISSCONNECTED))
		return N_TYPE_USER_DISSCONNECTED;

	return N_TYPE_UNKNOWN;
}

int Control_ErrorType(const char *msg, char* user, char* data) {
	char type[NETWORK_CODE_LENGTH];

	if (!Utils_tokenizeMessage(msg, type, user, data))
		return ERROR_UNKNOWN;

	if (0 == strcmp(type, ERR_CODE_USER_ALREADY_CONNECTED))
		return ERR_TYPE_USER_ALREADY_CONNECTED;

	if (0 == strcmp(type, ERR_CODE_UNABLE_TO_CONNECT_WRONG_ADRESS))
		return ERR_TYPE_UNABLE_TO_CONNECT_WRONG_ADRESS;

	if(0 == strcmp(type, ERR_CODE_UNABLE_TO_BIND))
		return ERR_TYPE_UNABLE_TO_BIND;

	return ERROR_UNKNOWN;
}
