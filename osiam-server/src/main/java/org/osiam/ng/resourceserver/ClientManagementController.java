package org.osiam.ng.resourceserver;

import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.ng.resourceserver.dao.ClientDao;
import org.osiam.ng.resourceserver.entities.ClientEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;

@Controller
@RequestMapping(value = "/Client")
public class ClientManagementController {

    @Inject
    private ClientDao clientDao;

    private ObjectMapper mapper = new ObjectMapper();


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ClientEntity getClient(@PathVariable final String id) {
        return clientDao.getClient(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ClientEntity create(@RequestBody String client) throws IOException {
        return clientDao.create(getClientEntity(client));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable final String id) {
        clientDao.delete(id);
    }

    private ClientEntity getClientEntity(String client) throws IOException {
        return new ClientEntity(mapper.readValue(client, ClientEntity.class));
    }
}