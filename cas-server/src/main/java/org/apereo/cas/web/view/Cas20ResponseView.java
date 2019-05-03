package org.apereo.cas.web.view;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.CasViewConstants;
import org.apereo.cas.authentication.AuthenticationAttributeReleasePolicy;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.ProtocolAttributeEncoder;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.web.view.AbstractDelegatingCasView;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.validation.CasProtocolAttributesRenderer;
import org.apereo.cas.web.view.attributes.DefaultCas30ProtocolAttributesRenderer;
import org.apereo.cas.web.view.attributes.InlinedCas30ProtocolAttributesRenderer;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 参照Cas30ResponseView重写Cas20ResponseView，以便能够向protocol2.0的客户端返回属性。
 * 在构造方法中手动设置以下两个属性
 * this.releaseProtocolAttributes = true;
 * this.attributesRenderer = new DefaultCas30ProtocolAttributesRenderer();
 *
 * @author duqian
 * @date 2019/5/3
 */
@Slf4j
public class Cas20ResponseView extends AbstractDelegatingCasView {
    /**
     * The Service selection strategy.
     */
    protected final AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies;
    private final CasProtocolAttributesRenderer attributesRenderer;
    private final boolean releaseProtocolAttributes;

    public Cas20ResponseView(final boolean successResponse,
                             final ProtocolAttributeEncoder protocolAttributeEncoder,
                             final ServicesManager servicesManager,
                             final String authenticationContextAttribute,
                             final View view,
                             final AuthenticationAttributeReleasePolicy authenticationAttributeReleasePolicy,
                             final AuthenticationServiceSelectionPlan serviceSelectionStrategy) {
        super(successResponse, protocolAttributeEncoder, servicesManager, authenticationContextAttribute, view, authenticationAttributeReleasePolicy);
        this.authenticationRequestServiceSelectionStrategies = serviceSelectionStrategy;
        this.releaseProtocolAttributes = true;
        this.attributesRenderer = new DefaultCas30ProtocolAttributesRenderer();
    }

    @Override
    protected void prepareMergedOutputModel(final Map<String, Object> model, final HttpServletRequest request,
                                            final HttpServletResponse response) throws Exception {
        super.putIntoModel(model, CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL, getPrincipal(model));
        super.putIntoModel(model, CasViewConstants.MODEL_ATTRIBUTE_NAME_CHAINED_AUTHENTICATIONS, getChainedAuthentications(model));
        super.putIntoModel(model, CasViewConstants.MODEL_ATTRIBUTE_NAME_PRIMARY_AUTHENTICATION, getPrimaryAuthenticationFrom(model));
        LOGGER.debug("Prepared CAS response output model with attribute names [{}]", model.keySet());

        final Service service = authenticationRequestServiceSelectionStrategies.resolveService(getServiceFrom(model));
        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

        final Map<String, Object> principalAttributes = getCasPrincipalAttributes(model, registeredService);
        final Map<String, Object> attributes = new HashMap<>(principalAttributes);

        LOGGER.debug("Processed principal attributes from the output model to be [{}]", principalAttributes.keySet());
        if (this.releaseProtocolAttributes) {
            LOGGER.debug("CAS is configured to release protocol-level attributes. Processing...");
            final Map<String, Object> protocolAttributes = getCasProtocolAuthenticationAttributes(model, registeredService);
            attributes.putAll(protocolAttributes);
            LOGGER.debug("Processed protocol/authentication attributes from the output model to be [{}]", protocolAttributes.keySet());
        }

        decideIfCredentialPasswordShouldBeReleasedAsAttribute(attributes, model, registeredService);
        decideIfProxyGrantingTicketShouldBeReleasedAsAttribute(attributes, model, registeredService);

        LOGGER.debug("Final collection of attributes for the response are [{}].", attributes.keySet());
        putCasResponseAttributesIntoModel(model, attributes, registeredService);
    }

    /**
     * Put cas authentication attributes into model.
     *
     * @param model             the model
     * @param registeredService the registered service
     * @return the cas authentication attributes
     */
    protected Map<String, Object> getCasProtocolAuthenticationAttributes(final Map<String, Object> model,
                                                                         final RegisteredService registeredService) {

        if (!registeredService.getAttributeReleasePolicy().isAuthorizedToReleaseAuthenticationAttributes()) {
            LOGGER.debug("Attribute release policy for service [{}] is configured to never release any attributes", registeredService);
            return new LinkedHashMap<>(0);
        }

        final Map<String, Object> filteredAuthenticationAttributes = authenticationAttributeReleasePolicy
                .getAuthenticationAttributesForRelease(getPrimaryAuthenticationFrom(model));

        filterCasProtocolAttributes(model, filteredAuthenticationAttributes);

        final String contextProvider = getSatisfiedMultifactorAuthenticationProviderId(model);
        if (StringUtils.isNotBlank(contextProvider) && StringUtils.isNotBlank(authenticationContextAttribute)) {
            filteredAuthenticationAttributes.put(this.authenticationContextAttribute, CollectionUtils.wrap(contextProvider));
        }

        return filteredAuthenticationAttributes;
    }

    private void filterCasProtocolAttributes(final Map<String, Object> model, final Map<String, Object> filteredAuthenticationAttributes) {
        filteredAuthenticationAttributes.put(CasProtocolConstants.VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_AUTHENTICATION_DATE,
                CollectionUtils.wrap(getAuthenticationDate(model)));
        filteredAuthenticationAttributes.put(CasProtocolConstants.VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_FROM_NEW_LOGIN,
                CollectionUtils.wrap(isAssertionBackedByNewLogin(model)));
        filteredAuthenticationAttributes.put(CasProtocolConstants.VALIDATION_REMEMBER_ME_ATTRIBUTE_NAME,
                CollectionUtils.wrap(isRememberMeAuthentication(model)));
    }

    /**
     * Put cas principal attributes into model.
     *
     * @param model             the model
     * @param registeredService the registered service
     * @return the cas principal attributes
     */
    protected Map<String, Object> getCasPrincipalAttributes(final Map<String, Object> model, final RegisteredService registeredService) {
        return super.getPrincipalAttributesAsMultiValuedAttributes(model);
    }

    /**
     * Put cas response attributes into model.
     *
     * @param model             the model
     * @param attributes        the attributes
     * @param registeredService the registered service
     */
    protected void putCasResponseAttributesIntoModel(final Map<String, Object> model,
                                                     final Map<String, Object> attributes,
                                                     final RegisteredService registeredService) {

        LOGGER.debug("Beginning to encode attributes for the response");
        final Map<String, Object> encodedAttributes = this.protocolAttributeEncoder.encodeAttributes(attributes, registeredService);

        LOGGER.debug("Encoded attributes for the response are [{}]", encodedAttributes);
        super.putIntoModel(model, CasProtocolConstants.VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_ATTRIBUTES, encodedAttributes);

        final Collection<String> formattedAttributes = this.attributesRenderer.render(encodedAttributes);
        super.putIntoModel(model, CasProtocolConstants.VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_FORMATTED_ATTRIBUTES, formattedAttributes);
    }
}
