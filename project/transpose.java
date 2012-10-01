import java.util.BitSet;

public class transpose {
	public static void main( String[] args ){

		// DECLARE VARIABLES
		BitSet plaintextBits = new BitSet(48);
		BitSet keyBits       = new BitSet(48);

		// Plantext = ABCDEFGH
		// Key      = FOOTBALL
		//byte[] key       =
		byte[][] plaintext = {
		 {0,0,0,1,0,1}, // F
		 {0,0,1,1,1,0}, // O
		 {0,0,1,1,1,0}, // O
		 {0,1,0,0,1,1}, // T
		 {0,0,0,0,0,1}, // B
		 {0,0,0,0,0,0}, // A
		 {0,0,1,0,1,1}, // L
		 {0,0,1,0,1,1}, // L
		};

		//System.out.println( plaintext[2][0] );
		plaintextBits = byteArray2BitSet( plaintext );
	}

	// converts a 2D byte array into a java.util.BitSet object
	public static BitSet byteArray2BitSet( byte[][] byteArray ){

		// DECLARE VARIABLES
		//BitSet bitSet = new BitSet( byteArray.length * byteArray[0].length );
		BitSet bitSet = new BitSet( 999 );
		System.out.println( bitSet.size() );

		// loop through the given 2D byte array
		for( int i=0; i < byteArray.length; i++ ){
			for( int j=0; j < byteArray[0].length; j++ ){
				// TODO: fix error by figuring out why this isn't looping exactly 48 times(?)
				//bitSet.set( (i*byteArray.length + j), byteArray[i][j] );
				//System.out.println( i*(byteArray.length-1) + j );
				System.out.println( j );
			}
			System.out.println();
		}

		return bitSet;

	}
}
