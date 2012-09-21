/*******************************************************************************
* File:    hw2-4.pl
* Purpose: Encrypts a given plaintext using the Hill Cipher
* Authors: Michael Alfield <maltfield@knights.ucf.edu>
*          Joe Castellanos
* Course:  CIS 3362 <http://www.cs.ucf.edu/courses/cis3362/fall2012/>
* Created: 2012-09-21
* Updated: 2012-09-21
* Notes:   This program must be compiled using the GNU99 standard
*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>

#define DEBUG 0
#if !defined(BUFSIZ)
#define BUFSIZ 256
#endif

int main(){

	// DECLARE VARIABLES
	FILE* inFile;	
	int matrixSize;
	int** key;
	int* tmp;

	char plaintext[BUFSIZ];
	char encrypted[BUFSIZ];

	/*************
	* FILE INPUT *
	*************/

	inFile = fopen( "hill.txt", "r" );
	fscanf( inFile, "%d", &matrixSize );

	// MATRIX ALLOC'S

	// key is a square matrix
	key = (int**) calloc( matrixSize, sizeof(int) );
	for( int i=0; i<matrixSize; i++ ){
		key[i] = (int*) calloc( matrixSize, sizeof(int) );
	}

	// tmp is a column vector (cols=1) with the same number of rows as the key
	tmp = (int*) calloc( matrixSize, sizeof(int) );

	// fill key matrix
	if( DEBUG ){ printf( "Encryption Key (below):\n" ); }

	for( int i=0; i<matrixSize; i++ ){
		for( int j=0; j<matrixSize; j++ ){

			fscanf( inFile, "%d", &key[i][j] );
			if( DEBUG ){ printf( "%d\t", key[i][j] ); }

		}
		if( DEBUG ){ printf( "\n" ); }
	}

	// get plaintext that we shall encrypt
	fscanf( inFile, "%s", &plaintext );
	if( DEBUG ){ printf( "PLAINTEXT: %s\n\n", plaintext ); }

	/*************
	* ENCRYPTION *
	*************/

	// loop through every letter in the key
	int c = 0;
	while( plaintext[c] != 0 ){
		if( DEBUG > 1 ){ printf( "Column Vector:\n" ); }

		// fill each row of the 'tmp' column vector
		for( int i=0; i<matrixSize; i++ ){

			tmp[i] = plaintext[c]-97;
			if( DEBUG > 1 ){ printf( "\t%d (%c)\n", tmp[i], plaintext[c] ); }


			// advance to the next letter in the plaintext message (if possible)
			if( plaintext[++c] == 0 ){
				// pad the plaintext, if necessary
				while( ++i<matrixSize ){
					tmp[i] = 'x' - 97;
					c++;
				}
			}
		}

		// MATRIX MULTIPLICATION
		// loop through each row of the column vector
		for( int i=0; i<matrixSize; i++ ){

			// DECLARE VARIABLES
			int sum = 0;

			// loop through each column of the key
			for( int j=0; j<matrixSize; j++ ){

				sum = sum + key[i][j] * tmp[j];
				if( DEBUG > 1 ){
					printf( "\t\t%d * %d += %d\n", key[i][j], tmp[j], sum );
				}
			}

			if( DEBUG > 1 ){ printf( "encrypted pos #%d\n", c-(matrixSize-i) ); }
			if( DEBUG > 1 ){ printf( "\n" ); }

			encrypted[c-(matrixSize-i)] = sum % 26 + 97;

		}

	}

	// print result!
	printf( "%s\n", encrypted );

	/**********
	* CLEANUP *
	**********/

	// dealloc matricies
	for( int i=0; i<matrixSize; i++ ){
		free( key[i] );
	}
	free( key );
	free( tmp );

	printf( "\n" );
	return 0;
}
