#include <iostream>
#include <vector>

using namespace std;

int main(int s){
    int test = 1;
    bool flag = false;
    string str = "asdasd";
    int tablica[] = {1,2,3,4};

    if(flag && test){
        test = 2;
    }

    if(1 > 0 || test == 2) {
        test = 3 - 2;
    }

    while(test) {
        test++;
        test++;
    }

    while(flag == false) {
        flag = !flag;
    }

    int b = 2 + 3;

    if (b == 3) {
        return b * 2;
    }
    return 12 % b;
}

void test(void){
    main(10 / 2);
}

bool boolFun(int i, int j){
    return i + j;
}