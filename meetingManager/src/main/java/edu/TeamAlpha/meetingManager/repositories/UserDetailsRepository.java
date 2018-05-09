package edu.TeamAlpha.meetingManager.repositories;




import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.TeamAlpha.meetingManager.models.*;

import java.util.List;

@Repository
public interface UserDetailsRepository extends MongoRepository<CustomUserDetails, String> {

    CustomUserDetails findByUsername(String username);

}
