package com.morotech.assessment.controllers;

import com.morotech.assessment.services.GutendexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j

public class Controllers {

    private final GutendexService gutendexService;
    @GetMapping("/")
    public ResponseEntity<Object> retrieveParticipantsOfAnEvent()
    {

        return   ResponseEntity.ok(gutendexService.searchBooksByTitle("dickens great"));
    }

}
