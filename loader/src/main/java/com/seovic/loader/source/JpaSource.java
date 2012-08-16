/**
 *
 */
package com.seovic.loader.source;


import com.seovic.core.Defaults;
import com.seovic.core.Extractor;
import com.seovic.loader.Source;
import java.util.Iterator;
import java.util.LinkedList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


/**
 * A {@link Source} implementation that reads items
 * to load using JPA.
 *
 * @author Ivan Cikic 2009.11.26
 */
public class JpaSource
        extends AbstractBaseSource
    {

    // ---- constructors ----------------------------------------------------

    /**
     * Construct JpaSource instance.
     *
     * @param persistenceUnitName the name of the persistence unit
     * @param entityClass         the class of entity
     */
    public JpaSource(String persistenceUnitName, Class entityClass)
        {
        m_persistenceUnitName = persistenceUnitName;
        m_entityClass         = entityClass;
        }


    // --- Source implementation --------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginExport()
        {
        if (m_emf == null)
            {
            m_emf = Persistence.createEntityManagerFactory(m_persistenceUnitName);
            }
        m_em = m_emf.createEntityManager();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endExport()
        {
        if (m_em != null && m_em.isOpen())
            {
            m_em.close();
            }
        if (m_emf != null)
            {
            m_emf.close();
            }
        }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Extractor createDefaultExtractor(String propertyName)
        {
        return Defaults.createExtractor(propertyName);
        }


    // ---- Iterable implementation -----------------------------------------

    /**
     * Return an iterator that iterates over this source.
     *
     * @return a source iterator
     *
     * @see JpaIterator
     */
    public Iterator iterator()
        {
        Query readAll = m_em.createQuery(
                createReadAllQuery(m_entityClass.getSimpleName()));
        return new JpaIterator(readAll, m_batchSize);
        }


    // ---- getters and setters ---------------------------------------------

    /**
     * Return batch size.
     *
     * @return batch size
     */
    public int getBatchSize()
        {
        return m_batchSize;
        }

    /**
     * Set the batch size.
     *
     * @param batchSize batch size to set
     */
    public void setBatchSize(int batchSize)
        {
        m_batchSize = batchSize;
        }


    // ---- helper methods --------------------------------------------------

    /**
     * Create JPA query for retrieving entities of specified name.
     *
     * @param entityName the name of entity
     *
     * @return query for retrieving entities (select/from clause)
     */
    private static String createReadAllQuery(String entityName)
        {
        return "select e from " + entityName + " e";
        }


    // ---- inner class: JpaIterator ----------------------------------------

    /**
     * Iterator to iterate over result of JPA "select" query.
     * <p/>
     * This iterator enables the end user to specify the number of objects to be
     * retrieved with a single query (batch size), in order to manage memory
     * consumption vs. number od executed queries ratio.
     */
    public static class JpaIterator
            implements Iterator
        {

        // ---- constructors --------------------------------------------

        public JpaIterator(Query query, int size)
            {
            m_start   = 0;
            m_size    = size;
            m_query   = query;
            m_partial = new LinkedList();
            }

        /**
         * Determine if there are more objects inside result set.
         *
         * @return <b>true</b> if there are more objects inside result set,
         *         <b>false</b> otherwise.
         */
        public boolean hasNext()
            {
            if (m_partial.isEmpty())
                {
                retrieveResult();
                }
            return !m_partial.isEmpty();
            }

        /**
         * Return the next object inside result set.
         *
         * @return the next object inside result set
         */
        public Object next()
            {
            return m_partial.removeFirst();
            }

        /**
         * Not supported.
         *
         * @throws UnsupportedOperationException always
         */
        public void remove()
            {
            throw new UnsupportedOperationException(
                    "JpaIterator does not support remove operation.");
            }


        // ---- helper methods ------------------------------------------

        /**
         * Retrieve the next partial result (from "m_start" to "m_start +
         * m_size").
         */
        @SuppressWarnings({"unchecked"})
        private void retrieveResult()
            {
            m_partial = new LinkedList(m_query.setFirstResult(m_start)
                    .setMaxResults(m_size).getResultList());
            incrementStep();
            }

        /**
         * Increment partial result step, making sure that next query execution
         * will retrieve next portion of objects within entire result set.
         */
        private void incrementStep()
            {
            m_start += m_size;
            }


        // ---- data members --------------------------------------------

        /**
         * Maximum number of results to retrieve with a single query.
         */
        private final int m_size;

        /**
         * The index of first result to retrieve.
         */
        private int m_start;

        /**
         * Query used to retrieve result.
         */
        private Query m_query;

        /**
         * Partial query result, containing objects retrieved with a single
         * query.
         */
        private LinkedList m_partial;

        }


    // ---- data members ----------------------------------------------------

    /**
     * Default batch size value.
     */
    private static final int BATCH_SIZE = 1000;

    /**
     * Entity manager factory.
     */
    private EntityManagerFactory m_emf;

    /**
     * Entity manager.
     */
    private EntityManager m_em;

    /**
     * The class of entity to load.
     */
    private Class m_entityClass;

    /**
     * The name of persistence unit.
     */
    private String m_persistenceUnitName;

    /**
     * Batch size.
     */
    private int m_batchSize = BATCH_SIZE;
    }
