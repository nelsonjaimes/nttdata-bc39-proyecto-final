package com.nttdata.bc39.grupo04.bootcoin.controller;

import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinDTO;
import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinOperationDTO;
import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinService;
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
        return service.getAllBootcoinAccounts();
    }

    @PostMapping("/save")
    public BootcoinDTO createBootcoinAccount(@RequestBody BootcoinDTO dto) {
        return service.save(dto);
    }

    @GetMapping("/{documentNumber}")
    public BootcoinDTO getBootcoinByDocumentNumber(@PathVariable("documentNumber") String documentNumber) {
        return service.getByDocumentNumber(documentNumber);
    }

    //Operations seller and buyer
    @PostMapping("/buyRequest")
    public BootcoinOperationDTO requestbuyOperation(@RequestBody BootcoinOperationDTO body) {
        return service.requestBuyOperation(body);
    }

    @PostMapping("/sellerAcept")
    public BootcoinOperationDTO sellerAceptOperation(@RequestBody BootcoinOperationDTO body) {
        return service.aceptSellOperation(body);
    }

    @GetMapping("/availableOperations/all")
    public List<BootcoinOperationDTO> listOfAvailableOperations() {
        return service.listOfAvailableOperations();
    }

    @GetMapping("/operations/all")
    public List<BootcoinOperationDTO> getAllOperations() {
        return service.getAllOperations();
    }
}
