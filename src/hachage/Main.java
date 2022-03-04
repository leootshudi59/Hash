package hachage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("========== TP 3-4 ==========");
        Scanner input = new Scanner(System.in);
        System.out.println("1. Exercice 1 : Messages hachés" +
                "\n2. Exercice 2 : HMAC");
        String choix = input.nextLine();
        switch (choix) {
            case "1":
                exercice1();
                break;
            case "2":
                exercice2();
                break;
            default:
                System.out.println("Erreur de choix");
                break;
        }
        input.close();
    }

    private static void exercice1() throws IOException {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        File dictionnaryFile = new File(
                "/Users/leootshudi/Library/CloudStorage/OneDrive-UniversitédeLimoges/S4/OS01/Hachage/src/ods5.txt");
        BufferedReader br = new BufferedReader(new FileReader(dictionnaryFile));
        String word;
        HashMap<String, String> strHashMap = new HashMap<String, String>();
        Scanner input = new Scanner(System.in);
        System.out.println("\nEntrez 1 ou 2");
        System.out.println("1. Extraire n derniers bits (q2)" + "\n2. Extraire n premiers bits (q5)");
        String bitExtractionMode = input.next();
        System.out.println("\nChargement du dictionnaire...");

        while ((word = br.readLine()) != null) {
            hash(word, strHashMap);
        }

        int testedWords = 0;
        System.out.print("\nQuel mot du dictionnaire voulez-vous tester ? ");
        String wordToTest = input.next().toLowerCase();
        char[] firstOccurrenceHash = strHashMap.get(wordToTest).toCharArray();
        StringBuilder extractFirstOccurrenceHash = new StringBuilder();
        System.out.println("\nSaisir nombre de bits à extraire (entre 5 et 10) : ");
        Integer bitsToExtract = Integer.parseInt(input.next());
        switch (bitExtractionMode) {
            // En extrayant les n derniers bits du haché
            case "1":
                for (int i = firstOccurrenceHash.length - bitsToExtract; i < firstOccurrenceHash.length; i++) {
                    extractFirstOccurrenceHash.append(firstOccurrenceHash[i]);
                }
                break;
            // En extrayant les n premiers bits du haché
            case "2":
                for (int i = 0; i < bitsToExtract; i++) {
                    extractFirstOccurrenceHash.append(firstOccurrenceHash[i]);
                }
                break;
            default:
                System.out.println("erreur saisie");
                break;
        }
        System.out.println("Mot testé : " + wordToTest);
        for (Map.Entry<String, String> entry : strHashMap.entrySet()) {
            char[] strHash = entry.getValue().toCharArray();
            StringBuilder extractStrHash = new StringBuilder();
            switch (bitExtractionMode) {
                // En extrayant les n derniers bits du haché
                case "1":
                    for (int i = strHash.length - bitsToExtract; i < strHash.length; i++) {
                        extractStrHash.append(strHash[i]);
                    }
                    break;
                // En extrayant les n premiers bits du haché
                case "2":
                    for (int i = 0; i < bitsToExtract; i++) {
                        extractStrHash.append(strHash[i]);
                    }
                    break;
            }

            if (!extractFirstOccurrenceHash.toString().equals(extractStrHash.toString())) {
                testedWords += 1;
            } else {
                System.out.println("Une occurence a été trouvée au mot : " + entry.getKey()
                        + " après " + testedWords + " mots testés.");
                break;
            }
        }
        input.close();
    }


    private static void exercice2() throws IOException {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        String key = "Exercise2";
        byte[] keyBytes = key.getBytes("UTF-8");
        System.out.println("La clé qui sera utilisée pour donner le HMAC sera \"" + key +"\"");
        Scanner input = new Scanner(System.in);
        System.out.println("\n\nEntrez 1 ou 2");
        System.out.println("\n1. HMAC-SHA1" + "\n2. HMAC-SHA512");
        String algorithm = input.next();
        String hMacInstance = "";
        SecretKeySpec secretKeySpec = null;
        switch (algorithm) {
            case "1":
                hMacInstance = "HmacSHA1";
                secretKeySpec = new SecretKeySpec(keyBytes, "SHA-1");
                break;
            case "2":
                hMacInstance = "HmacSHA512";
                secretKeySpec = new SecretKeySpec(keyBytes, "SHA-512");
                break;
            default:
                System.out.println("Erreur Saisie");
                break;
        }

        try {
            Mac mac = Mac.getInstance(hMacInstance);
            mac.init(secretKeySpec);
            byte[] hmacSHA = mac.doFinal("Ceci est mon premier HMAC SHA1".getBytes("UTF-8"));
            String hmacShaString = byteToHex(hmacSHA);
            System.out.println("Hex : " + hmacShaString);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA1", e);
        }

    }


    private static void hash(String str, HashMap<String, String> strHashMap) {
        String hashedStr = "";
        try {
            MessageDigest md2 = MessageDigest.getInstance("SHA-1");
            md2.reset();
            md2.update(str.getBytes(StandardCharsets.UTF_8));
            hashedStr = byteToHex(md2.digest());
            hashedStr = hexToBin(hashedStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        strHashMap.put(str, hashedStr);
        //System.out.println(strHashMap);
    }


    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String hexToBin(String hex) {
        hex = hex.replaceAll("0", "0000");
        hex = hex.replaceAll("1", "0001");
        hex = hex.replaceAll("2", "0010");
        hex = hex.replaceAll("3", "0011");
        hex = hex.replaceAll("4", "0100");
        hex = hex.replaceAll("5", "0101");
        hex = hex.replaceAll("6", "0110");
        hex = hex.replaceAll("7", "0111");
        hex = hex.replaceAll("8", "1000");
        hex = hex.replaceAll("9", "1001");
        hex = hex.replaceAll("a", "1010");
        hex = hex.replaceAll("b", "1011");
        hex = hex.replaceAll("c", "1100");
        hex = hex.replaceAll("d", "1101");
        hex = hex.replaceAll("e", "1110");
        hex = hex.replaceAll("f", "1111");
        return hex;
    }
}