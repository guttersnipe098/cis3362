// Arup Guha
// Written 7/29/06, edited on 8/2/06 for BHCSI Cryptography Course
// The purpose of this program is to illustrate the mechanics of RSA. 
// This is an edited version of RSABigInt.java. It adds a function of
// taking a text message of all uppercase characters and converting
// this into a big integer appropriate for encryption. In decryption,
// the resulting big integer is converted back to text. If the message
// is shorter than the blocksize, then X's are padded to the of it.
// Since this is just a demonstration program, only one message block
// is allowed to be enciphered when running the program.
import java.util.*;
import java.math.BigInteger;

public class RSA2BigInt {
	
	// Useful constants.
	public final static BigInteger zero = new BigInteger("0");
	public final static BigInteger one = new BigInteger("01");
	public final static BigInteger twentysix = new BigInteger("26");

	public static void main(String[] args) {
		
		BigInteger p, q, n, phi, e= new BigInteger("3"), d;
		
		Scanner stdin = new Scanner(System.in);
		
		// Get a start spot to pick a prime from the user.
		System.out.println("Enter the approximate value of p you want.");
		String ans = stdin.next();
		p = getNextPrime(ans);
		System.out.println("Your prime is "+p+".");
		
		// Get a start spot to pick a prime from the user.
		System.out.println("Enter the approximate value of q you want.");
		ans = stdin.next();
		q = getNextPrime(ans);
		System.out.println("Your prime is "+q+".");
		
		
		// Calculate the public key n.
		n = p.multiply(q);
		
		// Calculate phi of n, which is secret information.
		phi = (p.subtract(one)).multiply(q.subtract(one));
		
		// Try random values of e until one works.
		boolean done = false;
		while (!done) {
			
			System.out.println("Enter e.");
			e = new BigInteger(stdin.next());
			
			// e must be relatively prime to the phi of n. 
			// Checl that here, otherwise ask for another e.
			// When e is set, it will the other public key.
			if ((e.gcd(phi)).equals(one))
				done = true;
			else
				System.out.println("Your e is not relatively prime with phi.");	
		}
		
		// Calculate the secret key d. This can be done only with
		// knowledge of phi of n.
		d = e.modInverse(phi);
		
		// Try out a test message.
		int blocksize = getBlockSize(n);
		System.out.println("Enter your message, all in uppercase letters  with " + blocksize + " letters or less.");
		String message = stdin.next();
		
		// Convert the text to a BigInteger.
		BigInteger m = convert(message, blocksize);
		System.out.println("Numerical message is "+m);
		
		// Calculate the corresponding ciphertext.
		BigInteger c = m.modPow(e, n);
		System.out.println("Ciphertext is "+c);
		
		// Recover the plaintext as the message recipient would.
		BigInteger mback = c.modPow(d, n);
		System.out.println("original numerical message is "+mback);
		
		// Recover the original text message.
		String origmessage = convertBack(mback, blocksize);
		System.out.println("original text message is "+origmessage);
		
	}
	
	// Incrementally tries each BigInteger starting at the value passed
	// in as a parameter until one of them is tests as being prime.
	public static BigInteger getNextPrime(String ans) {
		
		BigInteger test = new BigInteger(ans);
		while (!test.isProbablePrime(99))
			test = test.add(one);
		return test;		
	}
	
	
	// The largest block size for this implementation is the greatest
	// power of 26 that is smaller than n.
	public static int getBlockSize(BigInteger n) {
		
		BigInteger ans = twentysix;
		int counter = 0;
		
		while (ans.compareTo(n) < 0) {
			ans = ans.multiply(twentysix);
			counter++;
		}
		
		return counter;
	}
	
	// Converts a message, given the blocksize, into a BigInteger.
	public static BigInteger convert(String message, int blocksize) {
		
		// Figure out the number of X's to pad. Only works if the 
		// message is short enough.
		int padding = blocksize - message.length();
		
		// Pad the appropriate number of X's.
		for (int i=0; i<padding; i++)
			message = message + "X";
			
		// The plaintext coversion starts at 0.
		BigInteger ans = zero;
		
		// Essentially use Horner's method.
		for (int i=0; i<blocksize; i++) {
			
			// This shifts the old answer over by one character place (26).
			ans = ans.multiply(twentysix);
			
			// Determine the numerical value (0-25) of the current character.
			int c = (char)(message.charAt(i) - 'A');
			
			// This is just a pain. All it does is create a string with the value
			// of the current character. It's okay for it to be padded with a 0.
			char[] tmp = new char[2];
			tmp[1] = (char)('0'+(c%10));
			tmp[0] = (char)(('0')+(c/10));
			String tmp2 = new String(tmp);
			
			// Add the current character value to ans.
			ans = ans.add(new BigInteger(tmp2));
		}
		
		// Return the answer.
		return ans;	
		
	}
	
	// Converts a BigInteger, given the blocksize into uppercase characters,
	// corresponding to the method used in convert.
	public static String convertBack(BigInteger m, int blocksize) {
		
		// Start with a blank message.
		String message = "";
					
		BigInteger ans = zero;
		
		// You will extract blocksize number of characters.
		for (int i=0; i<blocksize; i++) {
			
			// Calculate the integer value of the character at the end of what
			// hasn't been converted back yet.
			BigInteger leftover = m.mod(twentysix);
			
			// Store this as an int.
			int leftint = leftover.intValue();
			
			// Convert this into into an alphabetic character (A-Z) and store into a String.
			char[] tmp = new char[1];
			tmp[0] = (char)('A'+leftint);
			
			// Finally add this character to the end of the message we've recovered so far.
			message = (new String(tmp)) + message;
			
			// Divide m by 26 to get rid of the last character, since we've added it to
			// the String already.
			m = m.divide(twentysix);
		}
		
		// Return our final message.
		return message;
		
	}
	
}
