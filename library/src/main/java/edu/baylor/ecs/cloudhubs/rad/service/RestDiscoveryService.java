package edu.baylor.ecs.cloudhubs.rad.service;

import edu.baylor.ecs.cloudhubs.rad.context.RequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.ResponseContext;
import edu.baylor.ecs.cloudhubs.rad.context.RestEntityContext;
import edu.baylor.ecs.cloudhubs.rad.context.RestFlowContext;
import edu.baylor.ecs.cloudhubs.rad.graph.GVGenerator;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * This class constructs a {@link ResponseContext}.
 * It takes a {@link RequestContext} as input.
 *
 * @author Dipta Das
 */

@AllArgsConstructor
@Service
public class RestDiscoveryService {
    private final ResourceService resourceService;
    private final RestEntityService restEntityService;
    private final RestFlowService restFlowService;

    public RestDiscoveryService() {
        this.resourceService = new ResourceService(new DefaultResourceLoader());
        this.restEntityService = new RestEntityService();
        this.restFlowService = new RestFlowService();
    }

    public ResponseContext generateResponseContext(RequestContext request) {
        ResponseContext responseContext = new ResponseContext();
        responseContext.setRequest(request);

        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());
        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, request.getOrganizationPath());

            Set<Properties> propertiesSet = resourceService.getProperties(path, request.getOrganizationPath());
            Properties properties;
            if (propertiesSet.size() > 0) {
                properties = propertiesSet.iterator().next();
            } else properties = null;

            // print the properties for debug
            // Helper.dumpProperties(properties, path);

            RestEntityContext restEntityContext = restEntityService.getRestEntityContext(ctClasses, path, properties);
            responseContext.getRestEntityContexts().add(restEntityContext);
        }

        RestFlowContext restFlowContext = restFlowService.getRestFlowContext(responseContext.getRestEntityContexts());
        responseContext.setRestFlowContext(restFlowContext);

        if (request.getOutputPath() != null) {
            GVGenerator.generate(responseContext);
        }

        return responseContext;
    }
}
