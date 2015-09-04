package org.drugis.trialverse.search.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.drugis.trialverse.dataset.model.VersionMapping;
import org.drugis.trialverse.dataset.repository.DatasetReadRepository;
import org.drugis.trialverse.dataset.repository.VersionMappingRepository;
import org.drugis.trialverse.search.model.SearchResult;
import org.drugis.trialverse.search.service.impl.SearchServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by connor on 9/4/15.
 */
public class SearchServiceTest {

  @Mock
  VersionMappingRepository versionMappingRepository;

  @Mock
  DatasetReadRepository datasetReadRepository;

  @InjectMocks
  SearchService searchService;

  @Before
  public void init() {
    searchService = new SearchServiceImpl();
    initMocks(this);
  }

  @Test
  public void testSearchStudy() throws URISyntaxException, IOException, ParseException {
    String trialverseDatasetUrl = "trialverseDatasetUrl";
    String versiondDatasetUrl = "versiondDatasetUrl";
    VersionMapping versionMapping = new VersionMapping(versiondDatasetUrl, "ovnerUid", trialverseDatasetUrl);
    List<VersionMapping> versionMappings = Arrays.asList(versionMapping);
    when(versionMappingRepository.getVersionMappings()).thenReturn(versionMappings);

    String searchQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX ontology: <http://trials.drugis.org/ontology#>\n" +
            "\n" +
            "SELECT * WHERE {\n" +
            "  graph ?graph {\n" +
            "    {\n" +
            "      ?study\n" +
            "        a ontology:Study ;\n" +
            "        rdfs:label ?label .\n" +
            "      FILTER regex(?label, \"my term\", \"i\") .\n" +
            "    } UNION {\n" +
            "      ?study\n" +
            "        a ontology:Study ;\n" +
            "        rdfs:comment ?comment\n" +
            "      FILTER regex(?comment, \"my term\", \"i\")\n" +
            "    }\n" +
            "  }\n" +
            "}";
    InputStream inputStream = new ClassPathResource("mockSearchResults.json").getInputStream();
    JSONObject result = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(inputStream);


    when(datasetReadRepository.executeHeadQuery(searchQuery, versionMapping)).thenReturn(result);

    searchService.searchStudy("my term");

  }



}
