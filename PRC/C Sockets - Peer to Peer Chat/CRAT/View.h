#ifndef VIEW_H_
#define VIEW_H_

#include <stdlib.h>
#include <stdio.h>
#include "DataFlag.h"
#include "Utils.h"

struct View {
	void (*start)(struct View *);
	void (*close)(struct View *);

	int (*hasUserInput)(struct View *);
	void (*printMessage)(struct View*, char *msg);
	char* (*getUserInput)(struct View*);

	struct DataFlag* userInput;
	struct DataFlag* toPrint;
	struct DataFlag* terminate;
	pthread_t inputThread;
	pthread_t printThread;
};

struct View* new_View();
void dest_View(struct View*this);

#endif /* VIEW_H_ */
