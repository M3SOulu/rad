package edu.baylor.ecs.cloudhubs.rad.context;

import edu.baylor.ecs.cloudhubs.rad.model.RestEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * This class wraps the resource path with a list of {@link edu.baylor.ecs.cloudhubs.rad.model.RestEntity}
 * resulted after performing the REST API discovery analysis for that specific resource.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
public class SeerRestEntityContext {
    private String resourcePath;
    private List<RestEntity> restEntities = new ArrayList<>();
}
