package redCipher;

import java.util.BitSet;

public class Logic2 {
	
	
	
	public static void main(String Args[]){
		
		//hard code values to test 
		byte plainText[] = new byte[6];
		plainText[0]=(byte) 255;
		plainText[1]=(byte) 170;
		plainText[2]=(byte) 0;
		plainText[3]=(byte) 0;
		plainText[4]=(byte) 0;
		plainText[5]=(byte) 170;
		
		unorder(plainText);
		
		
	}

	private static byte[] unorder(byte[] plainText) {
		
		int count= 0,i,j;
		boolean array1[][]= new boolean [4][4];
		boolean array2[][]= new boolean [4][4];
		boolean array3[][]= new boolean [4][4];
		
		System.out.print("The byte array before the unorder operation:");
		printByteArrayHex(plainText);
		
		boolean[] bits = byteArr2BitArr(plainText);
		System.out.println("The length of the bit set:"+bits.length);
		printBitSet(bits);
		
		boolean[] shifted = new boolean[8*plainText.length];

		//stores the bits into the arrays
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				array1[i][j]=bits[count];
				count++;
			}
		}
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				array2[i][j]=bits[count];
				count++;
			}
		}
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				array3[i][j]=bits[count];
				count++;
			}
		}
		
		//puts the bits into a BitSet that has been shifted
		count = 0;
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				shifted[count]=array2[i][j];
				count++;
			}
		}
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				shifted[count]=array3[i][j];
				count++;
			}
		}
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				shifted[count]=array1[i][j];
				count++;
			}
		}

		System.out.println("The length of the bit set:"+shifted.length+" It should be:"+count);
		printBitSet(shifted);
		
		//changes the boolean array to a byte array
		
		
		
		return(bitArr2ByteArr(shifted));
		
	}
	
	private static void printBitSet(boolean[] bits) {
		
		for(int i=1;i<bits.length+1;i++){
			if(bits[i-1])
				System.out.print("1");
			else
				System.out.print("0");
			
			//formatting
			if(i%8==0)
				System.out.println();
			else if(i%4==0)
				System.out.print(" ");
		}
		System.out.println();
	}
	
	private static void printByteArrayHex(byte array[]){
		
		int i;
		
		for(i=1;i<array.length+1;i++){
			
			System.out.print((array[i-1]));
			
			if(i%2==0)
				System.out.print(" ");
		}
		
	}

	private static void printBitSet(BitSet set) {
		int i;
		
		for(i=1;i<set.length()+1;i++){
			//prints out the value
			if(set.get(i-1)==true)
				System.out.print("1");
			else
				System.out.print("0");
			
			//formatting
			if(i%8==0)
				System.out.println();
			else if(i%4==0)
				System.out.print(" ");
		}
		System.out.println();
		
	}

	/****************************************************************************
	* Name:    byteArr2BitSet
	* Purpose: converts a byte array into a java.util.BitSet object
	* Input:   byteArr - the byte array to be turned into the BitSet object
	* Output:  a BitSet object equalivalent to the supplied byte array
	****************************************************************************/
	public static boolean[] byteArr2BitArr (byte[] byteArr ){

		// DECLARE VARIABLES
		boolean[] bitSet = new boolean[8 * byteArr.length];
		int count=0;

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
				bitSet[count]=bit;;
				count++;
			}
		}
		
//		if(bitSet.length()<(8*byteArr.length)){
//			for (int i=0;i<(8*byteArr.length)-bitSet.length();i++){
//				bitSet.set(bitSet.length()+1,false);
//			}
//		}
//		

		return bitSet;

	} // end byteArr2BitSet()
	
	/****************************************************************************
	* Name:    bitArr2ByteArr
	* Purpose: converts a BitSet object into a byte array
	* Input:   bitset - the boolean array to be converted into the byte array. 
	* 		   Must be a multiple of 8 of the function will halt the System
	* Output:  returns a byte array equivalent to the given BitSet object
	****************************************************************************/
	public static byte[] bitArr2ByteArr(boolean[] bitset ){
		
		//crashes the system if the passed in boolean array is'nt compatible to a byte array
		if(bitset.length%8!=0){
			System.out.println("The passed in bit array cannot be parsed to a byte array" +
					"the value wont even fit into a bit");
			System.exit(0);
		}

		byte byteArry[] = new byte[bitset.length/8];
		int i,j,value,mult,count=0;
		
		for(i=0;i<byteArry.length;i++){
			value=0;
			mult=7;
					
			for(j=0;j<8;j++)
			{
				if(bitset[count]==true){
					value+=(Math.pow(2,mult));
				}
				mult--;
				count++;
			}
			byteArry[i]=(byte) value;
			
		}

		return byteArry;

	} // end bitSet2ByteArr()


}
