package mailsender;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.util.Random;

@Service
public class UserService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;
    
    private UserRegisterRequests userRegisterRequests = new UserRegisterRequests();

   
    public boolean initiateUserRegistration(UserRegisterRequests registerRequest, HttpSession session) {
       
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            return false;  
        }

        
        userRegisterRequests.setName(registerRequest.getName());
        userRegisterRequests.setEmail(registerRequest.getEmail());
        userRegisterRequests.setPassword(registerRequest.getPassword());
        
        String generatedCode = generateVerificationCode();

        
        session.setAttribute("verificationCode", generatedCode);

        
        try {
            sendVerificationEmail(registerRequest.getEmail(), generatedCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true; 
    }

    
    public boolean completeUserRegistration(UserVerificationRequest verificationRequest, HttpSession session) {
        
        String generatedCode = (String) session.getAttribute("verificationCode");

        if (generatedCode == null) {
            return false;  
        }

        
        if (generatedCode.equals(verificationRequest.getVerificationCode())) {
            
            User user = new User();
            user.setName(userRegisterRequests.getName()); 
            user.setEmail(userRegisterRequests.getEmail());
            user.setPassword(userRegisterRequests.getPassword()); 

            userRepository.save(user);

            
            session.removeAttribute("verificationCode");

            return true; 
        }

        return false; 
    }

   
    public void sendVerificationEmail(String email, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Doğrulama Kodu");
            helper.setText("Doğrulama kodunuz: " + verificationCode);

            mailSender.send(message); 
            System.out.println("Doğrulama kodu başarıyla gönderildi.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("E-posta gönderilemedi: " + e.getMessage());
        }
    }
    
    public boolean sendVerificationCode(String email, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(message);
            return true; 
        } catch (MessagingException e) {
        	System.out.println("E-posta gönderim hatası: {}"+ e.getMessage());
            return false;
        }
    }


    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}