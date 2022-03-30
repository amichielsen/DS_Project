package be.uantwerpen.namingserver.hash;

public class Main {
    public static void main(String[] args) {
        Hash hash = new Hash();
        int hashval = hash.generateHash("abcd");
        System.out.println(hashval);
    }
}
