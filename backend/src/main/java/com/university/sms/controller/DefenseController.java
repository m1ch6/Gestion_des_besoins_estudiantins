package com.university.sms.controller;

import com.university.sms.entity.Defense;
import com.university.sms.repository.DefenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/soutenances")
@RequiredArgsConstructor
public class DefenseController {
    private final DefenseRepository defenseRepository;

    @GetMapping
    public List<Defense> getAllDefenses() {
        return defenseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Defense> getDefenseById(@PathVariable Long id) {
        return defenseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Defense createDefense(@RequestBody Defense defense) {
        return defenseRepository.save(defense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Defense> updateDefense(@PathVariable Long id, @RequestBody Defense defense) {
        return defenseRepository.findById(id)
                .map(existing -> {
                    defense.setId(id);
                    return ResponseEntity.ok(defenseRepository.save(defense));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDefense(@PathVariable Long id) {
        if (defenseRepository.existsById(id)) {
            defenseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
