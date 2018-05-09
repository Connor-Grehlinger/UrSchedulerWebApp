package edu.TeamAlpha.meetingManager.Service;

import edu.TeamAlpha.meetingManager.models.CustomUserDetails;
import edu.TeamAlpha.meetingManager.repositories.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public UserService(UserDetailsRepository userDetailsRepository) {

        this.userDetailsRepository = userDetailsRepository;
    }

    public CustomUserDetails save(CustomUserDetails customUserDetails) {
    		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    		customUserDetails.setPassword(encoder.encode(customUserDetails.getPassword()));
        return this.userDetailsRepository.insert(customUserDetails);
    }

    public boolean checkLoginCred(String username, String pass) throws Exception{
      return pass.equals(this.userDetailsRepository.findByUsername(username).getPassword());
    }
}
