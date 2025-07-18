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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService ;


    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        List<JournalEntry> all = user.getJournalEntries();
        if(all != null && !all.isEmpty() ){
            return new ResponseEntity<>(all,HttpStatus.OK);

        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);

     }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry newEntry){


        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
             newEntry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(newEntry,userName);
            return new ResponseEntity<>(newEntry,HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(newEntry,HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("id/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId myId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()){
        Optional<JournalEntry> journalEntry = journalEntryService.findByID(myId);
        if(journalEntry.isPresent()){
        return new ResponseEntity<>(journalEntry.get(),HttpStatus.OK);}
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);


    }

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?>  deleteJournalById(@PathVariable ObjectId myId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
  boolean removed=      journalEntryService.deleteByID(myId, userName);
  if(removed){
  return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }else{
  return new ResponseEntity<>(HttpStatus.NOT_FOUND);

  }
     }


    @PutMapping("id/{id}")
    public ResponseEntity<?> updateJournalByID( 
            @PathVariable ObjectId myId,
            @RequestBody JournalEntry newEntry
             )
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());

        if(!collect.isEmpty()){
            Optional<JournalEntry> journalEntry = journalEntryService.findByID(myId);
            if(journalEntry.isPresent()){
                JournalEntry oldEntry = journalEntry.get();
                oldEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("")? newEntry.getTitle(): oldEntry.getTitle());
                oldEntry.setContent(newEntry.getContent()  != null && !newEntry.getContent().equals("")? newEntry.getContent(): oldEntry.getContent());
                journalEntryService.saveEntry(oldEntry);
                return new ResponseEntity<>(oldEntry, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(  HttpStatus.NOT_FOUND );

    }



}
