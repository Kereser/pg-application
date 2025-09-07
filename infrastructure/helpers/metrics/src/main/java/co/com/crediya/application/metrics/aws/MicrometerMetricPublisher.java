package co.com.crediya.application.metrics.aws;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricPublisher;

@Component
@AllArgsConstructor
public class MicrometerMetricPublisher implements MetricPublisher {
  private final ExecutorService service = Executors.newFixedThreadPool(10);
  private final MeterRegistry registry;

  @Override
  public void publish(MetricCollection metricCollection) {
    service.submit(
        () -> {
          List<Tag> tags = buildTags(metricCollection);
          metricCollection.stream()
              .filter(
                  metricRecord ->
                      metricRecord.value() instanceof Duration
                          || metricRecord.value() instanceof Integer)
              .forEach(
                  metricRecord -> {
                    if (metricRecord.value() instanceof Duration) {
                      registry
                          .timer(metricRecord.metric().name(), tags)
                          .record((Duration) metricRecord.value());
                    } else if (metricRecord.value() instanceof Integer) {
                      registry
                          .counter(metricRecord.metric().name(), tags)
                          .increment((Integer) metricRecord.value());
                    }
                  });
        });
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException();
  }

  private List<Tag> buildTags(MetricCollection metricCollection) {
    return metricCollection.stream()
        .filter(
            metricRecord ->
                metricRecord.value() instanceof String || metricRecord.value() instanceof Boolean)
        .map(metricRecord -> Tag.of(metricRecord.metric().name(), metricRecord.value().toString()))
        .toList();
  }
}
