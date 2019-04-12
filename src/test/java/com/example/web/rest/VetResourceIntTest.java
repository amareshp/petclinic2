package com.example.web.rest;

import com.example.Petclinic2App;

import com.example.domain.Vet;
import com.example.repository.VetRepository;
import com.example.repository.search.VetSearchRepository;
import com.example.service.VetService;
import com.example.web.rest.errors.ExceptionTranslator;

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
 * Test class for the VetResource REST controller.
 *
 * @see VetResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Petclinic2App.class)
public class VetResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE_PROVINCE = "AAAAAAAAAA";
    private static final String UPDATED_STATE_PROVINCE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private VetService vetService;

    /**
     * This repository is mocked in the com.example.repository.search test package.
     *
     * @see com.example.repository.search.VetSearchRepositoryMockConfiguration
     */
    @Autowired
    private VetSearchRepository mockVetSearchRepository;

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

    private MockMvc restVetMockMvc;

    private Vet vet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VetResource vetResource = new VetResource(vetService);
        this.restVetMockMvc = MockMvcBuilders.standaloneSetup(vetResource)
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
    public static Vet createEntity(EntityManager em) {
        Vet vet = new Vet()
            .name(DEFAULT_NAME)
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .stateProvince(DEFAULT_STATE_PROVINCE)
            .phone(DEFAULT_PHONE);
        return vet;
    }

    @Before
    public void initTest() {
        vet = createEntity(em);
    }

    @Test
    @Transactional
    public void createVet() throws Exception {
        int databaseSizeBeforeCreate = vetRepository.findAll().size();

        // Create the Vet
        restVetMockMvc.perform(post("/api/vets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vet)))
            .andExpect(status().isCreated());

        // Validate the Vet in the database
        List<Vet> vetList = vetRepository.findAll();
        assertThat(vetList).hasSize(databaseSizeBeforeCreate + 1);
        Vet testVet = vetList.get(vetList.size() - 1);
        assertThat(testVet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testVet.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testVet.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testVet.getStateProvince()).isEqualTo(DEFAULT_STATE_PROVINCE);
        assertThat(testVet.getPhone()).isEqualTo(DEFAULT_PHONE);

        // Validate the Vet in Elasticsearch
        verify(mockVetSearchRepository, times(1)).save(testVet);
    }

    @Test
    @Transactional
    public void createVetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vetRepository.findAll().size();

        // Create the Vet with an existing ID
        vet.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVetMockMvc.perform(post("/api/vets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vet)))
            .andExpect(status().isBadRequest());

        // Validate the Vet in the database
        List<Vet> vetList = vetRepository.findAll();
        assertThat(vetList).hasSize(databaseSizeBeforeCreate);

        // Validate the Vet in Elasticsearch
        verify(mockVetSearchRepository, times(0)).save(vet);
    }

    @Test
    @Transactional
    public void getAllVets() throws Exception {
        // Initialize the database
        vetRepository.saveAndFlush(vet);

        // Get all the vetList
        restVetMockMvc.perform(get("/api/vets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())));
    }
    
    @Test
    @Transactional
    public void getVet() throws Exception {
        // Initialize the database
        vetRepository.saveAndFlush(vet);

        // Get the vet
        restVetMockMvc.perform(get("/api/vets/{id}", vet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.stateProvince").value(DEFAULT_STATE_PROVINCE.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingVet() throws Exception {
        // Get the vet
        restVetMockMvc.perform(get("/api/vets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVet() throws Exception {
        // Initialize the database
        vetService.save(vet);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockVetSearchRepository);

        int databaseSizeBeforeUpdate = vetRepository.findAll().size();

        // Update the vet
        Vet updatedVet = vetRepository.findById(vet.getId()).get();
        // Disconnect from session so that the updates on updatedVet are not directly saved in db
        em.detach(updatedVet);
        updatedVet
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE)
            .phone(UPDATED_PHONE);

        restVetMockMvc.perform(put("/api/vets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVet)))
            .andExpect(status().isOk());

        // Validate the Vet in the database
        List<Vet> vetList = vetRepository.findAll();
        assertThat(vetList).hasSize(databaseSizeBeforeUpdate);
        Vet testVet = vetList.get(vetList.size() - 1);
        assertThat(testVet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVet.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testVet.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testVet.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);
        assertThat(testVet.getPhone()).isEqualTo(UPDATED_PHONE);

        // Validate the Vet in Elasticsearch
        verify(mockVetSearchRepository, times(1)).save(testVet);
    }

    @Test
    @Transactional
    public void updateNonExistingVet() throws Exception {
        int databaseSizeBeforeUpdate = vetRepository.findAll().size();

        // Create the Vet

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVetMockMvc.perform(put("/api/vets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vet)))
            .andExpect(status().isBadRequest());

        // Validate the Vet in the database
        List<Vet> vetList = vetRepository.findAll();
        assertThat(vetList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Vet in Elasticsearch
        verify(mockVetSearchRepository, times(0)).save(vet);
    }

    @Test
    @Transactional
    public void deleteVet() throws Exception {
        // Initialize the database
        vetService.save(vet);

        int databaseSizeBeforeDelete = vetRepository.findAll().size();

        // Delete the vet
        restVetMockMvc.perform(delete("/api/vets/{id}", vet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Vet> vetList = vetRepository.findAll();
        assertThat(vetList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Vet in Elasticsearch
        verify(mockVetSearchRepository, times(1)).deleteById(vet.getId());
    }

    @Test
    @Transactional
    public void searchVet() throws Exception {
        // Initialize the database
        vetService.save(vet);
        when(mockVetSearchRepository.search(queryStringQuery("id:" + vet.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(vet), PageRequest.of(0, 1), 1));
        // Search the vet
        restVetMockMvc.perform(get("/api/_search/vets?query=id:" + vet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vet.class);
        Vet vet1 = new Vet();
        vet1.setId(1L);
        Vet vet2 = new Vet();
        vet2.setId(vet1.getId());
        assertThat(vet1).isEqualTo(vet2);
        vet2.setId(2L);
        assertThat(vet1).isNotEqualTo(vet2);
        vet1.setId(null);
        assertThat(vet1).isNotEqualTo(vet2);
    }
}
