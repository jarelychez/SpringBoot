package com.webage.dao;

import com.webage.domain.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ResourceServerDaoImpl implements ResourceServerDao {

    //  PART 15a -
    @Value("${resource.server.uri}") String uri;
    @Value("${resource.server.port}") String port;

    //  PART 15b -
    //  Add the @Autowired annotation to this member variable:
    @Autowired
    RestClient client;


    private String serverUri() {
        return String.format(uri,port);
    }

    @Override
    public Iterable<Purchase> findAllPurchases(String jwt) {
        ResponseEntity<Purchase[]> responseEntity = client
                .get()
                .uri(serverUri() + "/purchases")
                .header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Purchase[].class);
        return Arrays.asList(responseEntity.getBody());
    }


    @Override
    public Optional<Purchase> findPurchaseById(String jwt, long id) {
        ResponseEntity<Purchase> responseEntity = client
                .get()
                .uri(serverUri() + "/purchases/{id}",id)

                //  PART 15f - 
                //  Compare the .header() and .accept() lines below to 
                //  your implementation above.  Did you get it right?

                .header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Purchase.class);
        return Optional.of(responseEntity.getBody());
    }
}
