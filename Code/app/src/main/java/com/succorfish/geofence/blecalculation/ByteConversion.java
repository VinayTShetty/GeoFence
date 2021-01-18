package com.succorfish.geofence.blecalculation;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ByteConversion {
    public static byte[] convertToTwoBytes(int data){
        return new byte[]{(byte)(data & 0x00FF),(byte)((data & 0xFF00)>>8)};
    }
    public static byte[] convertToFourBytes(final int data) {
        return new byte[] {
                (byte)((data >> 0) & 0xff),
                (byte)((data >> 8) & 0xff),
                (byte)((data >> 16) & 0xff),
                (byte)((data >> 24) & 0xff),
        };
    }


  public static byte []convertIntegertTWOBytes(int number){
      BigInteger bigInt = BigInteger.valueOf(number);
      return bigInt.toByteArray();
  }

    public static byte[] convert_LongTo_4_bytes(long value){
        byte [] data = new byte[4];
        data[3] = (byte) value;
        data[2] = (byte) (value >>> 8);
        data[1] = (byte) (value >>> 16);
        data[0] = (byte) (value >>> 32);
        return data;
    }

    /**
     * Used to convert 7 bytes long value to long.
     *  Logic Used in this method is reversiong the bytes in String and the calcualting.
     */

    public static String convert7bytesToLong(String valueToConevrt){
     long value=   new BigInteger(valueToConevrt, 16).longValue();
     return ""+value;
    }
}


