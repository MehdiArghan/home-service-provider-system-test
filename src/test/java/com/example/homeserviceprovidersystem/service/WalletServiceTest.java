package com.example.homeserviceprovidersystem.service;

import com.example.homeserviceprovidersystem.dto.person.PersonRequestWithEmail;
import com.example.homeserviceprovidersystem.dto.wallet.WalletResponse;
import com.example.homeserviceprovidersystem.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Test
    void save() {
        Wallet wallet = new Wallet();
        wallet.setPrice(5000);
        Wallet savedWallet = walletService.save(wallet);
        assertNotNull(savedWallet.getId());
        assertEquals(5000, savedWallet.getPrice());
    }

    @Test
    void findWalletForCustomer() {
        PersonRequestWithEmail request = new PersonRequestWithEmail("mehdi.arghan.cr77@gmail.com");
        String personType = "customer";
        WalletResponse walletResponse = walletService.findWallet(request, personType);
        assertNotNull(walletResponse);
        assertEquals(1300.0, walletResponse.getPrice());
    }

    @Test
    void findWalletForExpert() {
        PersonRequestWithEmail request = new PersonRequestWithEmail("mehdi.arghan.cr77@gmail.com");
        String personType = "expert";
        WalletResponse walletResponse = walletService.findWallet(request, personType);
        assertNotNull(walletResponse);
        assertEquals(1680.0, walletResponse.getPrice());
    }
}