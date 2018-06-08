#include <time.h>
#include <algorithm>
#include <math.h>
#include <cstdlib>
#include <stdio.h>
#include <iostream>
#include <vector>

#ifndef MIPS_MIN
#define MIPS_MIN 100
#endif

#ifndef MIPS_MAX
#define MIPS_MAX 1000
#endif

#ifndef MIN_INSTRUCTION
#define MIN_INSTRUCTION 100
#endif

#ifndef MAX_INSTRUCTION
#define MAX_INSTRUCTION 10000
#endif

void vectors_gen(int num_elements, int min, int max) {

	srand(time());

	for (int i = 0; i < num_elements; i++)
	{
		std::cout << (rand() % (max - min)) + min  << "\n";
	}
}

int main(int argc, char** argv) {

	if (argc < 2) {
		printf(
				"Parameters needed: <number of task>\n\n");
		return 0;
	}

	//int number_of_machines = atoi(argv[1]);
	int number_of_cloudlets = atoi(argv[1]);

	//printf("%d\n", number_of_machines);
	//vectors_gen(number_of_machines, MIPS_MIN, MIPS_MAX);
	//printf("\n");

	printf("%d\n", number_of_cloudlets);
	vectors_gen(number_of_cloudlets, MIN_INSTRUCTION, MAX_INSTRUCTION);
	printf("\n");

	return 0;
}

