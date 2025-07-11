package com.edigest.journalApp.controller;

import com.edigest.journalApp.entity.JournalEntry;
import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.service.JournalEntryService;
import com.edigest.journalApp.service.UserService;
import org.apache.coyote.Response;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService ;


    @GetMapping("{userName}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName){
        User user = userService.findByUserName(userName);

        List<JournalEntry> all = user.getJournalEntries();
        if(all != null && !all.isEmpty() ){
            return new ResponseEntity<>(all,HttpStatus.OK);

        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);

     }

    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry newEntry,@PathVariable String userName){

        try {
             newEntry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(newEntry,userName);
            return new ResponseEntity<>(newEntry,HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(newEntry,HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("id/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId myId){
     Optional<JournalEntry> journalEntry = journalEntryService.findByID(myId);
        return journalEntry.map(entry -> new ResponseEntity<>(   HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }

    @DeleteMapping("id/{userName}/{myId}")
    public ResponseEntity<?>  deleteJournalById(@PathVariable ObjectId myId,@PathVariable String userName){
        journalEntryService.deleteByID(myId, userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }


    @PutMapping("id/{userName}/{id}")
    public ResponseEntity<?> updateJournalByID( 
            @PathVariable ObjectId id,
            @RequestBody JournalEntry newEntry,
            @PathVariable String userName)
    {
    JournalEntry oldEntry = journalEntryService.findByID(id).orElse(null);
        if(oldEntry != null){
            oldEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("")? newEntry.getTitle(): oldEntry.getTitle());
            oldEntry.setContent(newEntry.getContent()  != null && !newEntry.getContent().equals("")? newEntry.getContent(): oldEntry.getContent());
            journalEntryService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
            }
        return new ResponseEntity<>(  HttpStatus.NOT_FOUND );

    }



}
