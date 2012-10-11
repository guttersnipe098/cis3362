// Arup Guha
// 11/7/06
// Solution for CIS 3362 DES Project

// There are many weaknesses in this solution due to my laziness!
// All of the constants in the algorithm should be stored in final
// static variables, but I just wanted to read in the information
// from the files instead of hard-coding them.

// Also, the key should stay the same for encrypting one file, but
// the blocks must change. This hasn't been indicated clearly.

import java.io.*;
import java.util.*;

public class DES {

	public static final int DEBUG = 3;
	
	private int[] key;
	private int[][] roundkeys;
	private int[] block;
	
	private static int[][][] stables;
	private static int[] IP;
	private static int[] IPInv;
	private static int[] E;
	private static int[] PC2;
	private static int[] P;
	private static int[] PC1;
	private static int[] keyshifts;		 
	
	// Reads all the information from the file I created based on the order
	// the values were stored in the file. My original posted file had some
	// errors in it, because some zeroes were stored as captial O's. I fixed 
	// those issues in the file and have posted the corrected file with this
	// solution.
	public DES(int[] thekey) throws Exception {
		
		key = new int[64];
		stables = new int[8][4][16];
		IP = new int[64];
		IPInv = new int[64];
		E = new int[48];
		PC2 = new int[48];
		P = new int[32];
		PC1 = new int[56];
		keyshifts = new int[16];
		block = new int[64];
		
		// Sets the key to what was passed in.
		for (int i=0; i<64; i++)
			key[i] = thekey[i];
		
		Scanner fin = new Scanner(new File("destables.txt"));
		
		// Reads in the initial permutation matrix.
		for (int i=0; i<64; i++)
			IP[i] = fin.nextInt();
			
		// Reads in the inverse of the initial permutation matrix.
		for (int i=0; i<64; i++)
			IPInv[i] = fin.nextInt();
			
		// Expansion matrix used in each round.
		for (int i=0; i<48; i++)
			E[i] = fin.nextInt();	
		
		// The permutation matrix P used in each round.
		for (int i=0; i<32; i++)
			P[i] = fin.nextInt();
		
		// Reads in the 8 S-boxes!
		for (int i=0; i<8; i++) {
		
			for (int j=0; j<64; j++) {
				stables[i][j/16][j%16] = fin.nextInt();
			}
		}
		
		// Reads in PC1, used for the key schedule.
		for (int i=0; i<56; i++)
			PC1[i] = fin.nextInt();
			
		// Reads in PC2 used for the round keys.	
		for (int i=0; i<48; i++)
			PC2[i] = fin.nextInt();
			
		// Reads in the shifts used for the key between each round.
		for (int i=0; i<16; i++)
			keyshifts[i] = fin.nextInt();
			
		fin.close();
	}
	
	// Sets a block based on the string of bits. The string is guaranteed
	// to only have characters '0' and '1'.
	public void setBlock(String bits) {
		for (int i=0; i<64; i++)
			block[i] = (int)(bits.charAt(i)-'0');
	}
	
	// Prints out a block with spaces after every 8 characters on one line.
	public void print(FileWriter fout) throws Exception {
		for (int i=0; i<64; i++) {
			if (i%8 == 0) fout.write(" ");		
			fout.write(""+block[i]);
		}
		
		fout.write("\n");
	}
	
	// Encrypts the current block.
	public void encrypt() {
		
		// Permute the block with the initial permutation.
		block = Permute(block, IP);
		
		// Run 16 rounds.
		for (int i=0; i<16; i++) {
			round(i);			
		}
		
		// Supposed to switch halves at the end and invert the initial
		// permutation.
		switchHalves();
		block = Permute(block, IPInv);
	}
	
	// Switches the left half of the current block with the right half.
	public void switchHalves() {
		int[] temp = new int[32];
		
		// We're just doing a regular swap between 32 bits...
		
		for (int i=0; i<32; i++)
			temp[i] = block[i];
			
		for (int i=0; i<32; i++)
			block[i] = block[32+i];
			
		for (int i=32; i<64; i++)
			block[i] = temp[i-32];
	}
	
	// Permutes the bits in original according to perm and
	// returns this permutation of the original bits.
	public static int[] Permute(int[] original, int[] perm) {
		
		
		int[] ans = new int[original.length];
		
		// Note: We subtract 1 from perm[i] because in the tables, the
		// permutations are 1-based, instead of 0-based.
		for (int i=0; i< perm.length; i++)
			ans[i] = original[perm[i]-1];
		return ans;		
	}
	
