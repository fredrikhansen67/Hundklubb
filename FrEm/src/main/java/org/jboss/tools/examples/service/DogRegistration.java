package org.jboss.tools.examples.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.tools.examples.model.Dog;

@Stateless
public class DogRegistration {
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Dog> dogEventSrc;

    public void register(Dog dog) throws Exception {
    	
        log.info("Registering " + dog.getName()+ " " + dog.getBreed());
        em.persist(dog);
        dogEventSrc.fire(dog);
    }
    //   
}
