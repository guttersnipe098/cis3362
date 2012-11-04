/*******************************************************************************
* File:    hw4.java
* Purpose: Implements a protocol for (en|de)crypting a given message using RSA
* Authors: Michael Alfield <maltfield@knights.ucf.edu>
*          Joe Castellanos
* Course:  CIS 3362 <http://www.cs.ucf.edu/courses/cis3362/fall2012/>
* Created: 2012-10-26
* Updated: 2012-10-26
*******************************************************************************/

// Arup Guha
// Written 7/29/06, edited on 8/2/06 for BHCSI Cryptography Course
// The purpose of this program is to illustrate the mechanics of RSA. 
import java.util.*;
import java.util.regex.*;
import java.math.BigInteger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class hw4 {
	
	// GLOBAL SETTINGS
	public final static char DEBUG = 0;
	public final static BigInteger one = new BigInteger("1");
	public final static BigInteger oneHundred = new BigInteger( "100" );
	
	public static void main(String[] args) {

		// DECLARE VARIABLES

		BigInteger p = new BigInteger("0");
		BigInteger q = new BigInteger("0");
		BigInteger e = new BigInteger("3");
		BigInteger n, phi, d;

		boolean encrypt = false;  // true if encrypting; false if decryting
		String infile = "";       // filename to input the plain or ciphertext
		String input = "";        // holds the contents of infile
		String outfile = "";      // filename to output the plain or ciphertext
		String output = "";       // holds the contents of outfile

		String ciphertext;       // holds the numerical value of the ciphertext
		int blocksize;           // number of characters to be encrypted per block

		String pattern;          // holds a regex pattern
		Pattern compiledPattern; // holds a compiled regex pattern
		Matcher matcher;         // holds a regex matcher

		/*******************
		* HANDLE ARGUMENTS *
		*******************/

		// did the user enter the correct number of arguments?
		if( args.length != 6 ){
			// the user did *not* enter the correct number of arguments!
			System.out.println(
			 "ERROR: Incorrect argument count. See usage below.\n"
			);
			printUsage();
			System.exit(1);
		}

		// ARGUMENT #1

		// did the user tell us if we should encrypt or decrypt?
		pattern = "e|E|d|D";
		compiledPattern = Pattern.compile(pattern);
		matcher = compiledPattern.matcher( args[0] );
		if( matcher.find() ){
			// argument looks valid

			// now, do we want to encrypt or decrypt?
			if( args[0].charAt(0) == 'e' || args[0].charAt(0) == 'E' ){
				// we're encrypting
				encrypt = true;
			} else {
				// we're decrypting
				encrypt = false;
			}

		} else {
			// the user did not enter the first argument correctly!
			System.out.println(
			 "ERROR:\tInvalid entry for first argument (encryption/decryption).\n"
			+"\tPlease enter 'e' if you want to encrypt or 'd' if you want to "
			+"decrypt.\n"
			+"\tSee usage below for more info.\n"
			);
			printUsage();
			System.exit(1);
		}

		// ARGUMENT #2
	
		// did the user enter a sane value for p?
		pattern = "[0-9]+";
		compiledPattern = Pattern.compile(pattern);
		matcher = compiledPattern.matcher( args[1] );
		if( matcher.find() ){
			// argument looks valid

			p = new BigInteger( args[1] );

		} else {
			// the user did not enter the first argument correctly!
			System.out.println(
			 "ERROR:\tInvalid entry for second agrument (p).\n"
			);
			printUsage();
			System.exit(1);
		}

		// ARGUMENT #3

		// did the user enter a sane value for q?
		pattern = "[0-9]+";
		compiledPattern = Pattern.compile(pattern);
		matcher = compiledPattern.matcher( args[2] );
		if( matcher.find() ){
			// argument looks valid

			q = new BigInteger( args[2] );

		} else {
			// the user did not enter the first argument correctly!
			System.out.println(
			 "ERROR:\tInvalid entry for third agrument (q).\n"
			);
			printUsage();
			System.exit(1);
		}

		// ARGUMENT #4

		// did the user enter a sane value for e?
		pattern = "[0-9]+";
		compiledPattern = Pattern.compile(pattern);
		matcher = compiledPattern.matcher( args[3] );
		if( matcher.find() ){
			// argument looks valid

			e = new BigInteger( args[3] );

		} else {
			// the user did not enter the first argument correctly!
			System.out.println(
			 "ERROR:\tInvalid entry for fourth agrument (e).\n"
			);
			printUsage();
			System.exit(1);
		}

		// ARGUMENT #5

		// did the user enter a sane value for the text file?
		pattern = ".+";
		compiledPattern = Pattern.compile(pattern);
		matcher = compiledPattern.matcher( args[4] );
		if( matcher.find() ){
			// argument looks valid

			infile = args[4];

		} else {
			// the user did not enter the first argument correctly!
			System.out.println(
			 "ERROR:\tInvalid entry for fifth agrument (input file).\n"
			);
			printUsage();
			System.exit(1);
		}

		// ARGUMENT #6

		// did the user enter a sane value for the text file?
		pattern = ".+";
		compiledPattern = Pattern.compile(pattern);
		matcher = compiledPattern.matcher( args[5] );
		if( matcher.find() ){
			// argument looks valid

			outfile = args[5];

		} else {
			// the user did not enter the first argument correctly!
			System.out.println(
			 "ERROR:\tInvalid entry for fifth agrument (output file).\n"
			);
			printUsage();
			System.exit(1);
		}

		// print some debug info
		debug( 1, "The user entered the following input:" );
		debug( 1, "\tencrypt:|" +encrypt+ "|" );
		debug( 1, "\tp:|" +p+ "|" );
		debug( 1, "\tq:|" +q+ "|" );
		debug( 1, "\te:|" +e+ "|" );
		debug( 1, "\tinfile:|" +args[4]+ "|" );
		debug( 1, "\toutfile:|" +outfile+ "|" );

		/************
		* MAIN BODY *
		************/

		// Calculate the public key n.
		n = p.multiply(q);

		// is n large enough to encrypt 2 letters together?
		if( n.compareTo( new BigInteger("2525") ) < 1 ){
			// n is too small; error & exit

			System.out.println(
			 "ERROR: n is too small. Please enter larger values for p and/or q"
			);
			System.exit( 1 );

		}

		debug( 1, "n:|" +n+ "|" );
		
		// Calculate phi of n, which is secret information.
		phi = (p.subtract(one)).multiply(q.subtract(one));
		
		// e must be relatively prime to the phi of n. 
		// Check that here, otherwise ask for another e.
		// When e is set, it will the other public key. (sic)
		if( !(e.gcd(phi)).equals(one) ){
			System.out.println(
			 "ERROR: Your e is not relatively prime with phi. Please try again."
			);
			System.exit(1);
		}
		
		// Calculate the secret key d. This can be done only with
		// knowledge of phi of n.
		d = e.modInverse(phi);

		debug( 1, "d:|" +d+ "|" );
		
		/*****************************
		* Get contents of input file *
		*****************************/

		// try to open the input file
		try( BufferedReader br = new BufferedReader( new FileReader(infile) ) ){
			// we successfully opened the input file

			// loop through every character in the input file
			int i;
			while( (i = br.read()) != -1 ){

				// is this character an EOF marker?
				if( i == 10 || i == 13 ){
					// this character is a newline, so we've reached the EOF
					break;
				}

				// does the input file contain plaintext or ciphertext?
				if( encrypt ){
					// the input file containts plaintext since we're encrypting

					// is this character a lower-case letter?
					if( i < 97 || i > 122 ){
						// this character is *not* a lower-case letter = invalid
						
						// since this is an invalid input, we error & exit
						System.out.println(
					 	"ERROR: Invalid input (" + (char)i + ")! "+
					 	"Only lower-case letters are valid plaintext inputs."
						);
						System.exit( 1 );

					}

				} else {
					// the input file contains ciphertext since we're decrypting

					// is this character a number or a space?
					if( (i!=32 && i<48) || i > 57 ){
						// this character is *not* a number or a space = invalid
						// since this is an invalid input, we error & exit
						System.out.println(
					 	"ERROR: Invalid input (" + (char)i + ")! "+
					 	"Only numbers are valid ciphertext inputs."
						);
						System.exit( 1 );

					}

				}

				// if we made it this far, the character is legal; add it to the
				// input String
				input += (char)i;

			}


		} catch( IOException exception ){
			// there was an error opening the input file

			System.out.println(
			 "\nERROR: Failed to open input file! See stack trace below:\n"
			);
			exception.printStackTrace();
			System.exit( 1 );

		}

		/*********************
		* Encrypt or Decrypt *
		*********************/

		// do we want to encrypt or decrypt the contents of the input file?
		if( encrypt ){
			// we want to encrypt the contents of the input file

			output = encrypt( input, e, n );

		} else {
			// we want to decrypt the contents of the input file

			output = decrypt( input, d, n );

		}

		/******************************
		* Write Result to Output File *
		******************************/

		// try to open the input file
		try( BufferedWriter bw = new BufferedWriter( new FileWriter(outfile) ) ){
			// we successfully opened the output file

			bw.write( output );

		} catch( IOException exception ){
			// there was an error opening the output file

			System.out.println(
			 "\nERROR: Failed to open output file! See stack trace below:\n"
			);
			exception.printStackTrace();
			System.exit( 1 );

		}

		
		
	}
	
	/****************************************************************************
	* Name:    getNextPrime
	* Purpose: Incrementally tries each BigInteger starting at the value passed
	*          in as a parameter until one of them is tests (sic) as being prime.
	* Author:  Arup Guha <dmarino@cs.ucf.edu>
	* Input:   ans - the first potential prime number to try
	* Output:  the first actual prime number
	****************************************************************************/
	public static BigInteger getNextPrime( String ans ){
		
		// TODO: optimize?
		BigInteger test = new BigInteger(ans);
		while (!test.isProbablePrime(99))
			test = test.add(one);
		return test;		

	}

	/****************************************************************************
	* Name:    printUsage
	* Purpose: Prints a message to the user explaining how to use this software
	* Input:   none
	* Output:  none (prints to STDOUT)
	****************************************************************************/
	public static void printUsage(){		
		System.out.println(
		 "hw4 is a protocol for encrypting and decrypting a message using RSA\n\n"
		+"Usage: hw4 <e|d> <p> <q> <e> <input file> <output file>\n"
		+"where:\n"
		+"\t<e|d>: 'e' means you want to 'encrypt' and 'd' means 'decrypt'\n"
		+"\t<p>, <q>, <e>: are the inputs defined by the RSA standard\n"
		+"\t<input file>: is the input file containing the plaintext (if "
		+"encrypting) or the ciphertext (if decrypting)\n"
		+"\t<output file>: is the output file containing the plaintext (if "
		+"decrypting) or the ciphertext (if encrypting)\n"
		);
	}

	/****************************************************************************
	* Name:    debug
	* Purpose: prints debug messages when DEBUG is set at least to the supplied
	*          level
	* Input:   level - a int coefficient for this message. If this level has
	*                  a higher value than the current DEBUG setting, the given
	*                  message will be printed. Otherwise, it will not be printed
	*          msg   - the debug message to be printed to the user
	* Output:  none (prints to STDOUT)
	****************************************************************************/
	public static void debug( int level, String msg ){

		// is the DEBUG value higher than this message's level?
		if( DEBUG >= level ){
			System.out.println( "DEBUG: " + msg );
		}

	}

	/****************************************************************************
	* Name:    ascii2BigInt
	* Purpose: No frills function that converts a String of (assumed) lower-case
	*          ASCII characters into their double-digit, 00-25 ASCII values minus
	*          97 concatentaed together.
	* Input:   input - the string to be converted to an integer
	* Output:  a BigInteger of the 00-25 ASCII values of input concatenated
	*          together
	****************************************************************************/
	public static BigInteger ascii2BigInt( String input ){

		debug( 5, "ascii2BigInt got '" +input+ "'" );

		// DECLARE VARIABLES
		BigInteger output = new BigInteger( "0" );
		BigInteger value;

		// loop through every character of the input string
		for( int i=0; i<input.length(); i++ ){

			// determine the value of this character
			value = new BigInteger( Integer.toString(input.charAt(i) - 'a') );

			// add 2 0s to the right of the value for each new letter
			value = value.multiply( oneHundred.pow( i ) );

			// add this character to the output sum
			output = output.add( value );

		}

		debug( 5, "\tascii2BigInt returned '" +output+ "'" );

		return output;

	}

	/****************************************************************************
	* Name:    bigInt2Ascii
	* Purpose: No frills function that converts a BigInteger generated by
	*          ascii2BigInt back to the original String of (assumed) lower-case
	*          ASCII characters.
	* Input:   input - the BigInteger to be converted back to its String
	* Output:  The original String of the input
	****************************************************************************/
	public static String bigInt2Ascii( BigInteger input ){

		debug( 5, "bigInt2Ascii got '" +input+ "'" );

		// DECLARE VARIABLES
		String inputString;
		String output = "";
		String character;
		int value;

		// convert the BigInteger to a String
		inputString = input.toString();

		// loop through every character of the input string
		for( int i=inputString.length(); i>0; i-=2 ){

			// peel off the 2-digit 0-25 value of this character (as a String)
			if( (i-2) < 0 ){
				character = inputString.substring( i-1, i );
			} else {
				character = inputString.substring( i-2, i );
			}

			// convert this string back to an int
			value = Integer.parseInt(character) + 'a';

			output += (char)value;

		}

		debug( 5, "\tbigInt2Ascii returned '" +output+ "'" );

		return output;

	}

	/****************************************************************************
	* Name:    getBlockSize
	* Purpose: Calculates the (maximum) block size for a given n
	* Input:   n - the n variable for the RSA cipher
	* Output:  the blocksize (the number maximum number of characters that can be
	*          grouped together and en/decrypted by the RSA cipher for n
	****************************************************************************/
	public static int getBlockSize( BigInteger n ){

			// DECLARE VARIABLES
			BigInteger temp;
			int blocksize = 0;

			// the maximum numerical value that we can encrypt using RSA is equal
			// to (n-1). Each character has a max value of 25. 2 chars = 2525. 3
			// chars = 252525, etc. The number of characters that we can encrypt
			// at once is the blocksize

			// determine the blocksize
			temp = new BigInteger( "25" );
			while( n.compareTo(temp) > 0 ){
				temp = new BigInteger( temp.toString().concat("25") );
				blocksize++;
			}

			debug( 5, "\tblocksize:|" +blocksize+ "|" );

			return blocksize;
	}

	/****************************************************************************
	* Name:    encrypt
	* Purpose: Encrypts a given message using RSA
	* Input:   msg - the plaintext String to encrypt
	*          e   - the e variable for the RSA cipher
	*          n   - the n varialbe for the RSA cipher
	* Output:  The ciphertext for the given message, e, and n
	****************************************************************************/
	public static String encrypt( String msg, BigInteger e, BigInteger n ){

			debug( 1, "encrypting msg:|" +msg+ "|" );

			// DECLARE VARIABLES
			String ciphertext = "";
			int blocksize;

			// get the blocksize so we know how many characters to encrypt at once
			blocksize = getBlockSize( n );

			// peel off a substring of blocksize # chars at a time from msg
			for( int i=0; i<msg.length(); i+=blocksize ){

				// DECLARE VARIABLES
				String block;
				BigInteger blockValue;
				BigInteger encryptedBlockValue;

				// does this block extend beyond the string?
				if( (i+blocksize) < msg.length() ){
					// there are at least as many charcters left in the message as
					// the `blocksize`, grab the next `blocksize` characters

					block = msg.substring( i, i+blocksize );

				} else {
					// there are less than `blocksize` characters left in msg, so
					// we just grab the rest

					block = msg.substring( i );

				}

				debug( 4, "\tblock:|" +block+ "|" );

				// convert this block to a numerical value so we can encrypt it
				blockValue = ascii2BigInt( block );

				debug( 4, "\t\tblockValue:|" +blockValue+ "|" );

				// Calculate the corresponding ciphertext.
				encryptedBlockValue = blockValue.modPow(e, n);

				debug( 4, "\t\tencryptedBlockValue:|" +encryptedBlockValue+ "|" );

				// append this block's ciphertext to the whole msg's ciphertext
				ciphertext += encryptedBlockValue + " ";

			}

		// remove the last (unnecessary) space
		ciphertext = ciphertext.substring( 0, ciphertext.length()-1 );

		debug( 1, "\tciphertext:|" +ciphertext+ "|" );
		return ciphertext;

	}

	/****************************************************************************
	* Name:    decrypt
	* Purpose: Decrypts a given ciphertext using RSA
	* Input:   msg - the ciphertext String to decrypt
	*          d   - the e variable for the RSA cipher
	*          n   - the n varialbe for the RSA cipher
	* Output:  The ciphertext for the given message, d, and n
	****************************************************************************/
	public static String decrypt( String msg, BigInteger d, BigInteger n ){

		debug( 1, "decrypting msg:|" +msg+ "|" );

		// DECLARE VARIABLES
		String plaintext = "";
		int blocksize = 0;
		String block = "";
		BigInteger blockValue;
		BigInteger decryptedBlockValue;
		String decryptedBlock;

		// get the blocksize so we know how many characters to encrypt at once
		blocksize = getBlockSize( n );

		// loop through each character in the ciphertext
		for( int i=0; i<msg.length(); i++ ){

			// DECLARE VARIABLES
			char c;

			c = msg.charAt(i);
			debug( 5, "\tchar:|" +c+ "|" );

			// is this a new character of the current block, or a delimiter for the
			// next block?
			if( c != 32 ){
				// this is a new character, add it to the current block
				block += c;

				// have we reached the end of the ciphertext?
				if( i+1 < msg.length() ){
					// we haven't reached the end of the ciphertext; keep reading
					continue;
				}

			}

			debug( 4, "\tblock:|" +block+ "|" );

			// if we made it this far, we have this entire block; get its numerical
			// value so we can decrypt it
			blockValue = new BigInteger( block );
			debug( 6, "\t\tblockValue:|" +blockValue+ "|" );

			// decrypt this block
			decryptedBlockValue = blockValue.modPow( d, n );
			debug( 4, "\t\tdecryptedBlockValue:|" +decryptedBlockValue+ "|" );

			// convert this decrypted block's value back to ASCII
			decryptedBlock = bigInt2Ascii( decryptedBlockValue );
			debug( 4, "\t\tdecryptedBlock:|" +decryptedBlock+ "|" );

			// append this decrypted block's ASCII text to the plaintext string
			plaintext += decryptedBlock;

			// clear this block for the next one
			block = "";

		}

		debug( 1, "\tplaintext:|" +plaintext+ "|" );
		return plaintext;

	}

}