	// Takes the block of bits in whole from index start to index end, 
	// inclusive and cyclicly left-shifts them by numbits number of bits.
	public static void leftShift(int[] whole, int start, int end, int numbits) {
		int size = end-start+1;
		int[] temp = new int[size];
		
		// Copy the bits into temp in their new order.
		for (int i=0; i<temp.length; i++) 
			temp[i] = whole[start+(numbits+i)%size];	
		
		// Copy them back into the original array in the order we stored them
		// in temp, with the appropriate offset, start.
		for (int i=0; i<temp.length; i++)
			whole[start+i] = temp[i];
	}
	
	public static void printArray(int[] array) {
		for (int i=0; i<array.length; i++) {
			if (i%8 == 0) System.out.print(" ");
			System.out.print(array[i]);
		}
		System.out.println();
	}
	
	// Runs round num of DES.
	public void round(int num) {
		int[] left = new int[32];
		int[] right = new int[32];
		
		// Copy in the left and right blocks into temporary arrays.
		for (int i=0; i<32; i++)
			left[i] = block[i];
		for (int i=0; i<32; i++)
			right[i] = block[32+i];
		
		// Expand the right block.
		int[] expanded = E(right);
		
		// This is the XOR we want.
		int[] xorans = XOR(expanded, roundkeys[num]);

		// Run the s-boxes on all the appropriate "blocks".
		int[] sboxout = Sboxes(xorans);
		
		// Permute the S-box output.
		int[] fout = Permute(sboxout, P);
		
		// Then do the necessary XOR.
		fout = XOR(fout, left);
		
		// Copy the blocks back into their proper place!
		for (int i=0; i<32; i++)
			block[i] = right[i];
		for (int i=0; i<32; i++)
			block[32+i] = fout[i]; 
	}
	
	// Expand the 32 bits and return the corresponding 48 bits.
	public static int[] E(int[] bits) {
		int[] ans = new int[48];
		
		// Our permutation function doesn't work for this, so it's coded here.
		for (int i=0; i<48; i++)
			ans[i] = bits[E[i]-1];
		return ans;	
	}
	
	// Returns the XOR of the bit streams a and b.
	public static int[] XOR(int[] a, int[] b) {
		int[] ans = new int[a.length];
		for (int i=0; i<a.length; i++)
			ans[i] = (a[i]+b[i])%2;
		return ans;
	}
	
	// Returns the output of putting the 48 bit input through the
	// 8 S-boxes.
	public int[] Sboxes(int[] input) {
		int[] ans = new int[32];
		
		for (int i=0; i<8; i++) {
			
			// Just hard-coded this part. There doesn't seem to be a more
			// elegant way...
			int row = 2*input[6*i] + input[6*i+5];
			int col = 8*input[6*i+1]+4*input[6*i+2]+2*input[6*i+3]+input[6*i+4];
			
			int temp = stables[i][row][col];
			
			// We have to store the base-10 answer in binary, so we strip off the
			// bits one-by-one, in the usual manner from the least to most significant.
			for (int j=3; j>=0; j--) {
				ans[4*i+j] = temp%2;
				temp /= 2;
			}
		}
		return ans;
		
	}
	
	// Set up the keys for each round.
	public void setKeys() {
		roundkeys = new int[16][48];
		
		// Set the original key with PC1.	
		key = Permute(key, PC1);
		
		// Go through and set the round keys using the process by which they
		// are supposed to be computed.
		for (int i=0; i<16; i++) {
			
			// Supposed to left-shift both halves by the appropriate amount,
			// based on the round.
			leftShift(key, 0, 27, keyshifts[i]);
			leftShift(key, 28, 55, keyshifts[i]);
			
			// Now, just copy in the (i+1)th round key.
			for (int j=0; j<48; j++) {
				roundkeys[i][j] = key[PC2[j]-1];
			}				
		}
	}
	
	// Converts the string version of the key in HEX to binary which is
	// stored in an integer array of size 64...thus, the check bits are
	// included here.
	public static int[] getKey(String thekey) {
		int[] ans = new int[64];
		thekey = thekey.toLowerCase();
		
		// Go through all 16 characters.
		for (int i=0; i<16; i++) {
			int val = (int)(thekey.charAt(i));
			
			// We need to assign value separately if it is a digit or a letter.
			if ('0' <= val && val <= '9')
				val = val - '0';
			else
				val = val - 'a' + 10;
			
			// Peel off the binary bits as before...
			for (int j=3; j>=0; j--) {
				ans[4*i+j]=val%2;
				val /= 2;
			}
		}
		
		return ans;
	}
	
