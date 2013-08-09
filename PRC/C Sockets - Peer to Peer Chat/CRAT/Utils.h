#ifndef CONSTANTS_H_
#define CONSTANTS_H_

#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#define WELCOME_MESSAGE "---------WELCOME TO EPSILON_CRAT---------\n"

#define LIST_OF_COMMANDS "List of commands:\n/connect [username]\n@username [message]\n/status [busy/available]\n/status\n/quit\n"

#define TYPE_USER_QUIT 40
#define TYPE_USER_CONNECT 41
#define TYPE_USER_MESSAGE 42
#define TYPE_USER_INVALID_COMMAND 43
#define TYPE_USE_CON_RESPONSE 44
#define TYPE_REFRESH 45
#define TYPE_STATUS_SET 46
#define TYPE_STATUS_CHECK 47

#define USER_STATUS_BUSY 24
#define USER_STATUS_AVAILABLE 42

#define USER_REFRESH "*"
#define USER_REFRESH_LENGHT 1
#define USER_COMMAND_QUIT "/quit"
#define LEN_QUIT 5
#define COMMAND_CONNECT "/connect"
#define LEN_CONNECT 8
#define COMMAND_MSG "@"
#define LEN_MSG_CMD 1
#define USER_COMMAND_STATUS "/status"
#define LEN_STATUS_CMD 7

#define USER_STATUS_TEXT_BUSY "busy"
#define USER_STATUS_TEXT_AVAILABLE "available"

#define PORT "2307"
#define BACKLOG 10

//defines for messages from network
#define NETWORK_CODE_LENGTH 8
#define DEFAULT_PADDING "___"
#define DELIMITER "~!~"
#define DELIM_LENGTH 3

#define N_CON_ACCEPTED "ACCEPTED"
#define N_CON_DECLINED "DECLINED"

#define N_CODE_CON_REQUEST "CONN_REQ"
#define N_CODE_CON_RESPONSE "CONN_RES"
#define N_CODE_CON_CONFIRMATION "CONNCONF"
#define N_CODE_MESSAGE "MESSAGE_"
#define N_CODE_USER_DISSCONNECTED "_DISCONN"
#define N_TYPE_UNKNOWN 19
#define N_TYPE_CON_REQUEST 20
#define N_TYPE_CON_RESPONSE 21
#define N_TYPE_MESSAGE 22
#define N_TYPE_CON_CONFIRMATION 23
#define N_TYPE_USER_DISSCONNECTED 24

#define ERR_CODE_USER_ALREADY_CONNECTED "ERRUSERC"
#define ERR_CODE_UNABLE_TO_CONNECT_WRONG_ADRESS "ERRADRI"
#define ERR_CODE_UNABLE_TO_BIND "ERR_BIND"

#define ERR_TYPE_USER_ALREADY_CONNECTED 80
#define ERR_TYPE_UNABLE_TO_CONNECT_WRONG_ADRESS 81
#define ERR_TYPE_UNABLE_TO_BIND 82
#define ERROR_UNKNOWN 12

/*
 * Utils take a message of the format TYPE~!~USERNAME~!~DATA and returns every token in
 * its apropriate parameters
 *
 * Return:
 * 		0 if 'msg' WASN'T adhering to the protocol
 * 		1 if the tokenization was successful
 *
 * 	Usage: splitting up the protocol messages into the separate parts
 * 		   Checking wether or not a message is formated according to the protocol
 */
int
Utils_tokenizeMessage(const char* msg, char* type, char * user, char* data);

void Utils_format(char*result, const char* type, const char* user,
		const char* data);

void Utils_clean(char*string);
#endif /* CONSTANTS_H_ */
