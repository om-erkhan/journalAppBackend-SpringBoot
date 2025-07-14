package com.edigest.journalApp.controller;

import com.edigest.journalApp.entity.JournalEntry;
import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.service.JournalEntryService;
import com.edigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PutMapping("")
    public ResponseEntity<?> updateUser(@RequestBody User user){
       User    userInDB =     userService.findByUserName(userName );

       if(userInDB != null){
           userInDB.setUserName(user.getUserName());
           userInDB.setPassword(user.getPassword());
       userService.saveEntry(userInDB);
       }
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
