/*
 ============================================================================
 Name        : 			CRAT_EPSILON
 Author      : 			TEAM EPSILON
 Version     :				1.0
 Description : Basic chat client.

 Developers  : Bran Adela, gr. 1.1
 	 	 	   Bratu Anca, gr. 1.1
 	 	 	   Popu Alexandra, gr, 2.2
 	 	 	   Szakacs Lorand, gr, 4.4
 ============================================================================
 */
#include <stdio.h>
#include <stdlib.h>

#include "Control.h"
#include "Network.h"
#include "View.h"

int main(int argc, char * argv[]) {
	struct Network *network = new_Network();
	struct View *view = new_View();
	struct Control* control = new_Control(network, view);

	control->start(control);

	dest_Control(control);

	return EXIT_SUCCESS;
}
