package de.captaingoldfish.scim.sdk.keycloak.scim;

import de.captaingoldfish.scim.sdk.common.constants.HttpHeader;
import de.captaingoldfish.scim.sdk.common.constants.enums.HttpMethod;
import de.captaingoldfish.scim.sdk.common.exceptions.InternalServerException;
import de.captaingoldfish.scim.sdk.common.response.ScimResponse;
import de.captaingoldfish.scim.sdk.keycloak.auth.ExtScimAuthorization;
import de.captaingoldfish.scim.sdk.keycloak.constants.ContextPaths;
import de.captaingoldfish.scim.sdk.keycloak.provider.ConfigurationProperties;
import de.captaingoldfish.scim.sdk.keycloak.services.ScimServiceProviderService;
import de.captaingoldfish.scim.sdk.server.endpoints.ResourceEndpoint;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;

/**
 * author Pascal Knueppel <br>
 * created at: 04.02.2020 <br>
 * <br>
 */
@Slf4j
public class ScimEndpoint extends AbstractEndpoint {

  /**
   * @param authentication used as constructor param to pass a mockito mock during unit testing
   */
  public ScimEndpoint(KeycloakSession keycloakSession) {
    super(keycloakSession);
  }

  /**
   * handles all SCIM requests
   *
   * @return the jax-rs response
   */
  @POST
  @GET
  @PUT
  @PATCH
  @DELETE
  @Path("{id}" + ContextPaths.SCIM_ENDPOINT_PATH + "/{s:.*}")
  @Produces(HttpHeader.SCIM_CONTENT_TYPE)
  public Response handleScimRequest(@PathParam("id") String id, String requestBody) {
    ComponentModel model = getKeycloakSession().getContext().getRealm().getComponent(id);
    if (model == null) {
      throw new NotFoundException(id + " unknown");
    }
    ConfigurationProperties config = new ConfigurationProperties(model);
    if (!config.isEnabled()) {
      throw new NotFoundException(id + " is currently disabled");
    }

    ScimServiceProviderService scimServiceProviderService =
        new ScimServiceProviderService(getKeycloakSession(), model);
    ResourceEndpoint resourceEndpoint = ScimConfiguration.getScimEndpoint(getKeycloakSession(), id);

    ExtScimAuthorization scimAuthorization =
        new ExtScimAuthorization(getKeycloakSession(), id, model);
    ScimKeycloakContext scimKeycloakContext =
        new ScimKeycloakContext(getKeycloakSession(), scimAuthorization);
    KeycloakSession keycloakSession = getKeycloakSession();

    final String url = keycloakSession.getContext().getUri().getAbsolutePath().toString();
    String query = getQuery(keycloakSession.getContext().getUri().getQueryParameters());
    final HttpRequest request = keycloakSession.getContext().getContextObject(HttpRequest.class);
    ScimResponse scimResponse =
        resourceEndpoint.handleRequest(
            url + query,
            HttpMethod.valueOf(request.getHttpMethod()),
            requestBody,
            getHttpHeaders(request),
            null,
            commitOrRollback(),
            scimKeycloakContext);
    return scimResponse.buildJakartaResponse();
  }

  private String getQuery(MultivaluedMap<String, String> queryParameters) {
    if (queryParameters == null || queryParameters.isEmpty()) {
      return "";
    }
    return "?"
        + queryParameters.entrySet().stream()
            .map(
                entry -> {
                  return String.format("%s=%s", entry.getKey(), String.join(",", entry.getValue()));
                })
            .collect(Collectors.joining("&"));
  }

  /** commit or rollback the transaction */
  private BiConsumer<ScimResponse, Boolean> commitOrRollback() {
    return (scimResponse, isError) -> {
      try {
        if (isError) {
          // if the request has failed roll the transaction back
          getKeycloakSession().getTransactionManager().setRollbackOnly();
        } else {
          // if the request succeeded commit the transaction
          getKeycloakSession().getTransactionManager().commit();
        }
      } catch (Exception ex) {
        throw new InternalServerException(ex.getMessage());
      }
    };
  }

  /**
   * extracts the http headers from the request and puts them into a map
   *
   * @param httpRequest the current request object
   * @return a map with the http-headers
   */
  public Map<String, String> getHttpHeaders(HttpRequest httpRequest) {
    Map<String, String> httpHeaders = new HashMap<>();

    httpRequest
        .getHttpHeaders()
        .getRequestHeaders()
        .forEach(
            (headerName, value) -> {
              String headerValue = value.get(0);

              boolean isContentTypeHeader =
                  HttpHeader.CONTENT_TYPE_HEADER
                      .toLowerCase(Locale.ROOT)
                      .equals(headerName.toLowerCase(Locale.ROOT));
              boolean isApplicationJson =
                  StringUtils.startsWithIgnoreCase(headerValue, "application/json");
              if (isContentTypeHeader && isApplicationJson) {
                headerValue = HttpHeader.SCIM_CONTENT_TYPE;
              }
              httpHeaders.put(headerName, headerValue);
            });
    return httpHeaders;
  }
}
