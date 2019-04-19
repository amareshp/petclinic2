package com.example.web.rest;

import com.example.Petclinic2App;

import com.example.domain.Pet;
import com.example.domain.Owner;
import com.example.repository.PetRepository;
import com.example.repository.search.PetSearchRepository;
import com.example.service.PetService;
import com.example.web.rest.errors.ExceptionTranslator;
import com.example.service.dto.PetCriteria;
import com.example.service.PetQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.example.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PetResource REST controller.
 *
 * @see PetResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Petclinic2App.class)
public class PetResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_BREED = "AAAAAAAAAA";
    private static final String UPDATED_BREED = "BBBBBBBBBB";

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetService petService;

    /**
     * This repository is mocked in the com.example.repository.search test package.
     *
     * @see com.example.repository.search.PetSearchRepositoryMockConfiguration
     */
    @Autowired
    private PetSearchRepository mockPetSearchRepository;

    @Autowired
    private PetQueryService petQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPetMockMvc;

    private Pet pet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PetResource petResource = new PetResource(petService, petQueryService);
        this.restPetMockMvc = MockMvcBuilders.standaloneSetup(petResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pet createEntity(EntityManager em) {
        Pet pet = new Pet()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .breed(DEFAULT_BREED);
        return pet;
    }

    @Before
    public void initTest() {
        pet = createEntity(em);
    }

    @Test
    @Transactional
    public void createPet() throws Exception {
        int databaseSizeBeforeCreate = petRepository.findAll().size();

        // Create the Pet
        restPetMockMvc.perform(post("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pet)))
            .andExpect(status().isCreated());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeCreate + 1);
        Pet testPet = petList.get(petList.size() - 1);
        assertThat(testPet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPet.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testPet.getBreed()).isEqualTo(DEFAULT_BREED);

        // Validate the Pet in Elasticsearch
        verify(mockPetSearchRepository, times(1)).save(testPet);
    }

    @Test
    @Transactional
    public void createPetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = petRepository.findAll().size();

        // Create the Pet with an existing ID
        pet.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPetMockMvc.perform(post("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pet)))
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeCreate);

        // Validate the Pet in Elasticsearch
        verify(mockPetSearchRepository, times(0)).save(pet);
    }

    @Test
    @Transactional
    public void getAllPets() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList
        restPetMockMvc.perform(get("/api/pets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].breed").value(hasItem(DEFAULT_BREED.toString())));
    }
    
    @Test
    @Transactional
    public void getPet() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get the pet
        restPetMockMvc.perform(get("/api/pets/{id}", pet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.breed").value(DEFAULT_BREED.toString()));
    }

    @Test
    @Transactional
    public void getAllPetsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where name equals to DEFAULT_NAME
        defaultPetShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the petList where name equals to UPDATED_NAME
        defaultPetShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPetsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPetShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the petList where name equals to UPDATED_NAME
        defaultPetShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPetsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where name is not null
        defaultPetShouldBeFound("name.specified=true");

        // Get all the petList where name is null
        defaultPetShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllPetsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where type equals to DEFAULT_TYPE
        defaultPetShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the petList where type equals to UPDATED_TYPE
        defaultPetShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllPetsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultPetShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the petList where type equals to UPDATED_TYPE
        defaultPetShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllPetsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where type is not null
        defaultPetShouldBeFound("type.specified=true");

        // Get all the petList where type is null
        defaultPetShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllPetsByBreedIsEqualToSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where breed equals to DEFAULT_BREED
        defaultPetShouldBeFound("breed.equals=" + DEFAULT_BREED);

        // Get all the petList where breed equals to UPDATED_BREED
        defaultPetShouldNotBeFound("breed.equals=" + UPDATED_BREED);
    }

    @Test
    @Transactional
    public void getAllPetsByBreedIsInShouldWork() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where breed in DEFAULT_BREED or UPDATED_BREED
        defaultPetShouldBeFound("breed.in=" + DEFAULT_BREED + "," + UPDATED_BREED);

        // Get all the petList where breed equals to UPDATED_BREED
        defaultPetShouldNotBeFound("breed.in=" + UPDATED_BREED);
    }

    @Test
    @Transactional
    public void getAllPetsByBreedIsNullOrNotNull() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where breed is not null
        defaultPetShouldBeFound("breed.specified=true");

        // Get all the petList where breed is null
        defaultPetShouldNotBeFound("breed.specified=false");
    }

    @Test
    @Transactional
    public void getAllPetsByOwnerIsEqualToSomething() throws Exception {
        // Initialize the database
        Owner owner = OwnerResourceIntTest.createEntity(em);
        em.persist(owner);
        em.flush();
        pet.setOwner(owner);
        petRepository.saveAndFlush(pet);
        Long ownerId = owner.getId();

        // Get all the petList where owner equals to ownerId
        defaultPetShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the petList where owner equals to ownerId + 1
        defaultPetShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPetShouldBeFound(String filter) throws Exception {
        restPetMockMvc.perform(get("/api/pets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].breed").value(hasItem(DEFAULT_BREED)));

        // Check, that the count call also returns 1
        restPetMockMvc.perform(get("/api/pets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPetShouldNotBeFound(String filter) throws Exception {
        restPetMockMvc.perform(get("/api/pets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPetMockMvc.perform(get("/api/pets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPet() throws Exception {
        // Get the pet
        restPetMockMvc.perform(get("/api/pets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePet() throws Exception {
        // Initialize the database
        petService.save(pet);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPetSearchRepository);

        int databaseSizeBeforeUpdate = petRepository.findAll().size();

        // Update the pet
        Pet updatedPet = petRepository.findById(pet.getId()).get();
        // Disconnect from session so that the updates on updatedPet are not directly saved in db
        em.detach(updatedPet);
        updatedPet
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .breed(UPDATED_BREED);

        restPetMockMvc.perform(put("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPet)))
            .andExpect(status().isOk());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeUpdate);
        Pet testPet = petList.get(petList.size() - 1);
        assertThat(testPet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPet.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testPet.getBreed()).isEqualTo(UPDATED_BREED);

        // Validate the Pet in Elasticsearch
        verify(mockPetSearchRepository, times(1)).save(testPet);
    }

    @Test
    @Transactional
    public void updateNonExistingPet() throws Exception {
        int databaseSizeBeforeUpdate = petRepository.findAll().size();

        // Create the Pet

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPetMockMvc.perform(put("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pet)))
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pet in Elasticsearch
        verify(mockPetSearchRepository, times(0)).save(pet);
    }

    @Test
    @Transactional
    public void deletePet() throws Exception {
        // Initialize the database
        petService.save(pet);

        int databaseSizeBeforeDelete = petRepository.findAll().size();

        // Delete the pet
        restPetMockMvc.perform(delete("/api/pets/{id}", pet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Pet in Elasticsearch
        verify(mockPetSearchRepository, times(1)).deleteById(pet.getId());
    }

    @Test
    @Transactional
    public void searchPet() throws Exception {
        // Initialize the database
        petService.save(pet);
        when(mockPetSearchRepository.search(queryStringQuery("id:" + pet.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(pet), PageRequest.of(0, 1), 1));
        // Search the pet
        restPetMockMvc.perform(get("/api/_search/pets?query=id:" + pet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].breed").value(hasItem(DEFAULT_BREED)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pet.class);
        Pet pet1 = new Pet();
        pet1.setId(1L);
        Pet pet2 = new Pet();
        pet2.setId(pet1.getId());
        assertThat(pet1).isEqualTo(pet2);
        pet2.setId(2L);
        assertThat(pet1).isNotEqualTo(pet2);
        pet1.setId(null);
        assertThat(pet1).isNotEqualTo(pet2);
    }
}
