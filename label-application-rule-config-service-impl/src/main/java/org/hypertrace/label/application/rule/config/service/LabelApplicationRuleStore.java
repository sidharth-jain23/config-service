package org.hypertrace.label.application.rule.config.service;

import com.google.protobuf.Value;
import java.util.Optional;
import lombok.SneakyThrows;
import org.hypertrace.config.objectstore.IdentifiedObjectStoreWithFilter;
import org.hypertrace.config.proto.converter.ConfigProtoConverter;
import org.hypertrace.config.service.change.event.api.ConfigChangeEventGenerator;
import org.hypertrace.config.service.v1.ConfigServiceGrpc;
import org.hypertrace.label.application.rule.config.service.v1.GetLabelApplicationRuleFilter;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRule;

class LabelApplicationRuleStore
    extends IdentifiedObjectStoreWithFilter<LabelApplicationRule, GetLabelApplicationRuleFilter> {
  private static final String LABEL_APPLICATION_RULE_CONFIG_RESOURCE_NAME =
      "label-application-rule-config";
  private static final String LABEL_APPLICATION_RULE_CONFIG_RESOURCE_NAMESPACE = "labels";

  LabelApplicationRuleStore(
      ConfigServiceGrpc.ConfigServiceBlockingStub stub,
      ConfigChangeEventGenerator configChangeEventGenerator) {
    super(
        stub,
        LABEL_APPLICATION_RULE_CONFIG_RESOURCE_NAMESPACE,
        LABEL_APPLICATION_RULE_CONFIG_RESOURCE_NAME,
        configChangeEventGenerator);
  }

  @Override
  protected Optional<LabelApplicationRule> filterConfigData(
      LabelApplicationRule data, GetLabelApplicationRuleFilter filter) {
    return Optional.of(data)
        .filter(
            rule ->
                filter.getIdsList().isEmpty()
                    || filter.getIdsList().stream().anyMatch(id -> id.equals(rule.getId())));
  }

  @Override
  protected Optional<LabelApplicationRule> buildDataFromValue(Value value) {
    try {
      LabelApplicationRule.Builder builder = LabelApplicationRule.newBuilder();
      ConfigProtoConverter.mergeFromValue(value, builder);
      return Optional.of(builder.build());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @SneakyThrows
  @Override
  protected Value buildValueFromData(LabelApplicationRule object) {
    return ConfigProtoConverter.convertToValue(object);
  }

  @Override
  protected String getContextFromData(LabelApplicationRule object) {
    return object.getId();
  }
}
