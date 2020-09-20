/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Robert Hutter
 */
public class ShortTest {
    public static void main (String[] args)
    {
        char left = 90;
        char right = 160;
        short s2 = (short) ((short) (left << 8) | right);
        short s = (((char)90) << 8) | ((char)160);
        System.out.println((Short.toString(s)));
        System.out.println((Short.toString(s2)));
    }
}
