package tn.esprit.spring.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
public class SkierServicesImplTest {

    @Mock
    private ISkierRepository skierRepository;
    
    @Mock
    private IPisteRepository pisteRepository;
    
    @Mock
    private ICourseRepository courseRepository;
    
    @Mock
    private IRegistrationRepository registrationRepository;
    
    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SkierServicesImpl skierServices;

    private Skier skier;
    private Subscription subscription;
    private Piste piste;

    @BeforeEach
    void setUp() {
        skier = new Skier(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "City", null, new HashSet<>(), new HashSet<>());
        subscription = new Subscription(1L, LocalDate.now(), null, TypeSubscription.ANNUAL);
        piste = new Piste(1L, "Easy", 2);
    }

    @Test
    void testRetrieveAllSkiers() {
        when(skierRepository.findAll()).thenReturn(List.of(skier));
        assertEquals(1, skierServices.retrieveAllSkiers().size());
    }

    @Test
    void testAddSkier() {
        skier.setSubscription(subscription);
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        Skier savedSkier = skierServices.addSkier(skier);
        assertNotNull(savedSkier);
        assertEquals(subscription.getEndDate(), subscription.getStartDate().plusYears(1));
    }

    @Test
    void testAssignSkierToSubscription() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        Skier updatedSkier = skierServices.assignSkierToSubscription(1L, 1L);
        assertNotNull(updatedSkier.getSubscription());
    }

    @Test
    void testAssignSkierToPiste() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        Skier updatedSkier = skierServices.assignSkierToPiste(1L, 1L);
        assertTrue(updatedSkier.getPistes().contains(piste));
    }

    @Test
    void testRemoveSkier() {
        doNothing().when(skierRepository).deleteById(1L);
        assertDoesNotThrow(() -> skierServices.removeSkier(1L));
    }

    @Test
    void testRetrieveSkier() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        assertEquals(skier, skierServices.retrieveSkier(1L));
    }
}
