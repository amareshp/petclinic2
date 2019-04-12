package com.example.web.rest;

import com.example.Petclinic2App;

import com.example.domain.Slot;
import com.example.repository.SlotRepository;
import com.example.repository.search.SlotSearchRepository;
import com.example.service.SlotService;
import com.example.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Test class for the SlotResource REST controller.
 *
 * @see SlotResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Petclinic2App.class)
public class SlotResourceIntTest {

    private static final String DEFAULT_START_TIME = "AAAAAAAAAA";
    private static final String UPDATED_START_TIME = "BBBBBBBBBB";

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private SlotService slotService;

    /**
     * This repository is mocked in the com.example.repository.search test package.
     *
     * @see com.example.repository.search.SlotSearchRepositoryMockConfiguration
     */
    @Autowired
    private SlotSearchRepository mockSlotSearchRepository;

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

    private MockMvc restSlotMockMvc;

    private Slot slot;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SlotResource slotResource = new SlotResource(slotService);
        this.restSlotMockMvc = MockMvcBuilders.standaloneSetup(slotResource)
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
    public static Slot createEntity(EntityManager em) {
        Slot slot = new Slot()
            .startTime(DEFAULT_START_TIME);
        return slot;
    }

    @Before
    public void initTest() {
        slot = createEntity(em);
    }

    @Test
    @Transactional
    public void createSlot() throws Exception {
        int databaseSizeBeforeCreate = slotRepository.findAll().size();

        // Create the Slot
        restSlotMockMvc.perform(post("/api/slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(slot)))
            .andExpect(status().isCreated());

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll();
        assertThat(slotList).hasSize(databaseSizeBeforeCreate + 1);
        Slot testSlot = slotList.get(slotList.size() - 1);
        assertThat(testSlot.getStartTime()).isEqualTo(DEFAULT_START_TIME);

        // Validate the Slot in Elasticsearch
        verify(mockSlotSearchRepository, times(1)).save(testSlot);
    }

    @Test
    @Transactional
    public void createSlotWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = slotRepository.findAll().size();

        // Create the Slot with an existing ID
        slot.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSlotMockMvc.perform(post("/api/slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(slot)))
            .andExpect(status().isBadRequest());

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll();
        assertThat(slotList).hasSize(databaseSizeBeforeCreate);

        // Validate the Slot in Elasticsearch
        verify(mockSlotSearchRepository, times(0)).save(slot);
    }

    @Test
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = slotRepository.findAll().size();
        // set the field null
        slot.setStartTime(null);

        // Create the Slot, which fails.

        restSlotMockMvc.perform(post("/api/slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(slot)))
            .andExpect(status().isBadRequest());

        List<Slot> slotList = slotRepository.findAll();
        assertThat(slotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSlots() throws Exception {
        // Initialize the database
        slotRepository.saveAndFlush(slot);

        // Get all the slotList
        restSlotMockMvc.perform(get("/api/slots?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(slot.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())));
    }
    
    @Test
    @Transactional
    public void getSlot() throws Exception {
        // Initialize the database
        slotRepository.saveAndFlush(slot);

        // Get the slot
        restSlotMockMvc.perform(get("/api/slots/{id}", slot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(slot.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSlot() throws Exception {
        // Get the slot
        restSlotMockMvc.perform(get("/api/slots/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSlot() throws Exception {
        // Initialize the database
        slotService.save(slot);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSlotSearchRepository);

        int databaseSizeBeforeUpdate = slotRepository.findAll().size();

        // Update the slot
        Slot updatedSlot = slotRepository.findById(slot.getId()).get();
        // Disconnect from session so that the updates on updatedSlot are not directly saved in db
        em.detach(updatedSlot);
        updatedSlot
            .startTime(UPDATED_START_TIME);

        restSlotMockMvc.perform(put("/api/slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSlot)))
            .andExpect(status().isOk());

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
        Slot testSlot = slotList.get(slotList.size() - 1);
        assertThat(testSlot.getStartTime()).isEqualTo(UPDATED_START_TIME);

        // Validate the Slot in Elasticsearch
        verify(mockSlotSearchRepository, times(1)).save(testSlot);
    }

    @Test
    @Transactional
    public void updateNonExistingSlot() throws Exception {
        int databaseSizeBeforeUpdate = slotRepository.findAll().size();

        // Create the Slot

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSlotMockMvc.perform(put("/api/slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(slot)))
            .andExpect(status().isBadRequest());

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Slot in Elasticsearch
        verify(mockSlotSearchRepository, times(0)).save(slot);
    }

    @Test
    @Transactional
    public void deleteSlot() throws Exception {
        // Initialize the database
        slotService.save(slot);

        int databaseSizeBeforeDelete = slotRepository.findAll().size();

        // Delete the slot
        restSlotMockMvc.perform(delete("/api/slots/{id}", slot.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Slot> slotList = slotRepository.findAll();
        assertThat(slotList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Slot in Elasticsearch
        verify(mockSlotSearchRepository, times(1)).deleteById(slot.getId());
    }

    @Test
    @Transactional
    public void searchSlot() throws Exception {
        // Initialize the database
        slotService.save(slot);
        when(mockSlotSearchRepository.search(queryStringQuery("id:" + slot.getId())))
            .thenReturn(Collections.singletonList(slot));
        // Search the slot
        restSlotMockMvc.perform(get("/api/_search/slots?query=id:" + slot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(slot.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Slot.class);
        Slot slot1 = new Slot();
        slot1.setId(1L);
        Slot slot2 = new Slot();
        slot2.setId(slot1.getId());
        assertThat(slot1).isEqualTo(slot2);
        slot2.setId(2L);
        assertThat(slot1).isNotEqualTo(slot2);
        slot1.setId(null);
        assertThat(slot1).isNotEqualTo(slot2);
    }
}
