package sernet.gs.reveng;

// Generated Jun 5, 2015 1:28:34 PM by Hibernate Tools 3.4.0.CR1

import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class StgMbMassnnkatTxt.
 * @see sernet.gs.reveng.StgMbMassnnkatTxt
 * @author Hibernate Tools
 */
public class StgMbMassnnkatTxtHome {

	private static final Log log = LogFactory
			.getLog(StgMbMassnnkatTxtHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(StgMbMassnnkatTxt transientInstance) {
		log.debug("persisting StgMbMassnnkatTxt instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(StgMbMassnnkatTxt instance) {
		log.debug("attaching dirty StgMbMassnnkatTxt instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(StgMbMassnnkatTxt instance) {
		log.debug("attaching clean StgMbMassnnkatTxt instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(StgMbMassnnkatTxt persistentInstance) {
		log.debug("deleting StgMbMassnnkatTxt instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public StgMbMassnnkatTxt merge(StgMbMassnnkatTxt detachedInstance) {
		log.debug("merging StgMbMassnnkatTxt instance");
		try {
			StgMbMassnnkatTxt result = (StgMbMassnnkatTxt) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public StgMbMassnnkatTxt findById(sernet.gs.reveng.StgMbMassnnkatTxtId id) {
		log.debug("getting StgMbMassnnkatTxt instance with id: " + id);
		try {
			StgMbMassnnkatTxt instance = (StgMbMassnnkatTxt) sessionFactory
					.getCurrentSession().get(
							"sernet.gs.reveng.StgMbMassnnkatTxt", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(StgMbMassnnkatTxt instance) {
		log.debug("finding StgMbMassnnkatTxt instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("sernet.gs.reveng.StgMbMassnnkatTxt")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
