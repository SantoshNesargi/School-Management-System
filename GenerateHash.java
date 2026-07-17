public class GenerateHash {
    public static void main(String[] args) {
        String[] passwords = {"admin123", "teacher123", "student123"};
        for (String password : passwords) {
            String hash = Students.PasswordUtil.hashPassword(password);
            System.out.println(password + ": " + hash);
        }
    }
}