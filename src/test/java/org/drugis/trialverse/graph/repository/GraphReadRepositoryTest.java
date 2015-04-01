package org.drugis.trialverse.graph.repository;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicStatusLine;
import org.drugis.trialverse.dataset.model.VersionMapping;
import org.drugis.trialverse.dataset.repository.VersionMappingRepository;
import org.drugis.trialverse.exception.ReadGraphException;
import org.drugis.trialverse.graph.repository.impl.GraphReadRepositoryImpl;
import org.drugis.trialverse.util.Namespaces;
import org.drugis.trialverse.util.WebConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by connor on 28-11-14.
 */
public class GraphReadRepositoryTest {

  @Mock
  WebConstants webConstants;

  @Mock
  HttpClient httpClient;

  @Mock
  VersionMappingRepository versionMappingRepository;

  @InjectMocks
  GraphReadRepository graphReadRepository;

  @Before
  public void init() throws IOException {
    graphReadRepository = new GraphReadRepositoryImpl();
    MockitoAnnotations.initMocks(this);

    when(webConstants.getTriplestoreBaseUri()).thenReturn("baseUri");
  }

  @Test
  public void testGetGraph() throws IOException, URISyntaxException, ReadGraphException {
    String datasetUUID = "datasetUUID";
    String graphUUID = "uuid";
    URI trialverseDatasetUri = new URI(Namespaces.DATASET_NAMESPACE + datasetUUID);

    VersionMapping mapping = new VersionMapping("http://versionedDatsetUrl", "ownerUuid", trialverseDatasetUri.toString());
    when(versionMappingRepository.getVersionMappingByDatasetUrl(trialverseDatasetUri)).thenReturn(mapping);
    HttpResponse mockResponse = mock(CloseableHttpResponse.class);
    org.apache.http.HttpEntity entity = mock(org.apache.http.HttpEntity.class);
    when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, org.apache.http.HttpStatus.SC_OK, "FINE!"));
    String responceString = "check me out";
    when(entity.getContent()).thenReturn(IOUtils.toInputStream(responceString));
    when(mockResponse.getEntity()).thenReturn(entity);
    when(httpClient.execute(any(HttpPut.class))).thenReturn(mockResponse);
    graphReadRepository.getGraph(trialverseDatasetUri, graphUUID);

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(mapping.getVersionedDatasetUrl())
            .path("/data")
            .queryParam("graph", Namespaces.GRAPH_NAMESPACE + graphUUID)
            .build();

    HttpGet request = new HttpGet(uriComponents.toUri());
    verify(httpClient).execute(any(HttpGet.class));

  }
}
