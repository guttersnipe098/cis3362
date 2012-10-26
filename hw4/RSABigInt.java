// Arup Guha
// Written 7/29/06, edited on 8/2/06 for BHCSI Cryptography Course
// The purpose of this program is to illustrate the mechanics of RSA. 
import java.util.*;
import java.math.BigInteger;

public class RSABigInt {
	
	public final static BigInteger one = new BigInteger("1");
	
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
			// Check that here, otherwise ask for another e.
			// When e is set, it will the other public key.
			if ((e.gcd(phi)).equals(one))
				done = true;
			else
				System.out.println("Your e is not relatively prime with phi.");	
		}
		
		// Calculate the secret key d. This can be done only with
		// knowledge of phi of n.
		d = e.modInverse(phi);
		System.out.println("The decryption exponent is " + d);
		
		// Try out a test message.
		System.out.println("Enter your message, in between 1 and "+(n.subtract(one)));
		BigInteger m = new BigInteger(stdin.next());
		
		// Calculate the corresponding ciphertext.
		BigInteger c = m.modPow(e, n);
		System.out.println("Ciphertext is "+c);
		
		// Recover the plaintext as the message recipient would.
		BigInteger mback = c.modPow(d, n);
		System.out.println("orig m is "+mback);
		
	}
	
	// Incrementally tries each BigInteger starting at the value passed
	// in as a parameter until one of them is tests as being prime.
	public static BigInteger getNextPrime(String ans) {
		
		BigInteger test = new BigInteger(ans);
		while (!test.isProbablePrime(99))
			test = test.add(one);
		return test;		
	}
	
}
