/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externalemail.luxembourg;

/**
 * Apply Verhoeff algorithm to compute check digit
 * This algorithm is used to compute the second check digit during extended unique id generation
 *
 * @author Ciedmdr
 */
public class CheckDigitVerhoeff {

    private static int inv(int iPos) {
        int invTable[] = {
                0,
                4,
                3,
                2,
                1,
                5,
                6,
                7,
                8,
                9};
        return invTable[iPos];
    }

    private static int d(int j, int k) {
        int dTable[][] = {
                {
                        0,
                        1,
                        2,
                        3,
                        4,
                        5,
                        6,
                        7,
                        8,
                        9},
                {
                        1,
                        2,
                        3,
                        4,
                        0,
                        6,
                        7,
                        8,
                        9,
                        5},
                {
                        2,
                        3,
                        4,
                        0,
                        1,
                        7,
                        8,
                        9,
                        5,
                        6},
                {
                        3,
                        4,
                        0,
                        1,
                        2,
                        8,
                        9,
                        5,
                        6,
                        7},
                {
                        4,
                        0,
                        1,
                        2,
                        3,
                        9,
                        5,
                        6,
                        7,
                        8},
                {
                        5,
                        9,
                        8,
                        7,
                        6,
                        0,
                        4,
                        3,
                        2,
                        1},
                {
                        6,
                        5,
                        9,
                        8,
                        7,
                        1,
                        0,
                        4,
                        3,
                        2},
                {
                        7,
                        6,
                        5,
                        9,
                        8,
                        2,
                        1,
                        0,
                        4,
                        3},
                {
                        8,
                        7,
                        6,
                        5,
                        9,
                        3,
                        2,
                        1,
                        0,
                        4},
                {
                        9,
                        8,
                        7,
                        6,
                        5,
                        4,
                        3,
                        2,
                        1,
                        0}};
        return dTable[j][k];
    }

    private static int p(int i, int Ni) {
        int pTable[][] = {
                {
                        0,
                        1,
                        2,
                        3,
                        4,
                        5,
                        6,
                        7,
                        8,
                        9},
                {
                        1,
                        5,
                        7,
                        6,
                        2,
                        8,
                        3,
                        0,
                        9,
                        4},
                {
                        5,
                        8,
                        0,
                        3,
                        7,
                        9,
                        6,
                        1,
                        4,
                        2},
                {
                        8,
                        9,
                        1,
                        6,
                        0,
                        4,
                        3,
                        5,
                        2,
                        7},
                {
                        9,
                        4,
                        5,
                        3,
                        1,
                        2,
                        6,
                        8,
                        7,
                        0},
                {
                        4,
                        2,
                        8,
                        6,
                        5,
                        7,
                        3,
                        9,
                        0,
                        1},
                {
                        2,
                        7,
                        9,
                        3,
                        8,
                        0,
                        6,
                        4,
                        1,
                        5},
                {
                        7,
                        0,
                        4,
                        6,
                        9,
                        1,
                        3,
                        2,
                        5,
                        8}};
        return pTable[i % 8][Ni];
    }

    /**
     * Computes the checksum C as
     * C = inv(F_n (a_n)×F_(n-1) (a_(n-1) )×… ×F_1 (a_1 ) )
     * (with × being the multiplication in D_5)
     *
     * @param String charset to compute Verhoeff check digit
     * @return the check digit
     */
    public static int computeCheckDigit(String iNumber) {
        int checkSum = 0;
        for (int pos = 0; pos < iNumber.length(); pos++) {
            checkSum = d(checkSum, p(pos + 1, Character.digit(iNumber.charAt(iNumber.length() - pos - 1), 10)));
        }
        return inv(checkSum);
    }

    /**
     * Verify the number in parameter (11 DIGITS + Verhoeff check digit = 12 DIGITS)
     * The verification computes and verified the following equation
     * (F_n (a_n )×F_(n-1) (a_(n-1) )×…×F_1 (a_1 )×C) = 0
     *
     * @param iNumber
     * @return true if checked
     */
    public static boolean checkDigit(String iNumber) {
        int checkSum = 0;
        for (int pos = 0; pos < iNumber.length(); pos++) {
            checkSum = d(checkSum, p(pos, Character.digit(iNumber.charAt(iNumber.length() - pos - 1), 10)));
        }
        if (checkSum == 0) {
            return true;
        }
        return false;
    }
}
