package mailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    
    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegisterRequests registerRequest, HttpSession session) {
        boolean success = userService.initiateUserRegistration(registerRequest, session);
        return success ? "Doğrulama kodu gönderildi!" : "E-posta adresi zaten kayıtlı!";
    }

    
    @PostMapping("/verify")
    public String verifyUser(@RequestBody UserVerificationRequest verificationRequest, HttpSession session) {
        boolean success = userService.completeUserRegistration(verificationRequest, session);
        return success ? "Kayıt tamamlandı!" : "Doğrulama kodu hatalı!";
    }
}