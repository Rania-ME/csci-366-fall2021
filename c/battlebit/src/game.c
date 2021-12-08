

#include <stdlib.h>
#include <stdio.h>
#include "game.h"
#include <math.h>

static game * GAME = NULL;

void game_init() {
    if (GAME) {
        free(GAME);
    }
    GAME = malloc(sizeof(game));
    GAME->status = CREATED;
    game_init_player_info(&GAME->players[0]);
    game_init_player_info(&GAME->players[1]);
}

void game_init_player_info(player_info *player_info) {
    player_info->ships = 0;
    player_info->hits = 0;
    player_info->shots = 0;
}

int game_fire(game *game, int player, int x, int y) {
    unsigned long long mask = xy_to_bitval(x,y);
    GAME->players[player].shots += mask;
    bool isUsed = false;

    if((player == 1) && (GAME->status = PLAYER_1_TURN)) {
        GAME->status = PLAYER_0_TURN;
        player = 0;

        if(mask & GAME->players[0].ships) {
            GAME->players[0].ships -= mask;
            GAME->players[1].hits += mask;
            isUsed = true;
            if(GAME->players[0].ships == '\0') {
                GAME->status = PLAYER_1_WINS;
            }
            return 1;
        } else {
            return 0;
        }
    }
    else if ((player == 0) && (GAME->status = PLAYER_0_TURN)) {
        GAME->status = PLAYER_1_TURN;
        player = 1;

        if(mask & GAME->players[1].ships) {
            GAME->players[1].ships -= mask;
            GAME->players[0].hits += mask;
            isUsed = true;
            if(GAME->players[1].ships == '\0') {
                GAME->status = PLAYER_0_WINS;
            }
            return 1;
        } else {
            return 0;
        }
    }
}

unsigned long long int xy_to_bitval(int x, int y) {
    unsigned long long int number = 0;
    if(x > 7) {
        if(y < 7) {
            return 0;
        }
        return 0;
    } else {
        number = (x+(y*8));
    }

    unsigned long long res = 1;
    unsigned long long temp = 2;

    while (number > 0) {
        if (number & 1) {
            res *= temp;
        }
        number >>= 1;
        temp *= temp;
    }
    return res;
}

struct game * game_get_current() {
    return GAME;
}

int game_load_board(struct game *game, int player, char *spec) {
    long long int d = 0;

    if(spec == NULL) {
        return -1;
    }

    int current = 0;
    int temp1 = 0;
    int temp2 = 0;
    int x = 0;
    int y = 0;
    int array[5] = {0,0,0,0,0};
    int j = 0;

    while(spec[j] != '\0') {
        j += 1;
    }

    for (int i = 0; i < 15; i+=3) {

        current = (int)spec[i];
        temp1 = (int) spec[i + 1];
        temp2 = (int) spec[i + 2];
        x = temp1-48;
        y = temp2-48;

        if(player == 1) {
            GAME->status = PLAYER_0_TURN;
        } else {
            GAME->status = CREATED;
        }
        if((d == -1) || ((x < 0) || (y < 0)) || (j != 15)) {
            return -1;
        } else if((current == 67) && (array[0] == 0)) {
            d = add_ship_horizontal(&GAME->players[player], x, y, 5);
            array[0] = 1;
        } else if((current == 99) && (array[0] == 0)) {
            d = add_ship_vertical(&GAME->players[player], x, y, 5);
            array[0] = 1;
        } else if((current == 66) && (array[1] == 0)) {
            d = add_ship_horizontal(&GAME->players[player], x, y, 4);
            array[1] = 1;
        } else if((current == 98) && (array[1] == 0)) {
            d = add_ship_vertical(&GAME->players[player], x, y, 4);
            array[1] = 1;
        } else if((current == 68) && (array[2] == 0)) {
            d = add_ship_horizontal(&GAME->players[player], x, y, 3);
            array[2] = 1;
        } else if((current== 100) && (array[2] == 0)) {
            d = add_ship_vertical(&GAME->players[player], x, y, 3);
            array[2] = 1;
        } else if((current == 83) && (array[3] == 0)) {
            d = add_ship_horizontal(&GAME->players[player], x, y, 3);
            array[3] = 1;
        } else if((current == 115) && (array[3] == 0)) {
            d = add_ship_vertical(&GAME->players[player], x, y, 3);
            array[3] = 1;
        } else if((current == 80) && (array[4] == 0)) {
            d = add_ship_horizontal(&GAME->players[player], x, y, 2);
            array[4] = 1;
        } else if((current == 112) && (array[4] == 0)) {
            d = add_ship_vertical(&GAME->players[player], x, y, 2);
            array[4] = 1;
        } else if(current == 71) {
            GAME->players[player].ships = GAME->players[player].ships | xy_to_bitval(0,0);
            return 1;
        } else {
            d = -1;
        }
    }
}




int add_ship_horizontal(player_info *player, int x, int y, int length) {
    unsigned long long mask = xy_to_bitval(x,y);
    if(length !=0) {
        if (x > 7 || x < 0) {
            if (y > 7 || y < 0) {
                return -1;
            }
            return -1;
        }
    }
    if(length == 0){
        return 1;
    }
    if (player->ships & mask){
        return -1;
    }
    player->ships |= mask;
    ++x;
    --length;
    return add_ship_horizontal(player, x, y, length);//test
}

int add_ship_vertical(player_info *player, int x, int y, int length) {
    unsigned long long mask = xy_to_bitval(x,y);
    if(length !=0) {
        if (x > 7 || x < 0) {
            if (y > 7 || y < 0) {
                return -1;
            }
            return -1;
        }
    }
    if(length == 0) {
        return 1;
    }
    if (player->ships & mask) {
        return -1;
    }
    player->ships |= mask;
    ++y;
    --length;
    return add_ship_vertical(player, x, y, length);
}
