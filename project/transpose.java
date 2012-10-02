import java.util.BitSet;

public class transpose {
	public static void main( String[] args ){

		// DECLARE VARIABLES
		BitSet plaintextBits = new BitSet(48);
		BitSet keyBits       = new BitSet(48);

		// Key      = FOOTBALL
		//byte[] key       = {};
		// Plantext = ABCDEFGH
		byte[] plaintext = {20,(byte)227,(byte)147,4,2,(byte)203};

		//System.out.println( plaintext[2][0] );
		plaintextBits = byteArr2BitSet( plaintext );
		System.out.println("=====================");
		printBitSetAsRadix( plaintextBits );
/*
		System.out.println( plaintextBits );
		for( int i=0; i<48; i++ ){
			System.out.println( plaintextBits.get( i ) );
		}
*/
	}

	// converts a 2D byte array into a java.util.BitSet object
	public static BitSet byteArr2BitSet( byte[] byteArr ){

		// DECLARE VARIABLES
		BitSet bitSet = new BitSet( 8 * byteArr.length );

		int counter = 0;
		// loop through every byte of the given byte array
		for( int i=0; i<byteArr.length; i++ ){

			System.out.println( byteArr[i] );

			// loop through every bit of this byte
			for( int j=0; j<8; j++ ){

				// DECLARE VARIABLES
				boolean bit;

				//System.out.print( "bit:|"+(byteArr[i]>>(7-j) & 0x0001)+"| " );

				// is this bit a 0 or a 1?
				if( (byteArr[i]>>(7-j) & 0x0001) == 1 ){
					// this bit is a 1
					bit = true;
				} else {
					// this bit is a 0
					bit = false;
				}

				// store this iteration's bit to the new BitSet
				bitSet.set(
				 //i*byteArr.length + j,          // increments by 1 at each iter
				 counter++,
				 bit // extract bit value from byte
				);
				//System.out.println("Setting bit #" + counter + "to " + bit );
			}
			//System.out.println( "current BitSet size:|" +bitSet.size()+ "|" );
			System.out.println();
		}

		return bitSet;

	}

	// prints a given BitSet as radix characters
	public static void printBitSetAsRadix( BitSet bitset ){

		// is this a valid Radix bitstring?
		if( bitset.length() % 6 != 0 ){
			// the given BitSet's length is not divisible by 6, so it's not a
			// valid Radix bitstring; exit.
			System.out.println( "ERROR: BitSet not valid Radix bitstring" );
			return;
		}

		// TODO: remove hard-coded 8
		for( int i=0; i<8; i++ ){

			byte sum = 0;

			for( int j=0; j<6; j++ ){

				//System.out.println( "\n\tGetting bit #" +(6*i+j)+ "");
				boolean bit = bitset.get( 6*i + j );

				if( bit ){
					sum += java.lang.Math.pow( 2, 5-j );
				}

/*
				if( bit ){
					System.out.print(1);
				} else {
					System.out.print(0);
				}
*/

			}
			System.out.print( (char) (sum+'A') );
		}
		System.out.println();
	}

}