	// main has been replaced by encryptLine()
	/****************************************************************************
	* Name:    encryptBlock
	* Purpose: Encrypts a block of lines (each 10x Radix-64 characters) using DES
	* Input:   plaintext - an array of Strings (each element contains 10x
	*                      Radix-64 characters) for the plaintext that will be
	*                      encrypted
	*          key       - an array of integers (each element is either a 0 or a
	*                      1) for the symmetric key used to encrypt the plaintext
	* Output:  returns the DES cyphertext, padded as per the hw3 requirements
	****************************************************************************/
	public static String[] encryptBlock(String[] plaintext, int[] key) throws Exception {

		// DECLARE VARIABLES
		String[] output = new String[plaintext.length];
		output[0] = "";
		
		/*********************
		* CHECK INPUT SANITY *
		*********************/

		if( key.length != 64 ){
			System.out.println( "ERROR: invalid key length!" );
			return output;
		}

		System.out.print( "INFO: Using key:      " );
		System.out.println( key2Hex(key) );

/*
		Scanner stdin = new Scanner(System.in);
		
		// Get the necessary user information...
		System.out.println("Enter the input file.");
		String infilename = stdin.next();
		
		System.out.println("Enter the output file.");
		String outfilename = stdin.next();
		
		System.out.println("Enter the key in hex.");
		String key = stdin.next();
		
		Scanner fin = new Scanner(new File(infilename));
		FileWriter fout = new FileWriter(new File(outfilename));
		
		// Set up the keys.
		int[] mykey = getKey(key);
		DES code = new DES(mykey);
*/

		DES code = new DES(key);
		code.setKeys();
		
/*
		// Read in each block and process...
		while (fin.hasNext()) {
			String myblock = fin.next();
			
			// So we don't run into any errors!
			if (myblock == null) break;
			
			code.setBlock(myblock);
			code.encrypt();
			code.print(fout);			
		}
*/

		// loop through every line in this block
		for( int i=0; i<plaintext.length; i++ ){

			// DECLARE VARIABLES
			String plaintextBitString = "";
			String ciphertextBitString = "";

			output[i] = "";

			System.out.println( "INFO: currently encrypting: (plaintext:  " +plaintext[i]+ ")" );

			// is this a valid line?
			if( plaintext[i].length() != 10 ){
				// this is not a valid line because it does not have 10 characters
				System.out.println( "ERROR: Invalid plaintext input!" );
				return output;
			}

			//code.setBlock( plaintext[i] );
			//code.encrypt();

			for( int j=0; j<plaintext[i].length(); j++ ){

				plaintextBitString += radix2BitString( plaintext[i].charAt(j) );

			}

			// pad the (10 characters) * (6 bits/character) = 60-bit bitstring with
			// 4x 0s, so it becomes a 64-bit string
			plaintextBitString += "0000";

			code.setBlock( plaintextBitString );
			code.encrypt();
			System.out.print( "INFO: finished encrytion!" );

			for( int j=0; j<code.block.length; j+=6 ){

				ciphertextBitString += code.block[j];
				ciphertextBitString += code.block[j+1];
				ciphertextBitString += code.block[j+2];
				ciphertextBitString += code.block[j+3];

				// 64 mod 6 = 4, so the last (6-4=) 2 digits of our 11th Radix-64
				// characters should be padding
				if( j+5 < code.block.length ){
					// we are not at the end of the string
					ciphertextBitString += code.block[j+4];
					ciphertextBitString += code.block[j+5];
				} else {
					// these next 2 bits are our special case: they are the last 2
					// bits of the entire 66-bit ciphertextBitString. We pad this
					// bitstring so that it is divisble by 6, in order to make an
					// 11-character Radix-64 string.
					ciphertextBitString += "00";
				}

				//output[i] += bitString2Radix( ciphertextBitString );
			}

			output[i] = bitString2Radix( ciphertextBitString );
			System.out.println( "   (ciphertext: " +output[i]+ ")\n" );

		}

	
/*
		fout.close();
		fin.close();
*/

		return output;
	}

