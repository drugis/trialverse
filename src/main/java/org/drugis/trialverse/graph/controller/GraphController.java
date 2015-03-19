package org.drugis.trialverse.graph.controller;

import org.apache.http.HttpResponse;
import org.apache.jena.riot.RDFLanguages;
import org.drugis.trialverse.dataset.repository.DatasetReadRepository;
import org.drugis.trialverse.exception.MethodNotAllowedException;
import org.drugis.trialverse.graph.repository.GraphReadRepository;
import org.drugis.trialverse.graph.repository.GraphWriteRepository;
import org.drugis.trialverse.util.Namespaces;
import org.drugis.trialverse.util.controller.AbstractTrialverseController;
import org.drugis.trialverse.util.service.TrialverseIOUtilsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

/**
 * Created by daan on 19-11-14.
 */
@Controller
@RequestMapping(value = "/datasets/{datasetUuid}/graphs")
public class GraphController extends AbstractTrialverseController {

  @Inject
  private GraphReadRepository graphReadRepository;

  @Inject
  private GraphWriteRepository graphWriteRepository;

  @Inject
  private DatasetReadRepository datasetReadRepository;

  @Inject
  private TrialverseIOUtilsService trialverseIOUtilsService;


  @RequestMapping(value = "/{graphUuid}", method = RequestMethod.GET)
  @ResponseBody
  public void getStudy(HttpServletResponse httpServletResponse, @PathVariable String datasetUuid , @PathVariable String graphUuid) throws URISyntaxException, IOException {
    HttpResponse response = graphReadRepository.getStudy(new URI(Namespaces.DATASET_NAMESPACE + datasetUuid), graphUuid);
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    httpServletResponse.setHeader("Content-Type", RDFLanguages.TURTLE.getContentType().getContentType());
    trialverseIOUtilsService.writeResponseContentToServletResponse(response, httpServletResponse);
  }


  @RequestMapping(value = "/{graphUuid}", method = RequestMethod.PUT)
  public void setStudy(HttpServletRequest request, HttpServletResponse response, Principal currentUser,
                     @PathVariable String datasetUuid, @PathVariable String graphUuid)
          throws IOException, MethodNotAllowedException, URISyntaxException {
    URI trialverseDatasetUri = new URI(Namespaces.DATASET_NAMESPACE + datasetUuid);
    if (datasetReadRepository.isOwner(trialverseDatasetUri, currentUser)) {
      graphWriteRepository.updateStudy(new URI(Namespaces.DATASET_NAMESPACE + datasetUuid), graphUuid, request.getInputStream());
      response.setStatus(HttpStatus.OK.value());
    } else {
      throw new MethodNotAllowedException();
    }


  }



}