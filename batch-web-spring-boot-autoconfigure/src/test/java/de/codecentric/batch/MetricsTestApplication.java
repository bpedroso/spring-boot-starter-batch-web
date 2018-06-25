/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.batch;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.codecentric.batch.metrics.ListenerMetricsAspect;
import de.codecentric.batch.metrics.MetricsOutputFormatter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Application for integration testing.
 *
 * @author Tobias Flohre
 */
@Configuration
@EnableAutoConfiguration
@Import(TestListenerConfiguration.class)
public class MetricsTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetricsTestApplication.class, args);
	}

	@Autowired
	private MeterRegistry meterRegistry;

	@Bean
	public ListenerMetricsAspect listenerMetricsAspect() {
		return new ListenerMetricsAspect(meterRegistry);
	}

	@Bean
	public MetricsOutputFormatter metricsOutputFormatter() {
		return new MetricsOutputFormatter() {

			@Override
			public String format(Collection<Gauge> gauges, Collection<Timer> timers) {
				StringBuilder builder = new StringBuilder(
						"\n########## Personal Header for metrics! #####\n########## Metrics Start ##########\n");
				gauges.stream().forEach(gauge -> {
					builder.append("Gauge [" + gauge.getId() + "]: ");
					builder.append(gauge.value() + "\n");
				});
				timers.stream().forEach(timer -> {
					builder.append("Timer [" + timer.getId() + "]: ");
					builder.append(
							"totalTime=" + timer.totalTime(timer.baseTimeUnit()) + " " + timer.baseTimeUnit() + "\n");
				});
				builder.append("########## Metrics End ############");
				return builder.toString();
			}

		};
	}

}
