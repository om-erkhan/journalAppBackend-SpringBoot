package com.edigest.journalApp.service;

import com.edigest.journalApp.entity.JournalEntry;
import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName){
        try {
            User user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);

            userService.saveUser(user);

        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("an error occurred while saving te entry.",e);
        }
        }

     public void saveEntry(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry);
    }


    public List<JournalEntry> getAll(){
            return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findByID(ObjectId id){
        return journalEntryRepository.findById(id);
        
    }

    @Transactional
    public boolean deleteByID(ObjectId id, String userName){
        boolean removed = false;
        try {
          User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
          if(removed){
              userService.saveUser(user);
              journalEntryRepository.deleteById(id);
          }
      }catch (Exception e){
          System.out.println(e);
          throw new RuntimeException("An error occurred while deleting the entry,",e);
      }
      return removed;

    }

//    public List<JournalEntry> findByUserName(String userName){
////        findByUserName(us)
//    }

}
