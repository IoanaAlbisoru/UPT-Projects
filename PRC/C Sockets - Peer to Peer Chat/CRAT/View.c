#include "View.h"

struct View* new_View();
void dest_View(struct View*this);

void View_start(struct View *this);
void View_close(struct View *this);

int View_hasUserInput(struct View *this);
void View_printMessage(struct View*this, char *msg);
char* View_getUserInput(struct View*this);

//PRIVATE
void* run_InputThread(void*);
void* run_PrintThread(void*);

//IMPLEMENTATION

struct View* new_View() {
	struct View* temp = malloc(sizeof(struct View));
	temp->close = View_close;
	temp->getUserInput = View_getUserInput;
	temp->hasUserInput = View_hasUserInput;
	temp->printMessage = View_printMessage;
	temp->start = View_start;

	temp->userInput = new_DataFlag();
	temp->toPrint = new_DataFlag();
	temp->terminate = new_EmptyDataFlag();

	return temp;
}
void dest_View(struct View*this) {
	dest_DataFlag(this->userInput);
	dest_DataFlag(this->toPrint);
	dest_DataFlag(this->terminate);
	free(this);
}

void View_start(struct View *this) {
	pthread_attr_t thAttr;

	pthread_attr_init(&thAttr);
	pthread_attr_setdetachstate(&thAttr, PTHREAD_CREATE_DETACHED);

	if (pthread_create(&this->inputThread, &thAttr, run_InputThread, (void*) this))
		exit(1);

	if (pthread_create(&this->inputThread, &thAttr, run_PrintThread, (void*) this))
		exit(1);

	pthread_attr_destroy(&thAttr);
}
void View_close(struct View *this) {
	this->terminate->set(this->terminate, NULL);
}

int View_hasUserInput(struct View *this) {
	return this->userInput->isActive(this->userInput);
}
void View_printMessage(struct View*this, char *msg) {
	this->toPrint->set(this->toPrint, msg);
}
char* View_getUserInput(struct View*this) {
	return this->userInput->reset(this->userInput);
}

void* run_InputThread(void* threadData) {
	struct View* this = (struct View*) threadData;
	char buffer[512];

	for (;;) {
		if (NULL == fgets(buffer, 512, stdin))
			continue;

		if (strcmp(buffer, "") == 0) {
			continue;
		}

		Utils_clean(buffer);

		this->userInput->set(this->userInput, buffer);

		if (this->terminate->isActive(this->terminate))
			pthread_exit(NULL);

	}
	pthread_exit(NULL);
}

void* run_PrintThread(void* threadData) {
	struct View* this = (struct View*) threadData;
	char* msg;

	fprintf(stdout, WELCOME_MESSAGE);
	fprintf(stdout, LIST_OF_COMMANDS);

	for (;;) {
		if (this->terminate->isActive(this->terminate))
			pthread_exit(NULL);

		if (this->toPrint->isActive(this->toPrint)) {
			msg = this->toPrint->reset(this->toPrint);
			fprintf(stdout, "%s", msg);
			free(msg);
		}
	}
	pthread_exit(NULL);
}
