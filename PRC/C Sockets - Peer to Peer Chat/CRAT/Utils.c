#include "Utils.h"

int Utils_tokenizeMessage(const char* msg, char* type, char * user, char* data) {
	char temp[1024];
	char *token;

	strcpy(temp, msg);

	if (msg == NULL)
		return 0;

	//this is the TYPE; this will NEVER BE DEFAULT_PADDING;
	token = strtok(temp, DELIMITER);
	if (token == NULL)
		return 0;

	if (type != NULL)
		strcpy(type, token);

	//this is the userName
	token = strtok(NULL, DELIMITER);
	if (token == NULL)
		return 0;
	if (user != NULL) {
		if (strcmp(token, DEFAULT_PADDING) != 0)
			strcpy(user, token);
		else
			strcpy(user, "");
	}

	token = strtok(NULL, DELIMITER);
	if (token == NULL)
		return 0;
	if (data != NULL) {
		if (strcmp(token, DEFAULT_PADDING) != 0)
			strcpy(data, token);
		else
			strcpy(data, "");
	}
	return 1;
}

void Utils_format(char*result, const char* type, const char* user,const char* data) {
	sprintf(result, "%s%s%s%s%s", type, DELIMITER, user, DELIMITER, data);
}

void Utils_clean(char*string) {
	char* temp = &string[strlen(string) - 1];
	temp[0] = 0;
}