	// takes a radix character and outputs a String representing the 6-bit value
	public static String radix2BitString( char radixChar ){

		// DECLARE VARIABLES
		String output = "";
		int value;

		value = (((int)radixChar)-'A');

		// handle special cases where ASCII differs from Radix-64 encoding
		if( value > 31 ){
			// fixes lower-case letters
			value  -= 6;
		} else if( value == -22 ){
			//fixes (+)
			value = 62;
		} else if( value == -18 ){
			// fixes (/)
			value = 63;
		}

		output = Integer.toBinaryString(value);

		while( output.length() < 6 ){
			output = "0" + output;
		}

		return output;

	}

	// takes a String representing a 64-bit bitstring and returs the
	// cooresponding Radix-64 character
	public static String bitString2Radix( String bitstring ){

		// DECLARE VARIABLES
		String output = "";

		// is the given bitstring a valid Radix-64 string?
		if( (bitstring.length() % 6) != 0 ){
			// the given bitstring is not divisible by 6, so it is invalid!
			System.out.println( "ERROR: bitstring is not divisble by 6!" );
			return "";
		}

		// loop through each 6-bit sub-bitstring of the entire bitstring
		for( int i=0; i<bitstring.length(); i+=6 ){

			// DECLARE VARIABLES
			String sixbits; // holds this iteration's 6-bit sub-bitstring
			int value;

			// get this iteration's 6-bit sub-bitstring
			sixbits = bitstring.substring( i, i+6 );

			// determine the numerical value of the 6-bit bitstring
			value = Integer.parseInt( sixbits, 2 );

			// add offset so that 0 (==A in Radix-64) becomes 65 (==A in ASCII)
			value += 'A';

			// handle special cases where ASCII differs from Radix-64 encoding
			if( value == 127 ){
				//fixes (+)
				value = 43;
			} else if( value == 128 ){
				// fixes (/)
				value = 47;
			} else if( value > 116 ){
				// fixes numbers
				value -= 69;
			} else if( value > 90 ){
				// fixes lower-case letters
				value  += 6;
			}

			// append the Radix-64 character for this 6-bit sub-bitstring to result
			output += (char)(value);

		}

		return output;

	}

	public static void main( String[] args ) throws Exception {

		// TODO remove test
		String hex = "0123456789ABCDEF";
		int[] key = getKey( hex );

		System.out.print( "INFO: Got key: " );
		// print this iteration's key for the user
		for( int k=0; k<key.length; k++ ){
			System.out.print( key[k] );
		}
		System.out.println();

		System.exit( 1 );

		// DECLARE VARIABLES
		String[] ciphertext;

		ciphertext = generate();

		/***********************
		* BRUTE FORCE THE KEY! *
		***********************/

		// attempt to find the key for the plaintext & ciphertext
		// key = 0
		crack( "ABLLOT+/YZ", ciphertext[2] );

		// we will manually add these values once Arup gives us the plaintext
		// block!
		//crack();

	}

	// testing function that generates & returns an array of ciphertext strings
	// for a fixed plaintext & fixed key.
	public static String[] generate() throws Exception {

	// * This block of code was used to initially encrypt a series of test
	// * strings, which will be used below to test our cracking brute-force
	// * attempts.

		// DECLARE VARIABLES
		String[] ciphertext;

		String[] plaintext = { "abcdefgxyz", "chaelinhec", "ABLLOT+/YZ" };
/*
		int[] staticKey = {
		 0, 0, 0, 0, 0, 0, 0, 0,
		 1, 1, 1, 1, 1, 1, 1, 1,
		 1, 1, 1, 1, 1, 1, 1, 1,
		 1, 1, 1, 1, 1, 1, 1, 1,
		 1, 1, 1, 1, 1, 1, 1, 1,
		 1, 1, 1, 1, 1, 1, 1, 1,
		 1, 1, 1, 1, 1, 1, 1, 1,
		 1, 1, 1, 1, 1, 1, 1, 1
		};
*/
///*
		int[] staticKey = {
		 0, 0, 0, 0, 0, 0, 0, 0,
		 0, 0, 0, 0, 0, 0, 0, 0,
		 0, 0, 0, 0, 0, 0, 0, 0,
		 0, 0, 0, 0, 0, 0, 0, 0,
		 0, 0, 0, 0, 0, 0, 0, 0,
		 0, 0, 0, 0, 0, 0, 0, 0,
		 0, 0, 0, 0, 0, 0, 0, 0,
		 0, 0, 0, 0, 0, 0, 0, 0
		};
//*/
		if( keyIsInvalid( staticKey ) ){
			System.out.println( "ERROR: Invalid Key!" );
			System.exit(1);
		}

		ciphertext = encryptBlock( plaintext, staticKey );

		for( int i=0; i<ciphertext.length; i++ ){
			System.out.println( ciphertext[i] );
		}

		return ciphertext;

	}

