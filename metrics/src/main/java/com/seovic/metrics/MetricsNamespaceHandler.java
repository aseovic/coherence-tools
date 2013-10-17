package com.seovic.metrics;


import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.xml.AbstractNamespaceHandler;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;

import com.tangosol.run.xml.XmlElement;

import com.tangosol.util.ResourceRegistry;

import com.tangosol.util.extractor.ReflectionExtractor;

import java.io.File;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URI;

import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Defines the Coherence NamespaceHandler for the "metrics" namespace, that allows
 * the definition of metrics publication as part of a Coherence Cache Configuration.
 * <p/>
 * This implementation is based on the Code Hale's Metrics library, which can
 * be found at <a link="http://metrics.codahale.com">http://metrics.codahale.com</a>.
 * <p/>
 * The documentation for Metrics, especially the supported reporters can be
 * found at <a link="http://metrics.codahale.com/getting-started/">here</a>.
 * <p/>
 * To use the metrics namespace, it must be declared within a <cache-config>
 * element as follows:
 * <pre>
 *     &lt;cache-config xmlns:metrics="class://com.seovic.metrics.MetricsNamespaceHandler">
 * </pre>
 * <p>
 * Once defined, a single &lt;metrics:registry> element may be defined in the &lt;cache-config>,
 * with one or more &lt;reporter> child elements.
 * <p>
 * For Example:
 * <pre>
 *     &lt;metrics:registry>
 *         &lt;reporter>
 *             &lt;name>console | csv:outputDirectory | jmx:domain | slf4j | className&lt;/name>
 *             &lt;rateUnit>DAYS | HOURS | MINUTES | SECONDS (default) | MILLISECONDS | MICROSECONDS | NANOSECONDS&lt;/rateUnit> (optional)
 *             &lt;durationUnit>DAYS | HOURS | MINUTES | SECONDS | MILLISECONDS (default) | MICROSECONDS | NANOSECONDS&lt;/durationUnit> (optional)
 *             &lt;locale>en-US&lt;/locale> (optional)
 *             &lt;frequency>reporting frequency in seconds, defaults to 10&lt;/locale> (optional)
 *             &lt;filter>class name of MetricFilter implementation, defaults to all&lt;/filter> (optional)
 *         &lt;/reporter>
 *         ...
 *     &lt;/metrics:registry>
 * </pre>
 *
 * @author Aleksandar Seovic  2013.10.17
 */
