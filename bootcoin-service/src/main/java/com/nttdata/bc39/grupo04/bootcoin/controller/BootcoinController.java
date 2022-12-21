package com.nttdata.bc39.grupo04.bootcoin.controller;

import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinDTO;
import com.nttdata.bc39.grupo04.bootcoin.service.BootcoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("all")
@RequestMapping(value = "/bootcoin")
@RequiredArgsConstructor
public class BootcoinController {
    private final BootcoinService service;

    @GetMapping("/all")
    public List<BootcoinDTO> getAllBootcoinAccount() {
        return service.getAllAccounts();
    }

    @PostMapping("/save")
    public BootcoinDTO createBootcoinAccount(@RequestBody BootcoinDTO dto) {
        return service.save(dto);
    }

    @GetMapping("/{documentNumber}")
    public BootcoinDTO getBootcoinByDocumentNumber(@PathVariable("documentNumber") String documentNumber) {
        return service.getByDocumentNumber(documentNumber);
    }
}
