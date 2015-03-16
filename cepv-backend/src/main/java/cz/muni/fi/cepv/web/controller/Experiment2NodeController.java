package cz.muni.fi.cepv.web.controller;

import cz.muni.fi.cepv.model.Experiment;
import cz.muni.fi.cepv.model.Experiment2Node;
import cz.muni.fi.cepv.model.Node;
import cz.muni.fi.cepv.repository.Experiment2NodeRepository;
import cz.muni.fi.cepv.repository.ExperimentRepository;
import cz.muni.fi.cepv.repository.NodeRepository;
import cz.muni.fi.cepv.repository.querydsl.Experiment2NodeQueryDsl;
import cz.muni.fi.cepv.web.LinkUtil;
import cz.muni.fi.cepv.web.resoureceassambler.Experiment2NodeResourceAssembler;
import cz.muni.fi.cepv.web.to.Experiment2NodeTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author xgarcar
 */
@RestController
@RequestMapping(produces = "application/hal+json")
public class Experiment2NodeController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private Experiment2NodeRepository experiment2NodeRepository;

    @Autowired
    private Experiment2NodeResourceAssembler experiment2NodeResourceAssembler;

    @RequestMapping(value = LinkUtil.EXPERIMENT2NODES, method = RequestMethod.GET)
    public HttpEntity<PagedResources<Resource<Experiment2Node>>> getExperiment2NodeRelations(final Pageable pageable,
                                                                                             final PagedResourcesAssembler<Experiment2Node> assembler) {

        final Page<Experiment2Node> experiment2NodePageResources =  experiment2NodeRepository.findAll(pageable);

        return new ResponseEntity<>(assembler.toResource(experiment2NodePageResources, experiment2NodeResourceAssembler), HttpStatus.OK);
    }

    @RequestMapping(value = LinkUtil.EXPERIMENT2NODE, method = RequestMethod.GET)
    public HttpEntity<Resource<Experiment2Node>> getExperiment2NodeRelation(@PathVariable Long experiment2NodeId) {

        final Experiment2Node experiment2Node =  experiment2NodeRepository.findOne(experiment2NodeId);
        return new ResponseEntity<>(experiment2NodeResourceAssembler.toResource(experiment2Node), HttpStatus.OK);
    }

    @RequestMapping(value = LinkUtil.EXPERIMENT_EXPERIMENT2NODES, method = RequestMethod.GET)
    public HttpEntity<PagedResources<Resource<Experiment2Node>>> getExperiment2NodeRelations(
            @PathVariable final Long experimentId, final Pageable pageable, final PagedResourcesAssembler<Experiment2Node> assembler) {

        final Page<Experiment2Node> experiment2NodePageResources =  experiment2NodeRepository.
                findAll(Experiment2NodeQueryDsl.isRelatedToExperiment(experimentId), pageable);

        return new ResponseEntity<>(assembler.toResource(experiment2NodePageResources, experiment2NodeResourceAssembler), HttpStatus.OK);
    }

    @RequestMapping(value = LinkUtil.EXPERIMENT_EXPERIMENT2NODES_FILTER, method = RequestMethod.GET)
    public HttpEntity<PagedResources<Resource<Experiment2Node>>> getFilteredExperiment2NodeRelations(
            @PathVariable final Long experimentId,
            @RequestParam(required = false) final String nodeName, @RequestParam(required = false) final String nodeDescription,
            @RequestParam(required = false) final Date gtAdditionTime, @RequestParam(required = false) final Date ltAdditionTime,
            @RequestParam(required = false) final Date gtRemovalTime, @RequestParam(required = false) final Date ltRemovalTime,
            final Pageable pageable, final PagedResourcesAssembler<Experiment2Node> assembler) {

        final Page<Experiment2Node> experiment2NodePageResources =  experiment2NodeRepository.
                findAll(Experiment2NodeQueryDsl.filter(experimentId, nodeName, nodeDescription, gtAdditionTime, ltAdditionTime, gtRemovalTime, ltRemovalTime), pageable);

        final Link link = new Link(new UriTemplate(linkTo(methodOn(Experiment2NodeController.class).
                getFilteredExperiment2NodeRelations(experimentId, nodeName, nodeDescription, gtAdditionTime, ltAdditionTime, gtRemovalTime, ltRemovalTime, pageable, assembler)).
                toUriComponentsBuilder().build().toUriString(), LinkUtil.getFilteredExperiment2NodeRelationsTemplateVariables()), "self");

        final PagedResources<Resource<Experiment2Node>> pagedExperimentResources = assembler.toResource(experiment2NodePageResources, experiment2NodeResourceAssembler, link);
        return new ResponseEntity<>(pagedExperimentResources, HttpStatus.OK);
    }

    @RequestMapping(value = LinkUtil.EXPERIMENT_EXPERIMENT2NODES, method = RequestMethod.POST)
    public HttpEntity<Void> createExperiment2NodeRelation(@PathVariable final Long experimentId, @RequestBody final Experiment2NodeTO experiment2NodeTO) {

        final Experiment experiment = experimentRepository.findOne(experimentId);
        final Node node = nodeRepository.findByExternalId(experiment2NodeTO.getNode());
        final Experiment2Node experiment2Node = new Experiment2Node(experiment, node, experiment2NodeTO.getAdditionTime(), experiment2NodeTO.getRemovalTime());

        final Experiment2Node savedExperiment2Node = experiment2NodeRepository.save(experiment2Node);

        Resource<Experiment2Node> resource = experiment2NodeResourceAssembler.toResource(savedExperiment2Node);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(resource.getLink("self").getHref()));

        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = LinkUtil.EXPERIMENT_EXPERIMENT2NODE, method = RequestMethod.PATCH)
    public HttpEntity<Void> updateExperiment2NodeRelation(@PathVariable final Long experiment2NodeId, @RequestBody final Experiment2NodeTO experiment2NodeTO) {

        final Experiment2Node originalExperiment2Node = experiment2NodeRepository.findOne(experiment2NodeId);
        if (experiment2NodeTO.getAdditionTime() != null) {
            originalExperiment2Node.setAdditionTime(experiment2NodeTO.getAdditionTime());
        }

        if (experiment2NodeTO.getRemovalTime() != null) {
            originalExperiment2Node.setRemovalTime(experiment2NodeTO.getRemovalTime());
        }

        final Experiment2Node savedExperiment2Node = experiment2NodeRepository.save(originalExperiment2Node);

        Resource<Experiment2Node> resource = experiment2NodeResourceAssembler.toResource(savedExperiment2Node);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(resource.getLink("self").getHref()));

        return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    }
}