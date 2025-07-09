package com.edigest.journalApp.controller;

import com.edigest.journalApp.entity.JournalEntry;
import com.edigest.journalApp.service.JournalEntryService;
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


    @GetMapping
    public ResponseEntity<?> getAll(){
        List<JournalEntry> all = journalEntryService.getAll();
        if(all != null && !all.isEmpty() ){
            return new ResponseEntity<>(all,HttpStatus.OK);

        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);

     }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry newEntry){

        try {
            newEntry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(newEntry);
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

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?>  deleteJournalById(@PathVariable ObjectId myId){
        journalEntryService.deleteByID(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }


    @PutMapping("id/{id}")
    public ResponseEntity<?> updateJournalByID(@PathVariable ObjectId id, @RequestBody JournalEntry newEntry){
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
