import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptCli {
    public static void main(String[] args) {
        String pw = args.length > 0 ? args[0] : "admin";
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        System.out.println(enc.encode(pw));
    }
}