public class MetricsNamespaceHandler
        extends AbstractNamespaceHandler
    {
    private static Logger LOG = LoggerFactory.getLogger(MetricsNamespaceHandler.class);

    @XmlSimpleName("registry")
    public static class RegistryProcessor
            implements ElementProcessor<Void>
        {
        @Override
        public Void process(ProcessingContext ctx, XmlElement xml)
                throws ConfigurationException
            {
            LOG.info("Creating Metrics Registry");
            ctx.getResourceRegistry().registerResource(MetricRegistry.class, new MetricRegistry());

            // process reporter definitions
            ctx.processElementsOf(xml);

            return null;
            }
        }

    @XmlSimpleName("reporter")
    public static class ReporterProcessor
            implements ElementProcessor<Void>
        {
        @Override
        public Void process(ProcessingContext ctx, XmlElement xml)
                throws ConfigurationException
            {
            String       name         = ctx.getMandatoryProperty("name", String.class, xml);
            TimeUnit     rateUnit     = ctx.getOptionalProperty("rateUnit", TimeUnit.class, TimeUnit.SECONDS, xml);
            TimeUnit     durationUnit = ctx.getOptionalProperty("durationUnit", TimeUnit.class, TimeUnit.MILLISECONDS, xml);
            Locale       locale       = ctx.getOptionalProperty("locale", Locale.class, Locale.getDefault(), xml);
            int          frequency    = ctx.getOptionalProperty("frequency", Integer.class, 10, xml);
            MetricFilter filter       = MetricFilter.ALL;

            if (xml.getElement("filter") != null)
                {
                ParameterizedBuilder<?> builder = ctx.getMandatoryProperty("filter", ParameterizedBuilder.class, xml);
                filter = (MetricFilter) builder.realize(ctx.getDefaultParameterResolver(), ctx.getContextClassLoader(), null);
                }

            ResourceRegistry resourceRegistry = ctx.getResourceRegistry();
            MetricRegistry   metricRegistry   = resourceRegistry.getResource(MetricRegistry.class);

            if (name.toLowerCase().startsWith("jmx"))
                {
                int n = name.indexOf(':');
                String domain = n > 0 ? name.substring(n+1) : "Metrics";

                JmxReporter reporter = JmxReporter.forRegistry(metricRegistry)
                        .inDomain(domain)
                        .convertDurationsTo(durationUnit)
                        .convertRatesTo(rateUnit)
                        .filter(filter)
                        .build();
                reporter.start();
                LOG.info("Started Metrics reporter: {} (domain={}, durationUnit={}, rateUnit={}, filter={}", new Object[] {reporter.getClass().getName(), domain, durationUnit, rateUnit, filter});
                }
            else if (name.toLowerCase().startsWith("csv"))
                {
                int n = name.indexOf(':');
                String outputDir = n > 0 ? name.substring(n+1) : "metrics";

                CsvReporter reporter = CsvReporter.forRegistry(metricRegistry)
                        .convertDurationsTo(durationUnit)
                        .convertRatesTo(rateUnit)
                        .filter(filter)
                        .build(new File(outputDir));
                reporter.start(frequency, TimeUnit.SECONDS);
                LOG.info("Started Metrics reporter: {} (outputDir={}, durationUnit={}, rateUnit={}, filter={}", new Object[] {reporter.getClass().getName(), outputDir, durationUnit, rateUnit, filter});
                }
            else
                {
                Class reporterClass = findReporterClass(name);
                Object builder = null;
                try
                    {
                    Method forRegistry = reporterClass.getDeclaredMethod("forRegistry", MetricRegistry.class);
                    builder = forRegistry.invoke(reporterClass, metricRegistry);
                    }
                catch (NoSuchMethodException e)
                    {
                    throw new IllegalArgumentException("Reporter class does not implement static forRegistry method");
                    }
                catch (InvocationTargetException | IllegalAccessException e)
                    {
                    throw new RuntimeException("Unable to create Builder for a reporter:" + name, e);
                    }
                invokeSafe(builder, "convertDurationsTo", durationUnit);
                invokeSafe(builder, "convertRatesTo", rateUnit);
                invokeSafe(builder, "filter", filter);
                invokeSafe(builder, "formattedFor", locale);

                Object reporter = invokeSafe(builder, "build");
                if (reporter instanceof ScheduledReporter)
                    {
                    invokeSafe(reporter, "start", frequency, TimeUnit.SECONDS);
                    }
                else
                    {
                    invokeSafe(reporter, "start");
                    }

                LOG.info("Started Metrics reporter: {} (durationUnit={}, rateUnit={}, filter={}, locale={}", new Object[] {reporter.getClass().getName(), durationUnit, rateUnit, filter, locale});
                }

            return null;
            }

        private Object invokeSafe(Object reporter, String methodName, Object... args)
            {
            try
                {
                ReflectionExtractor ex = new ReflectionExtractor(methodName, args);
                return ex.extract(reporter);
                }
            catch (Throwable ignore)
                {
                return null;
                }
            }

        private Class findReporterClass(String name)
            {
            try
                {
                String fullName = "com.codahale.metrics." + capitalize(name) + "Reporter";
                return getClass().getClassLoader().loadClass(fullName);
                }
            catch (Exception e)
                {
                try
                    {
                    return getClass().getClassLoader().loadClass(name);
                    }
                catch (Exception e1)
                    {
                    throw new IllegalArgumentException("Invalid reporter class: " + name, e1);
                    }
                }
            }

        private String capitalize(String name)
            {
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            }

        }
    }
