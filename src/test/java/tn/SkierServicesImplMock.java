package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkierServicesImplTest {

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
    
    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        subscription.setStartDate(LocalDate.of(2024, 1, 1));
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");
        skier.setSubscription(subscription);
    }
    
    @Test
    void testRetrieveAllSkiers() {
        List<Skier> skiers = Arrays.asList(skier);
        when(skierRepository.findAll()).thenReturn(skiers);
        
        List<Skier> retrievedSkiers = skierServices.retrieveAllSkiers();
        
        assertEquals(1, retrievedSkiers.size());
        verify(skierRepository, times(1)).findAll();
    }
    
    @Test
    void testAddSkier() {
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        
        Skier savedSkier = skierServices.addSkier(skier);
        
        assertNotNull(savedSkier);
        assertEquals(LocalDate.of(2025, 1, 1), savedSkier.getSubscription().getEndDate());
        verify(skierRepository, times(1)).save(any(Skier.class));
    }
    
    @Test
    void testAssignSkierToSubscription() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(2L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        
        Skier updatedSkier = skierServices.assignSkierToSubscription(1L, 2L);
        
        assertNotNull(updatedSkier);
        assertEquals(subscription, updatedSkier.getSubscription());
        verify(skierRepository, times(1)).save(skier);
    }
    
    @Test
    void testAddSkierAndAssignToCourse() {
        Course course = new Course();
        course.setNumCourse(1L);
        
        Registration registration = new Registration();
        registration.setSkier(skier);
        skier.setRegistrations(Set.of(registration));
        
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        when(courseRepository.getById(1L)).thenReturn(course);
        
        Skier savedSkier = skierServices.addSkierAndAssignToCourse(skier, 1L);
        
        assertNotNull(savedSkier);
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }
    
    @Test
    void testRemoveSkier() {
        doNothing().when(skierRepository).deleteById(1L);
        skierServices.removeSkier(1L);
        verify(skierRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testRetrieveSkier() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        
        Skier retrievedSkier = skierServices.retrieveSkier(1L);
        
        assertNotNull(retrievedSkier);
        assertEquals("John", retrievedSkier.getFirstName());
        verify(skierRepository, times(1)).findById(1L);
    }
    
    @Test
    void testAssignSkierToPiste() {
        Piste piste = new Piste();
        piste.setNumPiste(1L);
        
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        
        Skier updatedSkier = skierServices.assignSkierToPiste(1L, 1L);
        
        assertNotNull(updatedSkier);
        assertTrue(updatedSkier.getPistes().contains(piste));
        verify(skierRepository, times(1)).save(skier);
    }
    
    @Test
    void testRetrieveSkiersBySubscriptionType() {
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(Arrays.asList(skier));
        
        List<Skier> skiers = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);
        
        assertFalse(skiers.isEmpty());
        assertEquals(1, skiers.size());
        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
    }
}
