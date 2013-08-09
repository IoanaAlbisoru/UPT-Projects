#ifndef CONTROL_H_
#define CONTROL_H_

#include "Network.h"
#include "View.h"
#include "Utils.h"
#include <string.h>
#include <pthread.h>

struct Control {
	void (*start)(struct Control*);
	struct Network* network;
	struct View* view;

	pthread_t networkListener;
	pthread_t errorListener;

	struct DataFlag* terminate;

};

struct Control* new_Control(struct Network*, struct View*);
void dest_Control(struct Control*);

#endif /* CONTROL_H_ */
