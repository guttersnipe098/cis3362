/*******************************************************************************
* File:    BitSetHelpers.java
* Purpose: Helper functions for the crypto challenge project to assist in the
*          manipulation of BitSet objects
* Authors: Michael Alfield <maltfield@knights.ucf.edu>
* Course: CIS 3362 <http://www.cs.ucf.edu/courses/cis3362/fall2012/>
* Created: 2012-10-01
* Updated: 2012-10-01
*******************************************************************************/

import java.util.BitSet;

public class BitSetHelpers {

	public static void main( String[] args ){

		// DECLARE VARIABLES
		BitSet plaintextBitSet = new BitSet(48);
		BitSet keyBitSet       = new BitSet(48);

		// Key      = "FOOTBALL"
		byte[] key       = {20,(byte)227,(byte)147,4,2,(byte)203};
		// Plantext = "ABCDEFGH"
		byte[] plaintext = {20,(byte)227,(byte)147,4,2,(byte)203};

		// convert the key (byte array) to a BitSet object
		keyBitSet = byteArr2BitSet( key );

		// print the new key BitSet as RADIX-64 characters
		System.out.println( bitSet2Radix( keyBitSet ) );

		// convert the key BitSet object back to a byte array
		byte[] key2 = bitSet2ByteArr( keyBitSet );
	}

	/****************************************************************************
	* Name:    byteArr2BitSet
	* Purpose: converts a byte array into a java.util.BitSet object
	* Input:   byteArr - the byte array to be turned into the BitSet object
	* Output:  a BitSet object equalivalent to the supplied byte array
	****************************************************************************/
	public static BitSet byteArr2BitSet( byte[] byteArr ){

		// DECLARE VARIABLES
		BitSet bitSet = new BitSet( 8 * byteArr.length );

		// loop through every byte of the given byte array
		for( int i=0; i<byteArr.length; i++ ){

			// loop through every bit of this byte
			for( int j=0; j<8; j++ ){

				// DECLARE VARIABLES
				boolean bit;

				// is this bit a 0 or a 1?
				if( (byteArr[i]>>(7-j) & 0x0001) == 1 ){
					// this bit is a 1
					bit = true;
				} else {
					// this bit is a 0
					bit = false;
				}

				// store this iteration's bit to the new BitSet
				bitSet.set( i*8+j, bit );
			}
		}

		return bitSet;

	} // end byteArr2BitSet()

	/****************************************************************************
	* Name:    bitSet2ByteArr
	* Purpose: converts a BitSet object into a byte array
	* Input:   bitset - the BitSet object to be converted into the byte array
	* Output:  returns a byte array equivalent to the given BitSet object
	****************************************************************************/
	public static byte[] bitSet2ByteArr( BitSet bitset ){

		// DECLARE VARIABLES
		byte[] byteArr = {10};

		return byteArr;

	} // end bitSet2ByteArr()

	// TODO: fix to print '/' and '+' properly
	/****************************************************************************
	* Name:    bitSet2Radix
	* Purpose: converts a BitSet object to a String in Radix-64
	* Input:   the BitSet object to be converted to Radix-64
	* Output:  returns a String of characters represented by the given BitSet object
	****************************************************************************/
	public static String bitSet2Radix( BitSet bitset ){

		// DECLARE VARIABLES
		String result = "";

		// is this a valid Radix bitstring?
		if( bitset.length() % 6 != 0 ){
			// the given BitSet's length is not divisible by 6, so it's not a
			// valid Radix bitstring; exit.
			System.out.println( "ERROR: BitSet not valid Radix bitstring" );
			return "";
		}

		// iterate through each 6-bit character in the given BitSet
		for( int i=0; i<(bitset.length()/6); i++ ){

			byte sum = 0;

			// iterate through each bit of this character
			for( int j=0; j<6; j++ ){

				// strip this bit from the byte from left-to-right (MSb-to-LSb)
				boolean bit = bitset.get( 6*i + j );

				// if this bit is true, add it to the sum with its binary value
				if( bit ){
					sum += java.lang.Math.pow( 2, 5-j );
				}

			}
			// append the calculated sum as a character to the result String
			result += (char)(sum+'A');
		}

		// return the resulting String
		return result;

	} // end bitSet2Radix()

}
