package edu.TeamAlpha.meetingManager.repositories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.TeamAlpha.meetingManager.models.*;

@Repository
public interface MeetingPrototypeRepository extends MongoRepository<MeetingPrototype, String> {

}
