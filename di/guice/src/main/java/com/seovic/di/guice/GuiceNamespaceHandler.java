package com.seovic.di.guice;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.xml.AbstractNamespaceHandler;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;

import com.tangosol.run.xml.XmlElement;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Defines the Coherence NamespaceHandler for the "guice" namespace, that allows
 * the definition of Guice injector as part of a Coherence Cache Configuration.
 * <p/>
 * The documentation for Guice can be found <a link="http://code.google.com/p/google-guice/">here</a>.
 * <p/>
 * To use the guice namespace, it must be declared within a &lt;cache-config>
 * element as follows:
 * <pre>
 *     &lt;cache-config xmlns:guice="class://com.seovic.di.guice.GuiceNamespaceHandler">
 * </pre>
 * <p>
 * Once defined, a single &lt;guice:injector> element may be defined in the &lt;cache-config>,
 * with one or more &lt;module> child elements.
 * <p>
 * For Example:
 * <pre>
 *     &lt;guice:injector>
 *         &lt;module>com.myproject.MyModule1&lt;/module>
 *         &lt;module>com.myproject.MyModule2&lt;/module>
 *         &lt;module>com.myproject.MyModule3&lt;/module>
 *         ...
 *     &lt;/guice:injector>
 * </pre>
 *
 * @author Aleksandar Seovic  2013.10.17
 */
@SuppressWarnings("UnusedDeclaration")
public class GuiceNamespaceHandler
        extends AbstractNamespaceHandler
    {
    private static Logger LOG = LoggerFactory.getLogger(GuiceNamespaceHandler.class);

    @SuppressWarnings("unchecked")
    @XmlSimpleName("injector")
    public static class RegistryProcessor
            implements ElementProcessor<Void>
        {
        @Override
        public Void process(ProcessingContext ctx, XmlElement xml)
                throws ConfigurationException
            {
            LOG.info("Creating Guice Injector");

            Set<Module> modules = new LinkedHashSet<>();
            Iterator<XmlElement> it = xml.getElements("module");
            while (it.hasNext())
                {
                modules.add(instantiateModule(it.next().getString()));
                }

            ctx.getResourceRegistry()
                    .registerResource(Injector.class, Guice.createInjector(modules));

            return null;
            }

        private Module instantiateModule(String moduleClass)
                throws ConfigurationException
            {
            try
                {
                LOG.info("Registering Guice module " + moduleClass);
                return (Module) getClass().getClassLoader().loadClass(moduleClass).newInstance();
                }
            catch (Exception e)
                {
                throw new ConfigurationException("Unable to load Guice module " + moduleClass,
                         "Please make sure that the specified class exists in the classpath", e);
                }
            }
        }
    }
