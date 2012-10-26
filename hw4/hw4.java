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

public class hw4 {
	
	// GLOBAL SETTINGS
	public final static char DEBUG = 3;
	public final static BigInteger one = new BigInteger("1");
	
	public static void main(String[] args) {

		// DECLARE VARIABLES

		BigInteger p = new BigInteger("0");
		BigInteger q = new BigInteger("0");
		BigInteger e = new BigInteger("3");
		BigInteger n, phi, d;

		boolean encrypt = false;         // true if encrypting; false if decryting
		String infile = "";           // file to get plaintext or ciphertext
		String outfile = "";          // file to get plaintext or ciphertext

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
		System.out.println("The decryption exponent is " + d);
		
		// Try out a test message.
		System.out.println("Enter your message, in between 1 and "+(n.subtract(one)));

		// TODO: remove this hard-coded line (888) && instead, read in from
		//       `infile` instead. After reading the contents of `infile`, we
		//       need to convert the ASCII text (which we can limit to a domain of
		//       only all-lower-case-characters to simplify this [but then we need
		//       to error if `infile` contains any non-lower-case characters]) to
		//       integers so that we can store it to `BigInteger m`.
		BigInteger m = new BigInteger("888");
		

		// TODO: use `boolean encrypt` to determine if we should be encrypting or
		//       decrypting here.

		// Calculate the corresponding ciphertext.
		BigInteger c = m.modPow(e, n);
		System.out.println("Ciphertext is "+c);
		
		// Recover the plaintext as the message recipient would.
		BigInteger mback = c.modPow(d, n);
		System.out.println("orig m is "+mback);

		// TODO: convert the resulting numerical output back to ASCII.
		// ...

		// TODO: output the result (converted to ASCII) to `outfile`
		// ...

		// TODO: if all went well, print a happy 'success' message.
		
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
		 "hw3 is a protocol for encrypting and decrypting a message using RSA\n\n"
		+"Usage: hw3 <e|d> <p> <q> <e> <input file> <output file>\n"
		+"where:\n"
		+"\t<e|d>: 'e' means you want to 'encrypt' and 'd' means 'decrypt'\n"
		+"\t<p>, <q>, <e>: are the inputs defined by the RSA standard\n"
		+"\t<input file>: is the input file containing the plaintext (if "
		+"encrypting) or the ciphertext (if decrypting)\n"
		+"\t<output file>: is the output file containing the plaintext (if "
		+"decrypting) or the ciphertext (if encrypting)\n"
		);
	}

	// 
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

}
