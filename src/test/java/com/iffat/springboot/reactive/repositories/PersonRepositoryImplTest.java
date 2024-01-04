package com.iffat.springboot.reactive.repositories;

import com.iffat.springboot.reactive.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryImplTest {

    PersonRepository personRepository = new PersonRepositoryImpl();

    @Test
    void testGetByIdFound() {
        Mono<Person> personMono = personRepository.findById(3);

        assertTrue(personMono.hasElement().block());
    }

    @Test
    void testGetByIdNotFound() {
        Mono<Person> personMono = personRepository.findById(8);

        assertFalse(personMono.hasElement().block());
    }

    @Test
    void testMonoByIdBlock() {
        Mono<Person> personMono = personRepository.findById(1);

        Person person = personMono.block();

        System.out.println(person.toString());
    }

    @Test
    void testGetByIdSubscriber() {
        Mono<Person> personMono = personRepository.findById(1);

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testMapOperation() {
        Mono<Person> personMono = personRepository.findById(1);

        personMono.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        Person person = personFlux.blockFirst();
        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(System.out::println);
    }

    @Test
    void testFluxMap() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxToList() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> listMono = personFlux.collectList();

        listMono.subscribe(list -> {
            list.forEach(person -> System.out.println(person.getFirstName()));
        });
    }

    @Test
    void testFilterOnName() {
        personRepository.findAll()
                .filter(person -> person.getFirstName().equals("Fiona"))
                .subscribe(person -> System.out.println(person.getLastName()));
    }

    @Test
    void testGetById() {
        Mono<Person> fionaMono = personRepository.findAll().filter(person -> person.getFirstName().equals("Fiona")).next();

        fionaMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 8;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single()
                .doOnError(throwable -> {
                    System.out.println("Error occurred on flux");
                    System.out.println(throwable.toString());
                });

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        }, throwable -> {
            System.out.println("Error occurred on mono");
            System.out.println(throwable.toString());
        });
    }
}