	// brute forces the key from a given plaintext & given ciphertext
	public static void crack(String plaintext, String ciphertext) throws Exception {

		// DECLARE VARIABLES
		int[] solvedKey;

		// attempt to determinte the key by brute force!
		solvedKey = bruteForce( plaintext, ciphertext );

		// print the cracked key
		System.out.println( "Key found!" );
		for( int i=0; i<solvedKey.length; i++ ){
			System.out.print( solvedKey[i] );
		}
		System.out.println();
	}

	// takes a matching plaintext and ciphertext pair and attempts to crack the
	// key using brute-force
	public static int[] bruteForce( String plaintext, String ciphertext ) throws Exception{
		// TODO: figure out why the key is not being found!

		// DECLARE VARIABLES
		String[] result;
		String[] plaintextArg = {plaintext};
		int[] dynamicKey = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] oldKey = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };

		// loop through every possible key
		do{

			// check to see if this iteration of the key is valid or not
			if( keyIsInvalid( dynamicKey ) ){
				// this key is not valid as per the requirements; skip it.
				debug( 3, "Key " +key2Hex(dynamicKey) + " is Invalid; skipping" );
				oldKey = (int[])dynamicKey.clone();
				dynamicKey = iterateKey( dynamicKey );
				continue;
			}

		// TODO remove debug
		System.out.print( "INFO: Old key: " );
		System.out.println( key2Hex(oldKey) );
		System.out.print( "INFO: New key: " );
		System.out.println( key2Hex(dynamicKey) );

/*
			// check to see if this iteration of the key only changed a parity bit
			if( isIterateParityChange(oldKey, dynamicKey) ){
				// this key is redundant (parity bits are ignored by DES); skip it
				System.out.println( "INFO: Skipping key!" );

				oldKey = (int[])dynamicKey.clone();
				dynamicKey = iterateKey( dynamicKey );
				continue;
			}
*/
	
			System.out.print( "INFO: Attempting key: " );
			System.out.println( key2Hex(dynamicKey) );

			// encrypt the plaintext with this iteration's key
			result = encryptBlock( plaintextArg, dynamicKey );

			// does the encrypted plaintext with this iteration's key match our
			// known iphertext?
			if( result[0].equals(ciphertext) ){
				// the ciphertext for this plaintext + key combination matches our
				// known ciphertext, which means we found the key!
				System.out.println( "INFO: We found the key!!" );
				return dynamicKey;
			}
			
			oldKey = (int[])dynamicKey.clone();
			dynamicKey = iterateKey( dynamicKey );

		// TODO remove debug
		System.out.print( "INFO3: Old key: " );
		System.out.println( key2Hex(oldKey) );
		System.out.print( "INFO3: New key: " );
		System.out.println( key2Hex(dynamicKey) );

		} while( moreKeysExist(dynamicKey) );

		// if we made it this far, we did not find a key!
		System.out.println( "ERROR: Brute froce complete, but key not found! Please verify the accuracy of your plaintext<-->ciphertext pair!" );
		int[] keyFail = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		return keyFail;

	}

	// returns true if there are still keys to be tried
	// returns false if all perumations of keys have been extingusihed
	public static boolean moreKeysExist( int[] key ){

		// our key permutations start with a 64-bit bitstring of all 0s, then we
		// add 1 to the bitstring until all of the bits are 1. Therefore, this
		// function should return false if and only if all of the bits in key == 1

		// iterate through all of the bits in the key
		for( int i=0; i<key.length; i++ ){
			// is this bit a 0?
			if( key[i] == 0 ){
				// a zero exists in the key, so more keys *do* exist
				return true;
			}
		}

		// if we made it this far, no 0s exist in the key.
		// we have extinguished all possible permutations of the key
		// no more keys exist!
		return false;

	}

	// iterates the int[] key (pseudo) bitstring by adding 1 to it.
	public static int[] iterateKey( int[] key ){

		int i;

		// loop through every bit in the key, starting from the least significant
		// bit
		for( i=key.length-1; i>=0; i=i-1 ){

			// is this bit a parity bit?
			if( i%8 == 0 ){
				// this bit is a parity bit; skip it
				debug( 2, "iterateKey() skipped a redundant key (parity)" );
				continue;
			}

			// set the first possible bit to 1, then exit the loop
			if( key[i] == 0 ){
				key[i] = 1;
				break;
			}

		}

		// now, reset all of the lesser significant bits than the one we just
		// flipped to be 0
		while( i<key.length-1 ){
			key[++i] = 0;
		}

		return key;
	}

	// returns true if the key does not match the search-space restriction
	// requirements defined by this HW assignment
	// returns false otherwise
	public static boolean keyIsInvalid( int[] key ){

		// check that the key length is correct
		if( key.length != 64 ){
			// the key length is incorrect! error & exit.
			System.out.println( "ERROR: Invalid key length!" );
			System.exit( 1 );
		}

		for( int i=63; i>=0; i-- ){

			if(
			    ( i<=(64- 1) && i>=(64- 7) ) // bit is between  1-7
			 || ( i<=(64- 9) && i>=(64-15) ) // bit is between  9-15
			 || ( i<=(64-17) && i>=(64-23) ) // bit is between 17-23
			){
				// any of these i'th bits must match the (i-32)'th bit
				if( key[i] != key[i-32] ){
					// TODO remove debug
					//System.out.println( "\t because key["+i+"] ("+key[i]+") != key["+(i-32)+ "] ("+key[i-32]+")" );
					return true;
				}
			}

		}

		// if we made it this far, the key is valid
		return false;

	}

	// returns true if the only difference between the 2 supplied keys is a
	// change to parity bits. returns false otherwise.
	public static boolean isIterateParityChange( int[] oldKey, int[] newKey ){

		// check to see if the keys are the same length
		if( oldKey.length != newKey.length ){
			// the given keys are not the same length, so something is wrong!
			// error & exit
			System.out.println( "ERROR: Keys supplied are different length!" );
			System.exit(1);
		}

		// loop through all bits of the given keys
		for( int i=0; i<oldKey.length; i++ ){

			// is this bit a parity bit?
			if( (i%8) == 0 ){
				// ever eigth bit is a parity bit, so this is a parity bit
				continue;
			}

			// if we made it this far, the bits we're comparing are not parity bits

			// do these non-parity bits differ?
			if( oldKey[i] != newKey[i] ){
				// these non-parity bits differ, so this iteration is *not* a parity
				// chagnge; return false
				return false;
			}

		}

		// if we made it this far, the keys are either the same or a parity change
		return true;

	}

	// takes a int[] key and returns a more human-friendly hex representation
	public static String key2Hex( int [] key ){

		// DECLARE VARIABLES
		int value;
		String hex = "";

		for( int i=0; i<key.length; i=i+4 ){

			String fourbits = "";

			for( int j=i; j<i+4; j++ ){

				if( key[j] == 0 ){
					fourbits += '0';
				} else if( key[j] == 1 ){
					fourbits += '1';
				} else {
					System.out.println( "ERROR: unexpected value in key string (needs to be either a 0 or 1)!" );
					fourbits += '?';
				}

			}

			// pad to get a byte
			fourbits = "0000" + fourbits;

			// determine the numerical value of the 4-bit bitstring
			value = Integer.parseInt( fourbits, 2 );

			switch( value ){
				case 0: hex += '0'; break;
				case 1: hex += '1'; break;
				case 2: hex += '2'; break;
				case 3: hex += '3'; break;
				case 4: hex += '4'; break;
				case 5: hex += '5'; break;
				case 6: hex += '6'; break;
				case 7: hex += '7'; break;
				case 8: hex += '8'; break;
				case 9: hex += '9'; break;
				case 10: hex += 'A'; break;
				case 11: hex += 'B'; break;
				case 12: hex += 'C'; break;
				case 13: hex += 'D'; break;
				case 14: hex += 'E'; break;
				case 15: hex += 'F'; break;
				default: hex += '?';

			}

		}

		return hex;

	} // end key2Hex()

	// prints debug messages when DEBUG is set at least to the supplied level
	public static void debug( int level, String msg ){

		// is the DEBUG boolean true?
		if( DEBUG >= level ){
			System.out.println( "DEBUG: " + msg );
		}

	}

}
